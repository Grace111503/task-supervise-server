package com.enterprise.tasksuperviseserver.module.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

/**
 * 任务列表 Redis 缓存服务
 * <p>
 * 缓存场景：首页看板高频查询的任务列表，按 userId + 查询条件组合 key，
 * TTL 30 秒，写操作（创建/更新/删除/指派/状态变更）时清除该用户所有缓存。
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "task:list:";
    private static final String KEY_PATTERN = "task:list:*";
    private static final Duration TTL = Duration.ofSeconds(30);

    /**
     * 从缓存获取任务列表
     *
     * @param cacheKey 缓存 key（由调用方构造）
     * @return 缓存结果，不存在返回 null
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> get(String cacheKey) {
        try {
            Object val = redisTemplate.opsForValue().get(KEY_PREFIX + cacheKey);
            if (val instanceof Map) {
                return (Map<String, Object>) val;
            }
        } catch (Exception e) {
            log.warn("Redis 缓存读取失败，降级到数据库查询: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 写入缓存
     */
    public void put(String cacheKey, Map<String, Object> data) {
        try {
            redisTemplate.opsForValue().set(KEY_PREFIX + cacheKey, data, TTL);
        } catch (Exception e) {
            log.warn("Redis 缓存写入失败: {}", e.getMessage());
        }
    }

    /**
     * 清除指定用户的所有任务列表缓存
     * 通过 key pattern 批量删除
     */
    public void evictByUser(Long userId) {
        try {
            redisTemplate.delete(redisTemplate.keys(KEY_PREFIX + "u" + userId + ":*"));
        } catch (Exception e) {
            log.warn("Redis 缓存清除失败: {}", e.getMessage());
        }
    }

    /**
     * 清除所有任务列表缓存
     */
    public void evictAll() {
        try {
            redisTemplate.delete(redisTemplate.keys(KEY_PATTERN));
        } catch (Exception e) {
            log.warn("Redis 缓存清除失败: {}", e.getMessage());
        }
    }

    /**
     * 构造缓存 key
     * 格式：u{userId}:p{page}:s{pageSize}:st{status}:pr{priority}:g{groupId}:k{keyword}:a{assigneeId}
     */
    public static String buildKey(Long userId, long page, long pageSize,
                                  Integer status, Integer priority,
                                  Long groupId, String keyword, Long assigneeId) {
        return String.format("u%d:p%d:s%d:st%s:pr%s:g%s:k%s:a%s",
                userId, page, pageSize,
                status != null ? status : "",
                priority != null ? priority : "",
                groupId != null ? groupId : "",
                keyword != null ? keyword.hashCode() : "",
                assigneeId != null ? assigneeId : "");
    }
}
