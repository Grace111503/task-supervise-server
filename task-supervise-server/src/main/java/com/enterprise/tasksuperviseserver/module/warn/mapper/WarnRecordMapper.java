package com.enterprise.tasksuperviseserver.module.warn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprise.tasksuperviseserver.module.warn.entity.WarnRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预警记录Mapper
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Mapper
public interface WarnRecordMapper extends BaseMapper<WarnRecord> {
}
