package com.mailook.warmth.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.mailook.warmth.dto.MomentWarmth;
import com.mailook.warmth.dto.WarmthResult;
import com.mailook.weather.dto.OutdoorMoment;
import org.springframework.stereotype.Component;

// 숫자만 받아 숫자만 돌려주는 보온 계산. 추천과 피드백이 같이 쓴다.
// DAO, DB, 외부 API, LLM은 부르지 않는다.
@Component
public class WarmthCalculator {

    // W/m², 걷기 약 2 met (ASHRAE 55)
    private static final double ACTIVITY_OUTDOOR = 115;
    // W/m², 앉아 있기 약 1 met
    private static final double ACTIVITY_INDOOR = 60;
    // 반팔+반바지. 적정 clo의 하한
    private static final double MIN_REQUIRED_CLO = 0.36;
    // 적정 clo 식의 기준 상수
    private static final double BASE_TEMP = 31;
    // 1 clo = 0.155 m²K/W
    private static final double CLO_FACTOR = 0.155;
    // 추위 보정과 더위 보정을 나누는 체감 기준(°)
    private static final double SPLIT_TEMP = 20;
    // 적용 비율 분모. |체감 − 20| 을 이 값으로 나눈 뒤 1을 넘기지 않는다
    private static final double RATIO_RANGE = 4;
    // 몸이 안 좋은 날, 기준 온도에서 빼는 폭(°)
    private static final double UNWELL_SHIFT = 4;
    // 실내 검사 허용 폭(±°)
    private static final double INDOOR_TOLERANCE = 3;
    // 일교차가 크다고 보는 체감 차이(°)
    private static final double LARGE_RANGE = 10;

    // 조합 하나의 보온을 그날 밖에 있는 시간 모두에 대해 검사한다.
    // outfitClos: 입는 옷(WEAR) clo 목록. 챙길 것(BRING)은 넣지 않는다
    // innerClos: 아우터·액세서리를 뺀 옷 clo 목록. 실내 검사용
    // moments: 밖에 있는 시간. 비어 있으면 IllegalArgumentException
    // indoorTemps: 머무는 장소의 실내 온도(HOT 26 / NORMAL 22 / COLD 18). 비어 있으면 indoorOk = true
    // coldOffset: 추위 보정 −5 ~ +5. +면 따뜻하게
    // heatOffset: 더위 보정 −5 ~ +5
    // unwell: 몸이 안 좋은 날
    public WarmthResult calcOutfitWarmth(List<Double> outfitClos, List<Double> innerClos,
            List<OutdoorMoment> moments, List<Double> indoorTemps,
            double coldOffset, double heatOffset, boolean unwell) {

        if (moments.isEmpty()) throw new IllegalArgumentException("밖에 있는 시간이 없습니다.");

        double outfitClo = calcOutfitClo(outfitClos);
        List<MomentWarmth> momentWarmthList = new ArrayList<>();
        for (OutdoorMoment moment : moments) {
            momentWarmthList.add(calcMomentWarmth(moment, outfitClo, coldOffset, heatOffset, unwell));
        }

        List<MomentWarmth> marked = markDecidingMoment(momentWarmthList, moments);
        marked.sort(Comparator.comparingInt(MomentWarmth::getSeq));

        double innerClo = calcOutfitClo(innerClos);
        boolean indoorOk = checkIndoor(innerClo, indoorTemps);
        boolean largeTempRange = isLargeTempRange(moments);
        return new WarmthResult(roundClo(outfitClo), marked, indoorOk, largeTempRange);
    }

    // 결과에 담는 조합 clo만 소수 둘째 자리
    private double roundClo(double clo) {
        return Math.round(clo * 100.0) / 100.0;
    }

    // 조합 clo = 0.835 × (clo 합) + 0.16 (ISO 9920). 빈 목록이면 0.16
    // 옷들의 clo를 합계하여 return 한다.
    public double calcOutfitClo(List<Double> clos) {
        if(clos.isEmpty()) return 0.16;
        double sum = 0;
        for(Double clo : clos) {
            sum += clo;
        }
        return (sum * 0.835) + 0.16;
    }

    // 이 조합이 맞는 온도 = 31 − 0.155 × 활동량 × 조합 clo. 피드백 보정 계산용
    public double calcComfortTemp(double outfitClo, double activity) {
        return BASE_TEMP - (CLO_FACTOR *  activity * outfitClo);
    }

    // 개인의 온도 보정을 얼마나 반영할지에 대한 비율
    // 적용 비율 = min(1, |체감 − 20| ÷ 4)
    public double calcApplyRatio(double feelsLike) {
        // min(1, |체감 − 20| ÷ 4)
        double diff = Math.abs(feelsLike -  SPLIT_TEMP);

        return Math.min(1, diff / RATIO_RANGE);
    }

