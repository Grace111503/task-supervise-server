package com.enterprise.tasksuperviseserver.module.acceptance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprise.tasksuperviseserver.module.acceptance.entity.RectifyTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * 整改任务Mapper
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Mapper
public interface RectifyTaskMapper extends BaseMapper<RectifyTask> {
}
