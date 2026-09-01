package com.enterprise.tasksuperviseserver.module.warn.service.impl;

import com.enterprise.tasksuperviseserver.module.warn.entity.InAppMessage;
import com.enterprise.tasksuperviseserver.module.warn.mapper.InAppMessageMapper;
import com.enterprise.tasksuperviseserver.module.warn.service.NotificationService;
import com.enterprise.tasksuperviseserver.module.warn.websocket.MessageWebSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 通知服务实现
 * <p>
 * 统一封装消息入库 + WebSocket 实时推送，供各业务模块调用。
 *
 * @author grq
 * @date 2026-09-01
 * @version v1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final InAppMessageMapper inAppMessageMapper;

    @Override
    public void sendNotification(Long userId, String title, String content,
                                 int level, String msgType, Long relatedId) {
        if (userId == null) {
            log.warn("发送通知失败：目标用户ID为空");
            return;
        }

        try {
            // 1. 消息入库
            InAppMessage msg = new InAppMessage();
            msg.setUserId(userId);
            msg.setTitle(title);
            msg.setContent(content);
            msg.setLevel(level);
            msg.setMsgType(msgType);
            msg.setRelatedId(relatedId);
            msg.setReadStatus(0);
            msg.setCreatedAt(LocalDateTime.now());
            inAppMessageMapper.insert(msg);

            // 2. WebSocket 实时推送
            try {
                String pushData = String.format(
                        "{\"msgId\":%d,\"title\":\"%s\",\"content\":\"%s\",\"level\":%d,\"msgType\":\"%s\",\"relatedId\":%s}",
                        msg.getMsgId(),
                        escapeJson(title),
                        escapeJson(content),
                        level,
                        msgType,
                        relatedId != null ? relatedId.toString() : "null"
                );
                MessageWebSocket.sendToUser(userId, "new_message", pushData);
            } catch (Exception e) {
                log.debug("WebSocket 推送失败（用户可能不在线）: userId={}, error={}", userId, e.getMessage());
            }

            log.info("通知发送成功: userId={}, title={}, type={}", userId, title, msgType);
        } catch (Exception e) {
            log.error("发送通知失败: userId={}, title={}, error={}", userId, title, e.getMessage(), e);
        }
    }

    /**
     * 转义 JSON 字符串中的特殊字符
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}