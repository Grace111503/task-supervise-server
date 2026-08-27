package com.enterprise.tasksuperviseserver.module.warn.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.warn.entity.InAppMessage;
import com.enterprise.tasksuperviseserver.module.warn.service.InAppMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 站内消息接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/warn/message")
@RequiredArgsConstructor
public class InAppMessageController {

    private final InAppMessageService inAppMessageService;

    /**
     * 分页查询消息列表
     * GET /api/v1/warn/message/page
     */
    @GetMapping("/page")
    public Result<Page<InAppMessage>> page(@RequestParam(defaultValue = "1") int pageNo,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          @RequestParam(required = false) Integer isRead) {
        return Result.success(inAppMessageService.page(pageNo, pageSize, isRead));
    }

    /**
     * 获取消息详情
     * GET /api/v1/warn/message/{messageId}
     */
    @GetMapping("/{messageId}")
    public Result<InAppMessage> getById(@PathVariable Long messageId) {
        return Result.success(inAppMessageService.getById(messageId));
    }

    /**
     * 新增消息
     * POST /api/v1/warn/message
     */
    @PostMapping
    public Result<InAppMessage> create(@RequestBody InAppMessage entity) {
        return Result.success(inAppMessageService.create(entity));
    }

    /**
     * 更新消息
     * PUT /api/v1/warn/message/{messageId}
     */
    @PutMapping("/{messageId}")
    public Result<InAppMessage> update(@PathVariable Long messageId, @RequestBody InAppMessage entity) {
        entity.setMsgId(messageId);
        return Result.success(inAppMessageService.update(entity));
    }

    /**
     * 物理删除消息
     * DELETE /api/v1/warn/message/{messageId}
     */
    @DeleteMapping("/{messageId}")
    public Result<Void> delete(@PathVariable Long messageId) {
        inAppMessageService.delete(messageId);
        return Result.success();
    }

    /**
     * 查询当前登录用户的消息列表（按 userId 查询，userId 从 UserContext 获取）
     * GET /api/v1/warn/message/my
     */
    @GetMapping("/my")
    public Result<List<InAppMessage>> listMyMessages(@RequestParam(required = false) Integer isRead) {
        return Result.success(inAppMessageService.listMyMessages(isRead));
    }

    /**
     * 标记消息为已读
     * PUT /api/v1/warn/message/{messageId}/read
     */
    @PutMapping("/{messageId}/read")
    public Result<Void> markAsRead(@PathVariable Long messageId) {
        inAppMessageService.markAsRead(messageId);
        return Result.success();
    }
}
