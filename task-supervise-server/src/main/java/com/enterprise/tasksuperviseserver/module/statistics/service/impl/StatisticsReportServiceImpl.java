package com.enterprise.tasksuperviseserver.module.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enterprise.tasksuperviseserver.common.exception.BusinessException;
import com.enterprise.tasksuperviseserver.module.statistics.entity.StatisticsReport;
import com.enterprise.tasksuperviseserver.module.statistics.mapper.StatisticsReportMapper;
import com.enterprise.tasksuperviseserver.module.statistics.service.StatisticsReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

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
}
