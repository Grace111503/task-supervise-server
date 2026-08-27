package com.enterprise.tasksuperviseserver.module.acceptance.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.RectifyTask;
import com.enterprise.tasksuperviseserver.module.acceptance.service.RectifyTaskService;
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
 * 整改任务接口
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/acceptance/rectify")
@RequiredArgsConstructor
public class RectifyTaskController {

    private final RectifyTaskService rectifyTaskService;

    /**
     * 分页查询整改任务列表
     */
    @GetMapping("/page")
    public Result<Page<RectifyTask>> page(@RequestParam(defaultValue = "1") int pageNo,
                                           @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(rectifyTaskService.page(pageNo, pageSize));
    }

    /**
     * 获取整改任务详情
     */
    @GetMapping("/{rectifyId}")
    public Result<RectifyTask> getDetail(@PathVariable Long rectifyId) {
        return Result.success(rectifyTaskService.getDetail(rectifyId));
    }

    /**
     * 新增整改任务
     */
    @PostMapping
    public Result<RectifyTask> add(@RequestBody RectifyTask rectifyTask) {
        return Result.success(rectifyTaskService.add(rectifyTask));
    }

    /**
     * 更新整改任务
     */
    @PutMapping
    public Result<RectifyTask> update(@RequestBody RectifyTask rectifyTask) {
        return Result.success(rectifyTaskService.update(rectifyTask));
    }

    /**
     * 删除整改任务
     */
    @DeleteMapping("/{rectifyId}")
    public Result<Void> delete(@PathVariable Long rectifyId) {
        rectifyTaskService.delete(rectifyId);
        return Result.success("删除成功", null);
    }

    /**
     * 按 acceptId 查询整改任务
     */
    @GetMapping("/accept/{acceptId}")
    public Result<List<RectifyTask>> listByAcceptId(@PathVariable Long acceptId) {
        return Result.success(rectifyTaskService.listByAcceptId(acceptId));
    }

    /**
     * 完成整改
     */
    @PutMapping("/{rectifyId}/complete")
    public Result<RectifyTask> complete(@PathVariable Long rectifyId,
                                        @RequestParam(required = false) String rectifyOpinion) {
        return Result.success(rectifyTaskService.complete(rectifyId, rectifyOpinion));
    }
}
