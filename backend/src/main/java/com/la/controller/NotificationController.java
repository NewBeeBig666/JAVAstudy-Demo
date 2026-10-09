package com.la.controller;

import com.la.dto.ApiResponse;
import com.la.entity.Notification;
import com.la.security.CurrentUser;
import com.la.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "通知")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "通知列表")
    @GetMapping
    public ApiResponse<List<Notification>> list() {
        return ApiResponse.ok(notificationService.list(CurrentUser.id()));
    }

    @Operation(summary = "标记已读")
    @PutMapping("/{id}/read")
    public ApiResponse<Void> read(@PathVariable Long id) {
        notificationService.markRead(CurrentUser.id(), id);
        return ApiResponse.ok();
    }
}
