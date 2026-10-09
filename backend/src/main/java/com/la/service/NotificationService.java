package com.la.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.la.entity.Notification;
import com.la.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;

    public List<Notification> list(Long userId) {
        return notificationMapper.selectList(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt)
                .last("LIMIT 30"));
    }

    public void markRead(Long userId, Long id) {
        notificationMapper.update(null, new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getId, id).eq(Notification::getUserId, userId)
                .set(Notification::getReadFlag, true));
    }

    public void push(Long userId, String content, String type) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setContent(content);
        n.setType(type);
        n.setReadFlag(false);
        n.setCreatedAt(LocalDateTime.now());
        notificationMapper.insert(n);
    }
}
