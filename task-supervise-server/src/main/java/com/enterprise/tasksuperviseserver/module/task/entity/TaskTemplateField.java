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

    /** 模板ID */
    private Long templateId;

    /** 字段名称 */
    @TableField("field_name")
    private String fieldName;

    /** 字段标识（用于表单提交） */
    @TableField("field_key")
    private String fieldKey;

    /** 字段类型: text/textarea/number/date/select/file/checkbox/radio */
    @TableField("field_type")
    private String fieldType;

    /** 是否必填: 0-否 1-是 */
    private Integer required;

    /** 默认值 */
    @TableField("default_value")
    private String defaultValue;

    /** 排序 */
    private Integer sort;

    /** 字段选项（用于select/radio/checkbox类型，JSON格式） */
    @TableField("options")
    private String options;

    /** 字段提示信息 */
    @TableField("placeholder")
    private String placeholder;

}
