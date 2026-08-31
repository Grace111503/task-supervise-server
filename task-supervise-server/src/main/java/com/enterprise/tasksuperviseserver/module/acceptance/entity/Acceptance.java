package com.enterprise.tasksuperviseserver.module.acceptance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enterprise.tasksuperviseserver.module.feedback.entity.TaskFile;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 验收实体
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("acceptance")
public class Acceptance {

    @TableId(value = "accept_id", type = IdType.AUTO)
    private Long acceptId;

    private Long taskId;

    @TableField("applicant_id")
    private Long applicantId;

    /** 申请人姓名 */
    @TableField("applicant_name")
    private String applicantName;

    @TableField("acceptor_id")
    private Long acceptorId;

    /** 验收人姓名 */
    @TableField("acceptor_name")
    private String acceptorName;

    /** 验收结果: 0-待验收 1-通过 2-退回 */
    private Integer result;

    /** 验收意见 */
    private String opinion;

    @TableField("apply_time")
    private LocalDateTime applyTime;

    @TableField("accept_time")
    private LocalDateTime acceptTime;

    /** 关联文件列表（非数据库字段） */
    @TableField(exist = false)
    private List<TaskFile> files;

}
