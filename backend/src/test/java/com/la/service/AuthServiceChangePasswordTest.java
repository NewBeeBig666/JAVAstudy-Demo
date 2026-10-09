package com.la.service;

import com.la.dto.request.ChangePasswordRequest;
import com.la.entity.User;
import com.la.exception.BizException;
import com.la.mapper.ClassMapper;
import com.la.mapper.UserMapper;
import com.la.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * 修改密码功能单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceChangePasswordTest {

    @Mock private UserMapper userMapper;
    @Mock private ClassMapper classMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService service;

    private User user() {
        User u = new User();
        u.setId(15L);
        u.setUsername("2023318253");
        u.setPasswordHash("$2a$10$oldhash");
        return u;
    }

    private ChangePasswordRequest req(String oldP, String newP) {
        ChangePasswordRequest r = new ChangePasswordRequest();
        r.setOldPassword(oldP);
        r.setNewPassword(newP);
        return r;
    }

    @Test
    void changePassword_success_rehashAndSave() {
        when(userMapper.selectById(15L)).thenReturn(user());
        when(passwordEncoder.matches("old123", "$2a$10$oldhash")).thenReturn(true);
        when(passwordEncoder.encode("new456")).thenReturn("$2a$10$newhash");

        service.changePassword(15L, req("old123", "new456"));

        verify(userMapper).updateById(argThat((User u) -> "$2a$10$newhash".equals(u.getPasswordHash())));
    }

    @Test
    void changePassword_wrongOldPassword_rejected() {
        when(userMapper.selectById(15L)).thenReturn(user());
        when(passwordEncoder.matches("bad", "$2a$10$oldhash")).thenReturn(false);

        BizException e = assertThrows(BizException.class,
                () -> service.changePassword(15L, req("bad", "new456")));
        assertEquals("原密码不正确", e.getMessage());
        verify(userMapper, never()).updateById(any(User.class));
    }

    @Test
    void changePassword_sameAsOld_rejected() {
        when(userMapper.selectById(15L)).thenReturn(user());
        when(passwordEncoder.matches("same123", "$2a$10$oldhash")).thenReturn(true);

        BizException e = assertThrows(BizException.class,
                () -> service.changePassword(15L, req("same123", "same123")));
        assertEquals("新密码不能与原密码相同", e.getMessage());
        verify(userMapper, never()).updateById(any(User.class));
    }
}
