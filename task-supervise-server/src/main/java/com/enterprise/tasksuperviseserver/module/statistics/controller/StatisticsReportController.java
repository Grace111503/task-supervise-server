package com.enterprise.tasksuperviseserver.module.statistics.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.constant.TaskConstant;
import com.enterprise.tasksuperviseserver.common.result.Result;
import com.enterprise.tasksuperviseserver.module.statistics.entity.StatisticsReport;
import com.enterprise.tasksuperviseserver.module.statistics.service.StatisticsReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 统计报表接口
 *
 * @author grq
 * @date 2026-08-27
 * @version v1.0.0
 */
@RestController
@RequestMapping(TaskConstant.API_PREFIX + "/statistics/report")
@RequiredArgsConstructor
public class StatisticsReportController {

    private final StatisticsReportService statisticsReportService;

    /**
     * 分页查询统计报表列表
     * GET /api/v1/statistics/report/page
     */
    @GetMapping("/page")
    public Result<Page<StatisticsReport>> page(@RequestParam(defaultValue = "1") int pageNo,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              @RequestParam(required = false) String period,
                                              @RequestParam(required = false) String periodValue,
                                              @RequestParam(required = false) Long deptId,
                                              @RequestParam(required = false) Long userId) {
        return Result.success(statisticsReportService.page(pageNo, pageSize, period, periodValue, deptId, userId));
    }

    /**
     * 获取统计报表详情
     * GET /api/v1/statistics/report/{reportId}
     */
    @GetMapping("/{reportId}")
    public Result<StatisticsReport> getById(@PathVariable Long reportId) {
        return Result.success(statisticsReportService.getById(reportId));
    }

    /**
     * 新增统计报表
     * POST /api/v1/statistics/report
     */
    @PostMapping
    public Result<StatisticsReport> create(@RequestBody StatisticsReport entity) {
        return Result.success(statisticsReportService.create(entity));
    }

    /**
     * 更新统计报表
     * PUT /api/v1/statistics/report/{reportId}
     */
    @PutMapping("/{reportId}")
    public Result<StatisticsReport> update(@PathVariable Long reportId, @RequestBody StatisticsReport entity) {
        entity.setReportId(reportId);
        return Result.success(statisticsReportService.update(entity));
    }

    /**
     * 物理删除统计报表
     * DELETE /api/v1/statistics/report/{reportId}
     */
    @DeleteMapping("/{reportId}")
    public Result<Void> delete(@PathVariable Long reportId) {
        statisticsReportService.delete(reportId);
        return Result.success();
    }

    /**
     * 导出统计报表为 Excel
     * GET /api/v1/statistics/report/export
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String periodValue,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long userId) {
        byte[] bytes = statisticsReportService.export(period, periodValue, deptId, userId);
        String fileName = URLEncoder.encode("统计报表.xlsx", StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    /**
     * 自动生成统计报表
     * POST /api/v1/statistics/report/generate
     * body: { "period": "month", "periodValue": "2026-08", "deptId": null }
     */
    @PostMapping("/generate")
    public Result<List<StatisticsReport>> generate(@RequestBody Map<String, Object> body) {
        String period = (String) body.get("period");
        String periodValue = (String) body.get("periodValue");
        Long deptId = body.get("deptId") != null ? ((Number) body.get("deptId")).longValue() : null;
        return Result.success(statisticsReportService.generate(period, periodValue, deptId));
    }

    /**
     * 实时统计概览（不落库，直接从任务数据计算）
     * GET /api/v1/statistics/report/overview
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview(@RequestParam(required = false) Long deptId) {
        return Result.success(statisticsReportService.overview(deptId));
    }
}