    // 체감 20° 미만이면 coldOffset, 20° 이상이면 heatOffset
    public double selectOffset(double feelsLike, double coldOffset, double heatOffset) {
        if(feelsLike < SPLIT_TEMP) return coldOffset;
        return heatOffset;
    }

    // 적정 clo = (31 − 기준 온도) ÷ (0.155 × 활동량), 최소 0.36
    // 기준 온도 = 체감 − 보정 × 적용 비율. unwell이면 여기서 4°를 더 뺀다
    // 적용 비율은 보정 전 체감으로 구한다
    private double calcRequiredClo(double feelsLike, double offset, double activity, boolean unwell) {
        double standard = feelsLike - (offset * calcApplyRatio(feelsLike));
        if(unwell) { standard -= UNWELL_SHIFT; }

        return Math.max((BASE_TEMP - standard) / (CLO_FACTOR * activity), MIN_REQUIRED_CLO);
    }

    // 해당 시점에 필요되는 clo를 구하는 계산식
    // selectOffset 후 바깥 활동량으로 적정 clo를 구하고, diff = outfitClo − requiredClo
    private MomentWarmth calcMomentWarmth(OutdoorMoment moment, double outfitClo,
            double coldOffset, double heatOffset, boolean unwell) {

        double corrections = selectOffset(moment.getFeelsLike(), coldOffset, heatOffset);
        double requiredClo = calcRequiredClo(moment.getFeelsLike(), corrections, ACTIVITY_OUTDOOR, unwell);
        double diff = outfitClo - requiredClo;
        return new MomentWarmth(moment.getSeq(), requiredClo, diff, false);
    }

    // 기준 시점은 하루에 하나로 고정
    // 체감 20° 미만인 시점이 하나라도 있으면 적정 clo가 가장 높은 시점.
    // 모두 20° 이상이면 체감이 가장 높은 시점.
    // 같은 값이면 seq가 작은 쪽.
    private List<MomentWarmth> markDecidingMoment(List<MomentWarmth> list, List<OutdoorMoment> moments) {
        boolean hasCold = false;
        for (OutdoorMoment moment : moments) {
            if (moment.getFeelsLike() < SPLIT_TEMP) {
                hasCold = true;
                break;
            }
        }

        int decidingSeq;
        if (hasCold) {
            MomentWarmth picked = list.getFirst();
            for (MomentWarmth item : list) {
                if (item.getRequiredClo() > picked.getRequiredClo()) {
                    picked = item;
                } else if (item.getRequiredClo() == picked.getRequiredClo() && item.getSeq() < picked.getSeq()) {
                    picked = item;
                }
            }
            decidingSeq = picked.getSeq();
        } else {
            OutdoorMoment picked = moments.getFirst();
            for (OutdoorMoment moment : moments) {
                if (moment.getFeelsLike() > picked.getFeelsLike()) {
                    picked = moment;
                } else if (moment.getFeelsLike() == picked.getFeelsLike() && moment.getSeq() < picked.getSeq()) {
                    picked = moment;
                }
            }
            decidingSeq = picked.getSeq();
        }

        List<MomentWarmth> result = new ArrayList<>();
        for (MomentWarmth item : list) {
            result.add(new MomentWarmth(
                    item.getSeq(), item.getRequiredClo(), item.getDiff(), item.getSeq() == decidingSeq));
        }
        return result;
    }

    // 실내 온도마다 실내 활동량으로 구한 적정 온도와 비교한다.
    // 차이가 ±3° 안이면 그 장소는 통과한다.
    private boolean checkIndoor(double innerClo, List<Double> indoorTemps) {
        if(indoorTemps.isEmpty()) return true;

        double correctTemp = calcComfortTemp(innerClo, ACTIVITY_INDOOR);
        for(double indoorTemp : indoorTemps) {
            if(Math.abs(correctTemp - indoorTemp) > INDOOR_TOLERANCE) return false;
        }

        return true;
    }

    // 시점 체감의 최고 − 최저가 10° 이상이면 true
    private boolean isLargeTempRange(List<OutdoorMoment> moments) {
        if(moments.isEmpty()) return false;

        double highest = moments.getFirst().getFeelsLike();
        double lowest = moments.getFirst().getFeelsLike();

        for(OutdoorMoment moment : moments) {
            highest = Math.max(moment.getFeelsLike(), highest);
            lowest = Math.min(moment.getFeelsLike(), lowest);
        }

        return highest - lowest >= LARGE_RANGE;
    }
}
