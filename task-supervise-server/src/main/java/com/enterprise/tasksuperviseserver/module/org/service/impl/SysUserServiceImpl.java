package com.enterprise.tasksuperviseserver.module.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUser;
import com.enterprise.tasksuperviseserver.module.org.mapper.SysUserMapper;
import com.enterprise.tasksuperviseserver.module.org.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 系统用户 Service 实现
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<SysUser> page(int page, int pageSize, String keyword) {
        Page<SysUser> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysUser::getUserName, keyword)
                    .or()
                    .like(SysUser::getPhone, keyword);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = sysUserMapper.selectPage(p, wrapper);
        result.getRecords().forEach(u -> u.setPassword(null));
        return result;
    }

    @Override
    public SysUser detail(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new BusinessException(404, "用户不存在");
        }
        sysUser.setPassword(null);
        return sysUser;
    }

    @Override
    public SysUser create(SysUser sysUser) {
        if (StringUtils.hasText(sysUser.getPassword())) {
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        }
        sysUser.setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(sysUser);
        sysUser.setPassword(null);
        return sysUser;
    }

    @Override
    public SysUser update(SysUser sysUser) {
        SysUser existing = sysUserMapper.selectById(sysUser.getUserId());
        if (existing == null) {
            throw new BusinessException(404, "用户不存在");
        }
        sysUser.setPassword(null);
        sysUser.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(sysUser);
        return detail(sysUser.getUserId());
    }

    @Override
    public boolean delete(Long userId) {
        return sysUserMapper.deleteById(userId) > 0;
    }

    @Override
    public void transfer(Long userId, Long deptId, String roleCode) {
        SysUser existing = sysUserMapper.selectById(userId);
        if (existing == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (deptId != null) {
            existing.setDeptId(deptId);
        }
        if (org.springframework.util.StringUtils.hasText(roleCode)) {
            existing.setRoleCode(roleCode);
        }
        existing.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(existing);
    }

    @Override
    public void archive(Long userId) {
        SysUser existing = sysUserMapper.selectById(userId);
        if (existing == null) {
            throw new BusinessException(404, "用户不存在");
        }
        existing.setStatus(1); // 1 = 离职归档
        existing.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(existing);
    }
}
