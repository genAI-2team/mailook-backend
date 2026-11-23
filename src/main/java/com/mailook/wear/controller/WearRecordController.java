package com.mailook.wear.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/wear-records")
@RestController
@Slf4j
public class WearRecordController {

    // 착용 기록 등록
    @PostMapping
    public void createWearRecord() {
    }

    // 오늘 착용 기록 조회
    @GetMapping("/today")
    public void getTodayWearRecords() {
    }

    // 착용 기록 삭제
    @DeleteMapping("/{wearRecordId}")
    public void deleteWearRecord(@PathVariable("wearRecordId") Long wearRecordId) {
    }

    // 기간별 착용 기록 조회
    @GetMapping
    public void getWearRecords(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
    }
}
