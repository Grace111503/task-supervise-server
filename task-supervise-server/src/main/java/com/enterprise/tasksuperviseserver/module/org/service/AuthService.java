package com.enterprise.tasksuperviseserver.module.org.service;

import com.enterprise.tasksuperviseserver.module.org.dto.LoginByEmailDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.LoginDTO;
import com.enterprise.tasksuperviseserver.module.org.dto.RegisterDTO;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUser;
import com.enterprise.tasksuperviseserver.module.org.vo.LoginResultVO;

/**
 * 认证服务
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface AuthService {

    /**
     * 用户名密码登录
     */
    LoginResultVO login(LoginDTO dto);

    /**
     * 邮箱登录
     */
    LoginResultVO loginByEmail(LoginByEmailDTO dto);

    /**
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 根据ID获取用户实体
     */
    SysUser getUserById(Long id);
}
