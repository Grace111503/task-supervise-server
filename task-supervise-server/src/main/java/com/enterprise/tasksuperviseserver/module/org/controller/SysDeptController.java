package com.enterprise.tasksuperviseserver.module.org.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.org.entity.SysDept;
import com.enterprise.tasksuperviseserver.module.org.service.SysDeptService;
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
 * 系统部门接口
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/org/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService sysDeptService;

    /**
     * 分页查询部门列表
     * GET /api/v1/org/dept/page
     */
    @GetMapping("/page")
    public Result<Page<SysDept>> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int pageSize,
                                      @RequestParam(required = false) String keyword) {
        return Result.success(sysDeptService.page(page, pageSize, keyword));
    }

    /**
     * 查询部门详情
     * GET /api/v1/org/dept/{deptId}
     */
    @GetMapping("/{deptId}")
    public Result<SysDept> detail(@PathVariable Long deptId) {
        return Result.success(sysDeptService.detail(deptId));
    }

    /**
     * 新增部门
     * POST /api/v1/org/dept
     */
    @PostMapping
    public Result<SysDept> create(@RequestBody SysDept sysDept) {
        return Result.success("新增成功", sysDeptService.create(sysDept));
    }

    /**
     * 更新部门
     * PUT /api/v1/org/dept
     */
    @PutMapping
    public Result<SysDept> update(@RequestBody SysDept sysDept) {
        return Result.success("更新成功", sysDeptService.update(sysDept));
    }

    /**
     * 删除部门
     * DELETE /api/v1/org/dept/{deptId}
     */
    @DeleteMapping("/{deptId}")
    public Result<Void> delete(@PathVariable Long deptId) {
        sysDeptService.delete(deptId);
        return Result.success("删除成功", null);
    }

    /**
     * 部门树形结构
     * GET /api/v1/org/dept/tree
     */
    @GetMapping("/tree")
    public Result<List<Map<String, Object>>> tree() {
        return Result.success(sysDeptService.tree());
    }
}
