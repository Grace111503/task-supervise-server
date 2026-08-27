package com.enterprise.tasksuperviseserver.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.statistics.entity.StatisticsReport;
import com.enterprise.tasksuperviseserver.module.statistics.mapper.StatisticsReportMapper;
import com.enterprise.tasksuperviseserver.module.statistics.service.StatisticsReportService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 统计报表 Service 实现
 */
@Service
@RequiredArgsConstructor
public class StatisticsReportServiceImpl implements StatisticsReportService {

    private final StatisticsReportMapper statisticsReportMapper;

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
}
