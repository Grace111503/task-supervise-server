package com.enterprise.tasksuperviseserver.module.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprise.tasksuperviseserver.module.org.entity.TaskGroup;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务分组 Mapper
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Mapper
public interface TaskGroupMapper extends BaseMapper<TaskGroup> {
}
