package com.enterprise.tasksuperviseserver.common.controller;

import com.enterprise.tasksuperviseserver.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查接口
 */
@RestController
public class HealthController {

    /**
     * 根路径接口 - 返回服务基本信息
     */
    @GetMapping("/")
    public Result<Map<String, Object>> index() {
        Map<String, Object> data = new HashMap<>();
        data.put("service", "任务督办管理系统");
        data.put("version", "v1.0.0");
        data.put("status", "running");
        data.put("time", LocalDateTime.now());
        return Result.success(data);
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        return Result.success(data);
    }
}