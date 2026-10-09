package com.la.controller;

import com.la.dto.ApiResponse;
import com.la.dto.response.ReviewItemDto;
import com.la.security.CurrentUser;
import com.la.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "复习队列（SM-2）")
@RestController
@RequestMapping("/api/review-queue")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "今日复习队列")
    @GetMapping("/today")
    public ApiResponse<List<ReviewItemDto>> today() {
        return ApiResponse.ok(reviewService.today(CurrentUser.id()));
    }

    @Operation(summary = "全部待复习项（含未来）")
    @GetMapping("/all")
    public ApiResponse<List<ReviewItemDto>> all() {
        return ApiResponse.ok(reviewService.all(CurrentUser.id()));
    }

    @Operation(summary = "复习完成（quality 0-5，SM-2 更新下次时间）")
    @PostMapping("/{id}/complete")
    public ApiResponse<ReviewItemDto> complete(@PathVariable Long id, @RequestBody CompleteRequest req) {
        return ApiResponse.ok(reviewService.complete(CurrentUser.id(), id,
                req.getQuality() != null ? req.getQuality() : 3));
    }

    @Data
    public static class CompleteRequest {
        private Integer quality;
    }
}
