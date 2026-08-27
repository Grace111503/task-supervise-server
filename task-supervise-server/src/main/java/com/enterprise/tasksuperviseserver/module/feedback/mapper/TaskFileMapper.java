package com.enterprise.tasksuperviseserver.module.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务文件Mapper
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Mapper
public interface TaskFileMapper extends BaseMapper<TaskFile> {
}
