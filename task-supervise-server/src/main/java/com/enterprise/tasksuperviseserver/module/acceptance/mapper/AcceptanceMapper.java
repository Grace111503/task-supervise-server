package com.enterprise.tasksuperviseserver.module.acceptance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.Acceptance;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验收Mapper
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Mapper
public interface AcceptanceMapper extends BaseMapper<Acceptance> {
}
