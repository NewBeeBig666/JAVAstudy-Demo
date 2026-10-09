package com.la.controller;

import com.la.dto.ApiResponse;
import com.la.security.CurrentUser;
import com.la.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "学习报告")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "学生画像总览（streak/weekHours/avgMastery/趋势/复习队列/错题本）")
    @GetMapping("/overview")
    public ApiResponse<Map<String, Object>> overview() {
        return ApiResponse.ok(reportService.overview(CurrentUser.id()));
    }

    @Operation(summary = "掌握度明细")
    @GetMapping("/mastery")
    public ApiResponse<List<Map<String, Object>>> mastery() {
        return ApiResponse.ok(reportService.masteryDetail(CurrentUser.id()));
    }
}
