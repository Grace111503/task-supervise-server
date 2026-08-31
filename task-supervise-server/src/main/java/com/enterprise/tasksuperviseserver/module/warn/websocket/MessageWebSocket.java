package com.enterprise.tasksuperviseserver.module.warn.websocket;

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
 * 站内消息 WebSocket 端点
 * <p>
 * 用于实时推送站内消息（预警通知等）给在线用户。
 *
 * @author grq
 * @date 2026-08-28
 * @version v1.0.0
 */
@Slf4j
@Component
@ServerEndpoint("/ws/message/{userId}")
public class MessageWebSocket {

    /** 在线用户连接池：userId -> Session */
    private static final Map<Long, Session> SESSIONS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId) {
        SESSIONS.put(userId, session);
        log.info("MessageWebSocket 连接建立: userId={}", userId);
    }

    @OnClose
    public void onClose(@PathParam("userId") Long userId) {
        SESSIONS.remove(userId);
        log.info("MessageWebSocket 连接关闭: userId={}", userId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("MessageWebSocket 异常: sessionId={}, error={}", session.getId(), error.getMessage());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") Long userId) {
        log.debug("MessageWebSocket 收到消息: userId={}, message={}", userId, message);
    }

    /**
     * 向指定用户推送消息
     */
    public static void sendToUser(Long userId, String type, String data) {
        Session session = SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String message = String.format("{\"type\":\"%s\",\"data\":%s}", type, data);
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("MessageWebSocket 推送失败: userId={}, error={}", userId, e.getMessage());
            }
        }
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