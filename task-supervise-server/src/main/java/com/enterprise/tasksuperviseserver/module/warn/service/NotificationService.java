package com.enterprise.tasksuperviseserver.module.warn.service;

/**
 * 通知服务接口
 * <p>
 * 统一封装消息入库 + WebSocket 实时推送，供各业务模块调用。
 *
 * @author grq
 * @date 2026-09-01
 * @version v1.0.0
 */
public interface NotificationService {

    /**
     * 发送站内通知
     *
     * @param userId    目标用户ID
     * @param title     消息标题
     * @param content   消息内容
     * @param level     消息级别 1-普通 2-重要 3-紧急
     * @param msgType   消息类型 TASK/WARN/ACCEPT
     * @param relatedId 关联任务ID（可为null）
     */
    void sendNotification(Long userId, String title, String content,
                          int level, String msgType, Long relatedId);
}