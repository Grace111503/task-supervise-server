package com.enterprise.tasksuperviseserver.module.feedback.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 进度反馈 WebSocket 端点
 * <p>
 * 用于实时推送进度反馈更新通知给相关人员（任务创建人、督办管理员）
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Slf4j
@Component
@ServerEndpoint("/ws/feedback/{userId}")
public class FeedbackWebSocket {

    /** 在线用户连接池：userId -> Session */
    private static final Map<Long, Session> SESSIONS = new ConcurrentHashMap<>();

    /** 通知类型常量 */
    public static final String TYPE_FEEDBACK_ADDED = "feedback_added";
    public static final String TYPE_FEEDBACK_UPDATED = "feedback_updated";
    public static final String TYPE_FILE_UPLOADED = "file_uploaded";

    /**
     * 连接建立
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        SESSIONS.put(userId, session);
        log.info("WebSocket 连接建立: userId={}, sessionId={}", userId, session.getId());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        SESSIONS.remove(userId);
        log.info("WebSocket 连接关闭: userId={}", userId);
    }

    /**
     * 异常处理
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 异常: sessionId={}, error={}", session.getId(), error.getMessage());
    }

    /**
     * 收到客户端消息（心跳检测等）
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long userId) {
        log.debug("WebSocket 收到消息: userId={}, message={}", userId, message);
    }

    /**
     * 向指定用户发送消息
     */
    public static void sendToUser(Long userId, String type, String data) {
        Session session = SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String message = String.format("{\"type\":\"%s\",\"data\":%s}", type, data);
                session.getBasicRemote().sendText(message);
                log.debug("WebSocket 消息发送成功: userId={}, type={}", userId, type);
            } catch (IOException e) {
                log.error("WebSocket 消息发送失败: userId={}, error={}", userId, e.getMessage());
            }
        }
    }

    /**
     * 广播消息给所有在线用户
     */
    public static void broadcast(String type, String data) {
        String message = String.format("{\"type\":\"%s\",\"data\":%s}", type, data);
        SESSIONS.forEach((userId, session) -> {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    log.error("WebSocket 广播失败: userId={}, error={}", userId, e.getMessage());
                }
            }
        });
    }

    /**
     * 检查用户是否在线
     */
    public static boolean isOnline(Long userId) {
        Session session = SESSIONS.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 获取在线用户数量
     */
    public static int getOnlineCount() {
        return SESSIONS.size();
    }
}