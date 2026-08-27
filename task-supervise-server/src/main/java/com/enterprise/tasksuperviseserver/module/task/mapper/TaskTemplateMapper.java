package com.enterprise.tasksuperviseserver.module.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprise.tasksuperviseserver.module.task.entity.TaskTemplate;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务模板Mapper
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Mapper
public interface TaskTemplateMapper extends BaseMapper<TaskTemplate> {
}
