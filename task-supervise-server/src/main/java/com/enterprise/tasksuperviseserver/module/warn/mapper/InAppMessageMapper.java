package com.enterprise.tasksuperviseserver.module.warn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enterprise.tasksuperviseserver.module.warn.entity.InAppMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 站内消息Mapper
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Mapper
public interface InAppMessageMapper extends BaseMapper<InAppMessage> {
}
