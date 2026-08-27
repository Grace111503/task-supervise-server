package com.enterprise.tasksuperviseserver.module.statistics.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprise.tasksuperviseserver.module.statistics.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
