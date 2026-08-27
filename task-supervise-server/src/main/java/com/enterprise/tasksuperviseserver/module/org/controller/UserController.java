package com.enterprise.tasksuperviseserver.module.org.controller;

import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.org.dto.ChangePasswordDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.UpdateUserInfoDTO;
import com.enterprise.tasksuperviseserver.module.org.service.UserService;
import com.enterprise.tasksuperviseserver.module.org.vo.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户个人信息接口
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        return Result.success(userService.getUserInfo());
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/info")
    public Result<UserInfoVO> updateUserInfo(@RequestBody UpdateUserInfoDTO dto) {
        return Result.success(userService.updateUserInfo(dto));
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return Result.success();
    }
}
