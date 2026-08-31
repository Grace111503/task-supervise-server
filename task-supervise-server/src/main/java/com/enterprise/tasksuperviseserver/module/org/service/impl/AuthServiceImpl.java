package com.enterprise.tasksuperviseserver.module.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.org.dto.LoginByEmailDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.LoginDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.RegisterDTO;
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

import java.time.LocalDateTime;

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
        // 支持用用户名(userName)或真实姓名(name)登录
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .and(w -> w.eq(SysUser::getUserName, dto.getUsername())
                        .or()
                        .eq(SysUser::getName, dto.getUsername()))
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
    public void register(RegisterDTO dto) {
        // 校验密码一致性
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException(400, "两次输入的密码不一致");
        }
        // 检查用户名是否已存在
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, dto.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException(400, "用户名已存在");
        }
        // 检查邮箱是否已注册
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            Long emailCount = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getEmail, dto.getEmail()));
            if (emailCount != null && emailCount > 0) {
                throw new BusinessException(400, "该邮箱已注册");
            }
        }
        // 创建用户（注册默认角色为普通执行人员）
        SysUser user = new SysUser();
        user.setUserName(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName() != null ? dto.getName() : dto.getUsername());
        user.setDeptId(dto.getDeptId());
        user.setPosition(dto.getPosition());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRoleCode("user");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        log.info("用户注册成功: {}, 部门ID: {}, 职位: {}", dto.getUsername(), dto.getDeptId(), dto.getPosition());
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

        // 三级权限角色描述
        String roleDesc = switch (user.getRoleCode()) {
            case "admin" -> "督办管理员";
            case "manager" -> "部门主管";
            case "user" -> "普通执行人员";
            default -> "未知角色";
        };

        LoginAdminVO admin = LoginAdminVO.builder()
                .id(user.getUserId())
                .name(user.getUserName())
                .avatar(user.getAvatar())
                .role(user.getRoleCode())
                .roleDesc(roleDesc)
                .deptId(user.getDeptId())
                .position(user.getPosition())
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
