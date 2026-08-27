package com.enterprise.tasksuperviseserver.module.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务模板字段实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("task_template_field")
public class TaskTemplateField {

    @TableId(value = "field_id", type = IdType.AUTO)
    private Long fieldId;

    private Long templateId;

    private String fieldName;

    private String fieldKey;

    private String fieldType;

    private Integer required;

    private String defaultValue;

    private Integer sort;

}
