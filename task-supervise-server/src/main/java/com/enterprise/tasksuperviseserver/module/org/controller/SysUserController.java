package com.enterprise.tasksuperviseserver.module.org.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUser;
import com.enterprise.tasksuperviseserver.module.org.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统用户接口
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/org/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 分页查询用户列表
     * GET /api/v1/org/user/page
     */
    @GetMapping("/page")
    public Result<Page<SysUser>> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int pageSize,
                                      @RequestParam(required = false) String keyword) {
        return Result.success(sysUserService.page(page, pageSize, keyword));
    }

    /**
     * 查询用户详情
     * GET /api/v1/org/user/{userId}
     */
    @GetMapping("/{userId}")
    public Result<SysUser> detail(@PathVariable Long userId) {
        return Result.success(sysUserService.detail(userId));
    }

    /**
     * 新增用户
     * POST /api/v1/org/user
     */
    @PostMapping
    public Result<SysUser> create(@RequestBody SysUser sysUser) {
        return Result.success("新增成功", sysUserService.create(sysUser));
    }

    /**
     * 更新用户
     * PUT /api/v1/org/user
     */
    @PutMapping
    public Result<SysUser> update(@RequestBody SysUser sysUser) {
        return Result.success("更新成功", sysUserService.update(sysUser));
    }

    /**
     * 删除用户
     * DELETE /api/v1/org/user/{userId}
     */
    @DeleteMapping("/{userId}")
    public Result<Void> delete(@PathVariable Long userId) {
        sysUserService.delete(userId);
        return Result.success("删除成功", null);
    }

    /**
     * 员工调岗
     * PUT /api/v1/org/user/{userId}/transfer
     * body: { deptId, roleCode? }
     */
    @PutMapping("/{userId}/transfer")
    public Result<Void> transfer(@PathVariable Long userId, @RequestBody java.util.Map<String, Object> body) {
        Long deptId = body.get("deptId") != null ? ((Number) body.get("deptId")).longValue() : null;
        String roleCode = body.get("roleCode") != null ? body.get("roleCode").toString() : null;
        sysUserService.transfer(userId, deptId, roleCode);
        return Result.success("调岗成功", null);
    }

    /**
     * 员工离职归档
     * PUT /api/v1/org/user/{userId}/archive
     */
    @PutMapping("/{userId}/archive")
    public Result<Void> archive(@PathVariable Long userId) {
        sysUserService.archive(userId);
        return Result.success("归档成功", null);
    }
}
