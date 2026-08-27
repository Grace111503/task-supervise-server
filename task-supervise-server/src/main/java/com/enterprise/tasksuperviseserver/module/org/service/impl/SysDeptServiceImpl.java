package com.enterprise.tasksuperviseserver.module.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.org.entity.SysDept;
import com.enterprise.tasksuperviseserver.module.org.mapper.SysDeptMapper;
import com.enterprise.tasksuperviseserver.module.org.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统部门 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;

    @Override
    public Page<SysDept> page(int page, int pageSize, String keyword) {
        Page<SysDept> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysDept::getDeptName, keyword);
        }
        wrapper.orderByDesc(SysDept::getCreateTime);
        return sysDeptMapper.selectPage(p, wrapper);
    }

    @Override
    public SysDept detail(Long deptId) {
        SysDept sysDept = sysDeptMapper.selectById(deptId);
        if (sysDept == null) {
            throw new BusinessException(404, "部门不存在");
        }
        return sysDept;
    }

    @Override
    public SysDept create(SysDept sysDept) {
        sysDept.setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        sysDeptMapper.insert(sysDept);
        return sysDept;
    }

    @Override
    public SysDept update(SysDept sysDept) {
        SysDept existing = sysDeptMapper.selectById(sysDept.getDeptId());
        if (existing == null) {
            throw new BusinessException(404, "部门不存在");
        }
        sysDept.setUpdateTime(LocalDateTime.now());
        sysDeptMapper.updateById(sysDept);
        return sysDeptMapper.selectById(sysDept.getDeptId());
    }

    @Override
    public boolean delete(Long deptId) {
        return sysDeptMapper.deleteById(deptId) > 0;
    }

    @Override
    public List<Map<String, Object>> tree() {
        List<SysDept> all = sysDeptMapper.selectList(new LambdaQueryWrapper<SysDept>()
                .orderByAsc(SysDept::getSort));

        Map<Long, List<SysDept>> byParent = all.stream()
                .collect(Collectors.groupingBy(d -> d.getParentId() == null ? 0L : d.getParentId()));

        // 根节点：parentId = null 或 0
        List<SysDept> roots = new ArrayList<>();
        for (SysDept d : all) {
            if (d.getParentId() == null || d.getParentId() == 0L) {
                roots.add(d);
            }
        }
        if (roots.isEmpty() && !all.isEmpty()) {
            roots = byParent.getOrDefault(byParent.keySet().stream().min(Long::compareTo).orElse(0L), all);
        }

        return roots.stream().map(r -> buildNode(r, byParent)).toList();
    }

    private Map<String, Object> buildNode(SysDept dept, Map<Long, List<SysDept>> byParent) {
        Map<String, Object> node = new HashMap<>();
        node.put("deptId", dept.getDeptId());
        node.put("parentId", dept.getParentId());
        node.put("deptName", dept.getDeptName());
        node.put("sort", dept.getSort());
        node.put("status", dept.getStatus());
        List<SysDept> children = byParent.getOrDefault(dept.getDeptId(), List.of());
        node.put("children", children.stream().map(c -> buildNode(c, byParent)).toList());
        return node;
    }
}
