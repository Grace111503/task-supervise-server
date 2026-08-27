package com.enterprise.tasksuperviseserver.module.acceptance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.OverdueAccountability;
import org.apache.ibatis.annotations.Mapper;

/**
 * 逾期问责Mapper
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Mapper
public interface OverdueAccountabilityMapper extends BaseMapper<OverdueAccountability> {
}
