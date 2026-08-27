package com.enterprise.tasksuperviseserver.module.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.org.entity.SysRole;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUser;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUserRole;
import com.enterprise.tasksuperviseserver.module.org.mapper.SysRoleMapper;
import com.enterprise.tasksuperviseserver.module.org.mapper.SysUserMapper;
import com.enterprise.tasksuperviseserver.module.org.mapper.SysUserRoleMapper;
import com.enterprise.tasksuperviseserver.module.org.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统角色 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public Page<SysRole> page(int page, int pageSize, String keyword) {
        Page<SysRole> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysRole::getRoleCode, keyword)
                    .or()
                    .like(SysRole::getRoleName, keyword);
        }
        wrapper.orderByDesc(SysRole::getCreateTime);
        return sysRoleMapper.selectPage(p, wrapper);
    }

    @Override
    public SysRole detail(Long roleId) {
        SysRole sysRole = sysRoleMapper.selectById(roleId);
        if (sysRole == null) {
            throw new BusinessException(404, "角色不存在");
        }
        return sysRole;
    }

    @Override
    public SysRole create(SysRole sysRole) {
        sysRole.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(sysRole);
        return sysRole;
    }

    @Override
    public SysRole update(SysRole sysRole) {
        SysRole existing = sysRoleMapper.selectById(sysRole.getRoleId());
        if (existing == null) {
            throw new BusinessException(404, "角色不存在");
        }
        sysRoleMapper.updateById(sysRole);
        return sysRoleMapper.selectById(sysRole.getRoleId());
    }

    @Override
    public boolean delete(Long roleId) {
        return sysRoleMapper.deleteById(roleId) > 0;
    }

    @Override
    public void assign(Long userId, List<Long> roleIds) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 1. 删除旧的角色关联
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));
        // 2. 插入新的角色关联
        String firstRoleCode = null;
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysRole role = sysRoleMapper.selectById(roleId);
                if (role == null) continue;
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
                if (firstRoleCode == null) {
                    firstRoleCode = role.getRoleCode();
                }
            }
        }
        // 3. 同步更新用户 roleCode
        user.setRoleCode(firstRoleCode);
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    @Override
    public List<SysRole> list(String keyword) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysRole::getRoleCode, keyword)
                    .or()
                    .like(SysRole::getRoleName, keyword);
        }
        wrapper.orderByAsc(SysRole::getSort);
        return sysRoleMapper.selectList(wrapper);
    }
}
