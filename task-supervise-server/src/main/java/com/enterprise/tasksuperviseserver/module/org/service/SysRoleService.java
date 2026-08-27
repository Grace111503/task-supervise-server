package com.enterprise.tasksuperviseserver.module.org.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.org.entity.SysRole;

import java.util.List;

/**
 * 系统角色 Service
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
public interface SysRoleService {

    /**
     * 分页查询角色列表
     */
    Page<SysRole> page(int page, int pageSize, String keyword);

    /**
     * 查询角色详情
     */
    SysRole detail(Long roleId);

    /**
     * 新增角色
     */
    SysRole create(SysRole sysRole);

    /**
     * 更新角色
     */
    SysRole update(SysRole sysRole);

    /**
     * 物理删除角色
     */
    boolean delete(Long roleId);

    /**
     * 分配角色权限给用户
     * （先删除用户所有旧角色，再插入新的；并同步更新 sys_user.role_code）
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assign(Long userId, List<Long> roleIds);

    /**
     * 查询角色列表（不分页，用于下拉选项）
     */
    List<SysRole> list(String keyword);
}
