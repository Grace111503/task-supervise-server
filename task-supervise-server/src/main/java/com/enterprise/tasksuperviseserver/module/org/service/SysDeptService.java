package com.enterprise.tasksuperviseserver.module.org.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.org.entity.SysDept;

import java.util.List;
import java.util.Map;

/**
 * 系统部门 Service
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
public interface SysDeptService {

    /**
     * 分页查询部门列表
     */
    Page<SysDept> page(int page, int pageSize, String keyword);

    /**
     * 查询部门详情
     */
    SysDept detail(Long deptId);

    /**
     * 新增部门
     */
    SysDept create(SysDept sysDept);

    /**
     * 更新部门
     */
    SysDept update(SysDept sysDept);

    /**
     * 物理删除部门
     */
    boolean delete(Long deptId);

    /**
     * 部门树形结构
     *
     * @return 根节点列表，每个节点 { deptId, parentId, deptName, sort, status, children: [...] }
     */
    List<Map<String, Object>> tree();
}
