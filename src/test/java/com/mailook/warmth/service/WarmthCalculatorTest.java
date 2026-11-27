package com.mailook.warmth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import com.mailook.warmth.dto.MomentWarmth;
import com.mailook.warmth.dto.WarmthResult;
import com.mailook.weather.dto.OutdoorMoment;
import org.junit.jupiter.api.Test;

// Spring 컨텍스트 없이 new WarmthCalculator()로 검사한다.
class WarmthCalculatorTest {

    private final WarmthCalculator calculator = new WarmthCalculator();

    // 셔츠(0.2)와 바지(0.5)를 겹쳐 입은 날. 계산은 0.7445, 결과에 실을 때만 0.74
    @Test
    void calcOutfitClo_twoItems() {
        assertEquals(0.7445, calculator.calcOutfitClo(List.of(0.2, 0.5)), 1e-9);

        // 체감 20°인 밖에 있는 시간 하나. 조합 clo 반올림만 본다
        WarmthResult result = warmth(List.of(0.2, 0.5), List.of(), List.of(moment(1, 20)), List.of(), 0, 0, false);
        assertEquals(0.74, result.getOutfitClo(), 1e-9);
    }

    // 개인 보정을 얼마나 쓸지. 20°면 안 쓰고, 2° 차이면 절반, 4° 이상이면 추위·더위 모두 전부
    @Test
    void calcApplyRatio_boundaries() {
        assertEquals(0, calculator.calcApplyRatio(20), 1e-9); // 딱 20°, 보정 없음
        assertEquals(0.5, calculator.calcApplyRatio(18), 1e-9); // 살짝 쌀쌀, 절반만
        assertEquals(1, calculator.calcApplyRatio(10), 1e-9); // 추운 날, 추위 보정 전부
        assertEquals(1, calculator.calcApplyRatio(30), 1e-9); // 더운 날, 더위 보정 전부
    }

    // 한낮 체감 30°. 더위를 보통으로 타도 반팔+반바지(0.36)보다 얇게 요구하지 않는다
    @Test
    void requiredClo_minimum() {
        assertEquals(0.36, single(30, 0, 0, false).getRequiredClo(), 1e-9);
    }

    // 한파 체감 0°, 추위를 보통으로 타는 사람. 밖에 있는 시간 하나에 두꺼운 옷이 필요하다
    @Test
    void requiredClo_freezingWithoutOffset() {
        assertEquals(31.0 / (0.155 * 115), single(0, 0, 0, false).getRequiredClo(), 1e-9);
    }

    // 같은 한파인데 추위를 많이 타는 사람(+2°). 바로 위보다 더 따뜻하게 입어야 한다
    @Test
    void requiredClo_coldOffsetRaisesClo() {
        double usual = single(0, 0, 0, false).getRequiredClo();
        double runsCold = single(0, 2, 0, false).getRequiredClo();

        assertEquals(33.0 / (0.155 * 115), runsCold, 1e-9);
        assertTrue(runsCold > usual);
    }

    // 같은 한파인데 오늘 몸이 안 좋다. 기준 온도를 4° 낮춰 더 따뜻하게 계산한다
    @Test
    void requiredClo_unwellLowersBaseTemp() {
        assertEquals(35.0 / (0.155 * 115), single(0, 0, 0, true).getRequiredClo(), 1e-9);
    }

    // 출근 5°, 낮 12°, 퇴근 22°. 쌀쌀한 시간이 있으면 가장 두껍게 입어야 하는 출근만 기준
    @Test
    void decidingMoment_coldestWhenAnyBelowSplit() {
        WarmthResult result = warmth(
                List.of(), List.of(),
                List.of(moment(1, 5), moment(2, 12), moment(3, 22)),
                List.of(), 0, 0, false);

        assertDecidingOnly(result, 1); // 출근
    }

    // 오전 24°, 한낮 31°, 저녁 27°. 하루가 전부 더우면 가장 더운 한낮만 기준
    @Test
    void decidingMoment_hottestWhenAllWarm() {
        WarmthResult result = warmth(
                List.of(), List.of(),
                List.of(moment(1, 24), moment(2, 31), moment(3, 27)),
                List.of(), 0, 0, false);

        assertDecidingOnly(result, 2); // 한낮
    }

    // 아침과 낮의 체감 차이가 10° 이상이면 겉옷을 벗었다 입는 날
    @Test
    void largeTempRange() {
        // 아침 8°, 낮 19°. 차이가 11°라 겉옷이 필요하다
        WarmthResult wide = warmth(
                List.of(), List.of(),
                List.of(moment(1, 8), moment(2, 19)),
                List.of(), 0, 0, false);
        // 오후 15°, 저녁 22°. 차이가 7°라 그냥 따뜻한 날
        WarmthResult narrow = warmth(
                List.of(), List.of(),
                List.of(moment(1, 15), moment(2, 22)),
                List.of(), 0, 0, false);

        assertTrue(wide.isLargeTempRange());
        assertFalse(narrow.isLargeTempRange());
    }

    // 아우터를 벗고 실내에 있을 때. 방 온도와 맞는 온도 차이가 3° 안이면 괜찮다
    @Test
    void indoorCheck() {
        // 속옷만 있는 조합이 앉아서 맞는 온도
        double comfort = calculator.calcComfortTemp(calculator.calcOutfitClo(List.of()), 60);
        List<OutdoorMoment> moments = List.of(moment(1, 20));

        assertTrue(warmth(List.of(), List.of(), moments, List.of(), 0, 0, false).isIndoorOk()); // 갈 실내가 없음
        assertTrue(warmth(List.of(), List.of(), moments, List.of(comfort + 3), 0, 0, false).isIndoorOk()); // 방이 딱 3° 다름
        assertFalse(warmth(List.of(), List.of(), moments, List.of(comfort + 4), 0, 0, false).isIndoorOk()); // 방이 4° 달라 실패
    }

    // 밖에 있는 시간이 하나도 없으면 옷을 계산하지 않는다
    @Test
    void emptyMoments_throws() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> warmth(List.of(), List.of(), List.of(), List.of(), 0, 0, false));

        assertEquals("밖에 있는 시간이 없습니다.", error.getMessage());
    }

    private OutdoorMoment moment(int seq, double feelsLike) {
        return new OutdoorMoment(seq, feelsLike);
    }

    private WarmthResult warmth(List<Double> outfitClos, List<Double> innerClos, List<OutdoorMoment> moments,
            List<Double> indoorTemps, double coldOffset, double heatOffset, boolean unwell) {
        return calculator.calcOutfitWarmth(outfitClos, innerClos, moments, indoorTemps, coldOffset, heatOffset, unwell);
    }

    private MomentWarmth single(double feelsLike, double coldOffset, double heatOffset, boolean unwell) {
        return warmth(List.of(), List.of(), List.of(moment(1, feelsLike)), List.of(), coldOffset, heatOffset, unwell)
                .getMoments().getFirst();
    }

    private void assertDecidingOnly(WarmthResult result, int decidingSeq) {
        int decidingCount = 0;
        for (MomentWarmth item : result.getMoments()) {
            if (item.isDeciding()) {
                decidingCount++;
                assertEquals(decidingSeq, item.getSeq());
            }
        }
        assertEquals(1, decidingCount);
    }
}
