package com.enterprise.tasksuperviseserver.module.org.controller;

import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.org.dto.LoginByEmailDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.LoginDTO;
import com.enterprise.tasksuperviseserver.module.org.service.AuthService;
import com.enterprise.tasksuperviseserver.module.org.vo.LoginResultVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户名密码登录
     */
    @PostMapping("/login")
    public Result<LoginResultVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginResultVO result = authService.login(dto);
        return Result.success(result);
    }

    /**
     * 邮箱登录
     */
    @PostMapping("/login/email")
    public Result<LoginResultVO> loginByEmail(@Valid @RequestBody LoginByEmailDTO dto) {
        LoginResultVO result = authService.loginByEmail(dto);
        return Result.success(result);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
