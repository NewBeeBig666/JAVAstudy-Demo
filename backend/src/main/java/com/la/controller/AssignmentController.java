package com.la.controller;

import com.la.dto.ApiResponse;
import com.la.security.CurrentUser;
import com.la.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "作业（学生侧）")
@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @Operation(summary = "我的作业列表（含本人提交状态）")
    @GetMapping("/my")
    public ApiResponse<List<Map<String, Object>>> my() {
        return ApiResponse.ok(assignmentService.myAssignments(CurrentUser.id()));
    }

    @Operation(summary = "提交作业（AI 初评异步进行，完成后通知）")
    @PostMapping("/{id}/submissions")
    public ApiResponse<Map<String, Object>> submit(@PathVariable Long id, @RequestBody SubmitRequest req) {
        return ApiResponse.ok(assignmentService.submit(CurrentUser.id(), id, req.getCode()));
    }

    @Data
    public static class SubmitRequest {
        @NotBlank(message = "代码不能为空")
        private String code;
    }
}
