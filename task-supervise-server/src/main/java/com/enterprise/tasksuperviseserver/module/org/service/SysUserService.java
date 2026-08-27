package com.enterprise.tasksuperviseserver.module.org.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUser;

/**
 * 系统用户 Service
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
public interface SysUserService {

    /**
     * 分页查询用户列表（不返回 password）
     */
    Page<SysUser> page(int page, int pageSize, String keyword);

    /**
     * 查询用户详情（不返回 password）
     */
    SysUser detail(Long userId);

    /**
     * 新增用户（密码加密）
     */
    SysUser create(SysUser sysUser);

    /**
     * 更新用户（不更新 password）
     */
    SysUser update(SysUser sysUser);

    /**
     * 物理删除用户
     */
    boolean delete(Long userId);

    /**
     * 员工调岗：修改用户所属部门 + 角色
     *
     * @param userId   用户ID
     * @param deptId   新部门ID
     * @param roleCode 新角色编码（可为空，不变）
     */
    void transfer(Long userId, Long deptId, String roleCode);

    /**
     * 员工离职归档：将 status 置为 1 (离职)
     */
    void archive(Long userId);
}
