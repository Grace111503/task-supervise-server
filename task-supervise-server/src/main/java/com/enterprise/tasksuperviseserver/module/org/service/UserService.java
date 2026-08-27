package com.enterprise.tasksuperviseserver.module.org.service;

import com.enterprise.tasksuperviseserver.module.org.dto.ChangePasswordDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.UpdateUserInfoDTO;
import com.enterprise.tasksuperviseserver.module.org.vo.UserInfoVO;

/**
 * 用户个人信息服务（当前登录用户的资料管理）
 */
public interface UserService {

    /**
     * 获取当前登录用户信息
     */
    UserInfoVO getUserInfo();

    /**
     * 获取指定用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 更新当前登录用户信息
     */
    UserInfoVO updateUserInfo(UpdateUserInfoDTO dto);

    /**
     * 修改密码
     */
    void changePassword(ChangePasswordDTO dto);
}
