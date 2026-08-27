package com.enterprise.tasksuperviseserver.module.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.tasksuperviseserver.module.org.dto.LoginByEmailDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.LoginDTO;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUser;
import com.enterprise.tasksuperviseserver.module.org.mapper.SysUserMapper;
import com.enterprise.tasksuperviseserver.module.org.service.AuthService;
import com.enterprise.tasksuperviseserver.module.org.vo.LoginAdminVO;
import com.enterprise.tasksuperviseserver.module.org.vo.LoginResultVO;
import com.enterprise.tasksuperviseserver.module.org.vo.LoginTokenVO;
import com.enterprise.tasksuperviseserver.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /** 开发阶段，允许明文密码匹配 */
    private static final String DEV_DEFAULT_PASSWORD = "123456";

    @Override
    public LoginResultVO login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, dto.getUsername())
                .last("LIMIT 1"));
        if (user == null) {
            return buildFailedLogin("用户不存在");
        }
        if (!validatePassword(dto.getPassword(), user.getPassword())) {
            return buildFailedLogin("密码错误");
        }
        return buildSuccessLogin(user);
    }

    @Override
    public LoginResultVO loginByEmail(LoginByEmailDTO dto) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, dto.getEmail())
                .last("LIMIT 1"));
        if (user == null) {
            return buildFailedLogin("该邮箱未注册");
        }
        if (!validatePassword(dto.getPassword(), user.getPassword())) {
            return buildFailedLogin("密码错误");
        }
        return buildSuccessLogin(user);
    }

    @Override
    public void logout() {
        // JWT 无状态，服务端 logout 留空；如接入 Redis token 黑名单可在此处记录
    }

    @Override
    public SysUser getUserById(Long id) {
        if (id == null) return null;
        return sysUserMapper.selectById(id);
    }

    /**
     * 密码校验：先尝试 BCrypt，失败后再尝试开发阶段明文兜底
     */
    private boolean validatePassword(String rawPassword, String encodedPassword) {
        try {
            if (passwordEncoder.matches(rawPassword, encodedPassword)) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return DEV_DEFAULT_PASSWORD.equals(rawPassword) && rawPassword.equals(encodedPassword);
    }

    private LoginResultVO buildSuccessLogin(SysUser user) {
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getUserName(), user.getRoleCode());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getUserName(), user.getRoleCode());

        LoginAdminVO admin = LoginAdminVO.builder()
                .id(user.getUserId())
                .name(user.getUserName())
                .avatar(user.getAvatar())
                .role(user.getRoleCode())
                .build();

        LoginTokenVO token = LoginTokenVO.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .expires_in(jwtUtil.getAccessExpireSeconds())
                .build();

        return LoginResultVO.builder()
                .status(200)
                .admin(admin)
                .token(token)
                .build();
    }

    private LoginResultVO buildFailedLogin(String message) {
        return LoginResultVO.builder()
                .status(400)
                .message(message)
                .build();
    }
}
