package com.enterprise.tasksuperviseserver.module.warn.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.warn.entity.InAppMessage;
import com.enterprise.tasksuperviseserver.module.warn.mapper.InAppMessageMapper;
import com.enterprise.tasksuperviseserver.module.warn.service.InAppMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 站内消息 Service 实现
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Service
@RequiredArgsConstructor
public class InAppMessageServiceImpl implements InAppMessageService {

    private final InAppMessageMapper inAppMessageMapper;

    @Override
    public Page<InAppMessage> page(int pageNo, int pageSize, Integer isRead) {
        LambdaQueryWrapper<InAppMessage> wrapper = new LambdaQueryWrapper<>();
        if (isRead != null) {
            wrapper.eq(InAppMessage::getIsRead, isRead);
        }
        wrapper.orderByDesc(InAppMessage::getCreateTime);
        return inAppMessageMapper.selectPage(Page.of(pageNo, pageSize), wrapper);
    }

    @Override
    public InAppMessage getById(Long messageId) {
        InAppMessage message = inAppMessageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(404, "消息不存在");
        }
        return message;
    }

    @Override
    public InAppMessage create(InAppMessage entity) {
        entity.setMessageId(null);
        entity.setCreateTime(LocalDateTime.now());
        inAppMessageMapper.insert(entity);
        return entity;
    }

    @Override
    public InAppMessage update(InAppMessage entity) {
        if (entity.getMessageId() == null) {
            throw new BusinessException("消息ID不能为空");
        }
        InAppMessage exist = inAppMessageMapper.selectById(entity.getMessageId());
        if (exist == null) {
            throw new BusinessException(404, "消息不存在");
        }
        inAppMessageMapper.updateById(entity);
        return inAppMessageMapper.selectById(entity.getMessageId());
    }

    @Override
    public void delete(Long messageId) {
        InAppMessage exist = inAppMessageMapper.selectById(messageId);
        if (exist == null) {
            throw new BusinessException(404, "消息不存在");
        }
        inAppMessageMapper.deleteById(messageId);
    }

    @Override
    public List<InAppMessage> listMyMessages(Integer isRead) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已失效");
        }
        LambdaQueryWrapper<InAppMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InAppMessage::getUserId, userId);
        if (isRead != null) {
            wrapper.eq(InAppMessage::getIsRead, isRead);
        }
        wrapper.orderByDesc(InAppMessage::getCreateTime);
        return inAppMessageMapper.selectList(wrapper);
    }

    @Override
    public void markAsRead(Long messageId) {
        InAppMessage exist = inAppMessageMapper.selectById(messageId);
        if (exist == null) {
            throw new BusinessException(404, "消息不存在");
        }
        LambdaUpdateWrapper<InAppMessage> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(InAppMessage::getMessageId, messageId)
                .set(InAppMessage::getIsRead, 1);
        inAppMessageMapper.update(null, updateWrapper);
    }
}
