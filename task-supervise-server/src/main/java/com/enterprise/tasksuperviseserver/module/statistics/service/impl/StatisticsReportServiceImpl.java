package com.enterprise.tasksuperviseserver.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.statistics.entity.StatisticsReport;
import com.enterprise.tasksuperviseserver.module.statistics.mapper.StatisticsReportMapper;
import com.enterprise.tasksuperviseserver.module.statistics.service.StatisticsReportService;
import com.enterprise.tasksuperviseserver.module.task.entity.Task;
import com.enterprise.tasksuperviseserver.module.task.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计报表 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsReportServiceImpl implements StatisticsReportService {

    private final StatisticsReportMapper statisticsReportMapper;
    private final TaskMapper taskMapper;

    @Override
    public Page<StatisticsReport> page(int pageNo, int pageSize, String period, String periodValue, Long deptId, Long userId) {
        LambdaQueryWrapper<StatisticsReport> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(period)) {
            wrapper.eq(StatisticsReport::getPeriod, period);
        }
        if (StringUtils.hasText(periodValue)) {
            wrapper.eq(StatisticsReport::getPeriodValue, periodValue);
        }
        if (deptId != null) {
            wrapper.eq(StatisticsReport::getDeptId, deptId);
        }
        if (userId != null) {
            wrapper.eq(StatisticsReport::getUserId, userId);
        }
        wrapper.orderByDesc(StatisticsReport::getGenerateTime);
        return statisticsReportMapper.selectPage(Page.of(pageNo, pageSize), wrapper);
    }

    @Override
    public StatisticsReport getById(Long reportId) {
        StatisticsReport report = statisticsReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException(404, "统计报表不存在");
        }
        return report;
    }

    @Override
    public StatisticsReport create(StatisticsReport entity) {
        entity.setReportId(null);
        entity.setGenerateTime(LocalDateTime.now());
        statisticsReportMapper.insert(entity);
        return entity;
    }

    @Override
    public StatisticsReport update(StatisticsReport entity) {
        if (entity.getReportId() == null) {
            throw new BusinessException("报表ID不能为空");
        }
        StatisticsReport exist = statisticsReportMapper.selectById(entity.getReportId());
        if (exist == null) {
            throw new BusinessException(404, "统计报表不存在");
        }
        statisticsReportMapper.updateById(entity);
        return statisticsReportMapper.selectById(entity.getReportId());
    }

    @Override
    public void delete(Long reportId) {
        StatisticsReport exist = statisticsReportMapper.selectById(reportId);
        if (exist == null) {
            throw new BusinessException(404, "统计报表不存在");
        }
        statisticsReportMapper.deleteById(reportId);
    }

    @Override
    public byte[] export(String period, String periodValue, Long deptId, Long userId) {
        LambdaQueryWrapper<StatisticsReport> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(period)) {
            wrapper.eq(StatisticsReport::getPeriod, period);
        }
        if (StringUtils.hasText(periodValue)) {
            wrapper.eq(StatisticsReport::getPeriodValue, periodValue);
        }
        if (deptId != null) {
            wrapper.eq(StatisticsReport::getDeptId, deptId);
        }
        if (userId != null) {
            wrapper.eq(StatisticsReport::getUserId, userId);
        }
        wrapper.orderByDesc(StatisticsReport::getGenerateTime);
        List<StatisticsReport> reports = statisticsReportMapper.selectList(wrapper);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("统计报表");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // 表头
            String[] headers = {"报表ID", "周期", "周期值", "部门ID", "用户ID",
                    "派发总数", "按时率", "逾期数", "平均完成天数", "生成时间"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
                headerRow.getCell(i).setCellStyle(headerStyle);
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            // 数据行
            for (int i = 0; i < reports.size(); i++) {
                StatisticsReport r = reports.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(r.getReportId() != null ? r.getReportId() : 0);
                row.createCell(1).setCellValue(r.getPeriod() != null ? r.getPeriod() : "");
                row.createCell(2).setCellValue(r.getPeriodValue() != null ? r.getPeriodValue() : "");
                row.createCell(3).setCellValue(r.getDeptId() != null ? r.getDeptId() : 0);
                row.createCell(4).setCellValue(r.getUserId() != null ? r.getUserId() : 0);
                row.createCell(5).setCellValue(r.getTotalDispatch() != null ? r.getTotalDispatch() : 0);
                row.createCell(6).setCellValue(r.getOnTimeRate() != null ? r.getOnTimeRate().doubleValue() : 0);
                row.createCell(7).setCellValue(r.getOverdueCount() != null ? r.getOverdueCount() : 0);
                row.createCell(8).setCellValue(r.getAvgCompleteDays() != null ? r.getAvgCompleteDays().doubleValue() : 0);
                row.createCell(9).setCellValue(r.getGenerateTime() != null ? r.getGenerateTime().format(fmt) : "");
            }

            // 自动列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new BusinessException("导出 Excel 失败: " + e.getMessage());
        }
    }

    @Override
    public List<StatisticsReport> generate(String period, String periodValue, Long deptId) {
        // 解析时间范围
        LocalDate startDate;
        LocalDate endDate;
        if ("quarter".equals(period)) {
            // periodValue 格式: "2026-Q3"
            String[] parts = periodValue.split("-Q");
            int year = Integer.parseInt(parts[0]);
            int quarter = Integer.parseInt(parts[1]);
            int startMonth = (quarter - 1) * 3 + 1;
            startDate = LocalDate.of(year, startMonth, 1);
            endDate = startDate.plusMonths(3).with(TemporalAdjusters.lastDayOfMonth());
        } else {
            // month 格式: "2026-08"
            LocalDate month = LocalDate.parse(periodValue + "-01");
            startDate = month.with(TemporalAdjusters.firstDayOfMonth());
            endDate = month.with(TemporalAdjusters.lastDayOfMonth());
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<StatisticsReport> reports = new ArrayList<>();

        if (deptId != null) {
            // 按部门生成
            StatisticsReport report = computeReport(period, periodValue, deptId, null, start, end);
            reports.add(report);
            statisticsReportMapper.insert(report);
        } else {
            // 全企业汇总
            StatisticsReport report = computeReport(period, periodValue, null, null, start, end);
            reports.add(report);
            statisticsReportMapper.insert(report);
        }

        log.info("统计报表生成完成: period={}, periodValue={}, deptId={}, 共{}条", period, periodValue, deptId, reports.size());
        return reports;
    }

    @Override
    public Map<String, Object> overview(Long deptId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getDeleted, 0);
        if (deptId != null) {
            wrapper.eq(Task::getDeptId, deptId);
        }

        List<Task> tasks = taskMapper.selectList(wrapper);
        return computeOverview(tasks);
    }

    /**
     * 定时任务：每月1号凌晨自动生成上月统计报表
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void autoGenerateMonthlyReport() {
        try {
            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            String periodValue = lastMonth.getYear() + "-" + String.format("%02d", lastMonth.getMonthValue());
            generate("month", periodValue, null);
            log.info("月度统计报表自动生成完成: {}", periodValue);
        } catch (Exception e) {
            log.error("月度统计报表自动生成失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 计算单条统计报表
     */
    private StatisticsReport computeReport(String period, String periodValue, Long deptId, Long userId,
                                           LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Task::getCreatedAt, start);
        wrapper.le(Task::getCreatedAt, end);
        wrapper.eq(Task::getDeleted, 0);
        if (deptId != null) {
            wrapper.eq(Task::getDeptId, deptId);
        }
        if (userId != null) {
            wrapper.eq(Task::getAssigneeId, userId);
        }

        List<Task> tasks = taskMapper.selectList(wrapper);
        Map<String, Object> stats = computeOverview(tasks);

        StatisticsReport report = new StatisticsReport();
        report.setPeriod(period);
        report.setPeriodValue(periodValue);
        report.setDeptId(deptId);
        report.setUserId(userId);
        report.setTotalDispatch(((Number) stats.get("totalDispatch")).intValue());
        report.setOnTimeRate(new BigDecimal(stats.get("onTimeRate").toString()));
        report.setOverdueCount(((Number) stats.get("overdueCount")).intValue());
        report.setAvgCompleteDays(new BigDecimal(stats.get("avgCompleteDays").toString()));
        report.setGenerateTime(LocalDateTime.now());
        return report;
    }

    /**
     * 从任务列表计算统计概览
     */
    private Map<String, Object> computeOverview(List<Task> tasks) {
        int total = tasks.size();
        int completed = 0;
        int overdue = 0;
        long totalCompleteDays = 0;
        int completedWithTime = 0;

        for (Task task : tasks) {
            if ("completed".equals(task.getStatus())) {
                completed++;
                if (task.getCreatedAt() != null && task.getAcceptedAt() != null) {
                    long days = java.time.Duration.between(task.getCreatedAt(), task.getAcceptedAt()).toDays();
                    totalCompleteDays += days;
                    completedWithTime++;
                }
            }
            if ("overdue".equals(task.getStatus())) {
                overdue++;
            }
        }

        // 按期完成率 = 已完成数 / 总数 * 100
        BigDecimal onTimeRate = total > 0
                ? BigDecimal.valueOf(completed).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 平均办结时长（天）
        BigDecimal avgDays = completedWithTime > 0
                ? BigDecimal.valueOf(totalCompleteDays)
                .divide(BigDecimal.valueOf(completedWithTime), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalDispatch", total);
        result.put("completedCount", completed);
        result.put("overdueCount", overdue);
        result.put("onTimeRate", onTimeRate);
        result.put("avgCompleteDays", avgDays);
        return result;
    }
}
