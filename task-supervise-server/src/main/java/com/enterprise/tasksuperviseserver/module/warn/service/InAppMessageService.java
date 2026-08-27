package com.enterprise.tasksuperviseserver.module.warn.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.module.warn.entity.InAppMessage;

import java.util.List;

/**
 * 站内消息 Service
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public interface InAppMessageService {

    /**
     * 分页查询消息列表（支持按 isRead 筛选）
     */
    Page<InAppMessage> page(int pageNo, int pageSize, Integer isRead);

    /**
     * 获取消息详情
     */
    InAppMessage getById(Long messageId);

    /**
     * 新增消息（createTime 自动设置为当前时间）
     */
    InAppMessage create(InAppMessage entity);

    /**
     * 更新消息
     */
    InAppMessage update(InAppMessage entity);

    /**
     * 物理删除消息
     */
    void delete(Long messageId);

    /**
     * 按当前登录用户 ID 查询消息列表（支持 isRead 筛选）
     * userId 从 UserContext 获取
     */
    List<InAppMessage> listMyMessages(Integer isRead);

    /**
     * 标记消息为已读（isRead = 1）
     */
    void markAsRead(Long messageId);
}
