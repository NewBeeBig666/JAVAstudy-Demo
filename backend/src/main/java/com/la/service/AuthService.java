package com.la.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.la.dto.request.LoginRequest;
import com.la.dto.request.RegisterRequest;
import com.la.dto.response.AuthResponse;
import com.la.entity.ClassEntity;
import com.la.entity.User;
import com.la.exception.BizException;
import com.la.mapper.ClassMapper;
import com.la.mapper.UserMapper;
import com.la.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final ClassMapper classMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (exists > 0) {
            throw new BizException("用户名已被注册");
        }
        String role = "TEACHER".equals(req.getRole()) ? "TEACHER" : "STUDENT";
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRealName(req.getRealName());
        user.setRole(role);
        user.setStudentNo(req.getStudentNo());
        user.setClassId(req.getClassId());
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return buildResponse(user, false);
    }

    public AuthResponse login(LoginRequest req) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BizException("用户名或密码错误");
        }
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);
        return buildResponse(user, true);
    }

    public AuthResponse me(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(401, "用户不存在");
        }
        return buildResponse(user, false);
    }

    private AuthResponse buildResponse(User user, boolean withToken) {
        String className = null;
        if (user.getClassId() != null) {
            ClassEntity cls = classMapper.selectById(user.getClassId());
            className = cls != null ? cls.getName() : null;
        }
        String token = withToken ? jwtUtil.generate(user.getId(), user.getRole()) : null;
        return new AuthResponse(token, withToken ? 12 * 3600L : null,
                new AuthResponse.UserInfo(user.getId(), user.getUsername(), user.getRealName(), user.getRole(), className));
    }
}
