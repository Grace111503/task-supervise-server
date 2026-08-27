package com.enterprise.tasksuperviseserver.module.org.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.org.entity.SysRole;
import com.enterprise.tasksuperviseserver.module.org.service.SysRoleService;
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

import java.util.List;
import java.util.Map;

/**
 * 系统角色接口
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/org/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 分页查询角色列表
     * GET /api/v1/org/role/page
     */
    @GetMapping("/page")
    public Result<Page<SysRole>> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int pageSize,
                                      @RequestParam(required = false) String keyword) {
        return Result.success(sysRoleService.page(page, pageSize, keyword));
    }

    /**
     * 查询角色详情
     * GET /api/v1/org/role/{roleId}
     */
    @GetMapping("/{roleId}")
    public Result<SysRole> detail(@PathVariable Long roleId) {
        return Result.success(sysRoleService.detail(roleId));
    }

    /**
     * 新增角色
     * POST /api/v1/org/role
     */
    @PostMapping
    public Result<SysRole> create(@RequestBody SysRole sysRole) {
        return Result.success("新增成功", sysRoleService.create(sysRole));
    }

    /**
     * 更新角色
     * PUT /api/v1/org/role
     */
    @PutMapping
    public Result<SysRole> update(@RequestBody SysRole sysRole) {
        return Result.success("更新成功", sysRoleService.update(sysRole));
    }

    /**
     * 删除角色
     * DELETE /api/v1/org/role/{roleId}
     */
    @DeleteMapping("/{roleId}")
    public Result<Void> delete(@PathVariable Long roleId) {
        sysRoleService.delete(roleId);
        return Result.success("删除成功", null);
    }

    /**
     * 查询角色列表（不分页，用于下拉选项）
     * GET /api/v1/org/role/list
     */
    @GetMapping("/list")
    public Result<List<SysRole>> list(@RequestParam(required = false) String keyword) {
        return Result.success(sysRoleService.list(keyword));
    }

    /**
     * 分配角色给用户
     * POST /api/v1/org/role/assign
     * body: { userId, roleIds: [1,2,3] }
     */
    @PostMapping("/assign")
    public Result<Void> assign(@RequestBody Map<String, Object> body) {
        Long userId = ((Number) body.get("userId")).longValue();
        @SuppressWarnings("unchecked")
        List<Long> roleIds = ((List<Number>) body.get("roleIds"))
                .stream().map(Number::longValue).toList();
        sysRoleService.assign(userId, roleIds);
        return Result.success("分配成功", null);
    }
}
