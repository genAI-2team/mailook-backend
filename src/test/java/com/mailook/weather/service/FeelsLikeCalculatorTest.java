package com.mailook.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

// 기상청 체감온도 식. Spring 없이 new 로 검사한다.
class FeelsLikeCalculatorTest {

    private final FeelsLikeCalculator calculator = new FeelsLikeCalculator();

    // 한겨울 기온 0°, 바람 5m/s. 바람 때문에 영하로 내려간다
    @Test
    void winter_freezingWithWind() {
        assertEquals(-4.94, calculator.calc(0, 5, 40, 1), 0.01);
    }

    // 겨울이어도 기온이 10°를 넘거나 바람이 약하면 기온을 그대로 쓴다
    @Test
    void winter_usesTemperatureWhenFormulaDoesNotApply() {
        assertEquals(15, calculator.calc(15, 5, 40, 1), 1e-9);
        assertEquals(0, calculator.calc(0, 1, 40, 12), 1e-9);
    }

    // 한여름 기온 33°, 습도 60%. 습도 때문에 기온보다 조금 더 덥다
    @Test
    void summer_humidAfternoon() {
        assertEquals(33.45, calculator.calc(33, 0, 60, 7), 0.01);
    }
}
