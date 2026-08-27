-- 任务督办系统数据库脚本
-- 版本: v2.0.0
-- 说明: 每次启动都会重建 sys_user 和 task 表（DROP IF EXISTS），其余模块表为可选

-- ========== 1. 用户表 ==========
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密存储)',
    name VARCHAR(50) NOT NULL COMMENT '姓名/昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色: admin/user',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ========== 2. 部门表 ==========
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
    dept_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '部门ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID 0为顶级',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    sort INT DEFAULT 0 COMMENT '排序',
    leader VARCHAR(50) COMMENT '负责人',
    phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-停用',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ========== 3. 角色表 ==========
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ========== 4. 用户角色关联 ==========
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联';

-- ========== 5. 任务组表 ==========
DROP TABLE IF EXISTS task_group;
CREATE TABLE task_group (
    group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    creator_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务组';

-- ========== 6. 任务表 ==========
DROP TABLE IF EXISTS task;
CREATE TABLE task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending待处理/in_progress进行中/completed已完成/overdue已逾期',
    priority VARCHAR(20) DEFAULT 'medium' COMMENT '优先级: high高/medium中/low低',
    deadline TIMESTAMP NULL COMMENT '截止时间',
    remark VARCHAR(500) COMMENT '备注',
    attachments VARCHAR(2000) COMMENT '附件(多个URL以逗号分隔)',
    creator_id BIGINT COMMENT '创建人ID',
    creator_name VARCHAR(50) COMMENT '创建人姓名',
    assignee_id BIGINT COMMENT '指派人ID',
    assignee_name VARCHAR(50) COMMENT '指派人姓名',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_creator (creator_id),
    INDEX idx_assignee (assignee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- ========== 7. 预警规则表 ==========
DROP TABLE IF EXISTS warn_rule;
CREATE TABLE warn_rule (
    rule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL,
    condition_expr VARCHAR(500) COMMENT '触发条件表达式',
    level INT DEFAULT 1 COMMENT '预警级别 1-普通 2-重要 3-紧急',
    enabled TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则';

-- ========== 8. 预警记录表 ==========
DROP TABLE IF EXISTS warn_record;
CREATE TABLE warn_record (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    rule_id BIGINT,
    level INT,
    warn_content VARCHAR(1000),
    push_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警记录';

-- ========== 9. 站内消息表 ==========
DROP TABLE IF EXISTS in_app_message;
CREATE TABLE in_app_message (
    msg_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    content VARCHAR(2000),
    level INT DEFAULT 1 COMMENT '消息级别 1-普通 2-重要 3-紧急',
    read_status TINYINT DEFAULT 0 COMMENT '0-未读 1-已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_read (user_id, read_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息';

-- ========== 10. 进度反馈表 ==========
DROP TABLE IF EXISTS progress_feedback;
CREATE TABLE progress_feedback (
    feedback_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT,
    content VARCHAR(2000),
    progress_percent INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='进度反馈';

-- ========== 11. 任务文件表 ==========
DROP TABLE IF EXISTS task_file;
CREATE TABLE task_file (
    file_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_name VARCHAR(200),
    stored_name VARCHAR(500),
    file_path VARCHAR(1000),
    file_size BIGINT,
    file_type VARCHAR(50),
    task_id BIGINT,
    feedback_id BIGINT,
    uploader_id BIGINT,
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务文件';

-- ========== 12. 验收表 ==========
DROP TABLE IF EXISTS acceptance;
CREATE TABLE acceptance (
    accept_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    applicant_id BIGINT,
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    result VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/approved/rejected',
    opinion VARCHAR(1000),
    approver_id BIGINT,
    approve_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验收表';

-- ========== 13. 流程配置表 ==========
DROP TABLE IF EXISTS flow_config;
CREATE TABLE flow_config (
    flow_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flow_name VARCHAR(100) NOT NULL,
    dept_id BIGINT,
    config_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程配置';

-- ========== 14. 逾期追责表 ==========
DROP TABLE IF EXISTS overdue_accountability;
CREATE TABLE overdue_accountability (
    overdue_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    reason VARCHAR(1000),
    disposition VARCHAR(500),
    overdue_days INT,
    archive_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='逾期追责';

-- ========== 15. 整改任务表 ==========
DROP TABLE IF EXISTS rectify_task;
CREATE TABLE rectify_task (
    rectify_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    rectify_content VARCHAR(2000),
    deadline TIMESTAMP NULL,
    status VARCHAR(20) DEFAULT 'pending' COMMENT 'pending/completed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='整改任务';

-- ========== 16. 操作日志表 ==========
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module VARCHAR(50),
    action VARCHAR(50),
    task_id BIGINT,
    operator_id BIGINT,
    operator_name VARCHAR(50),
    detail TEXT,
    operate_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_module_time (module, operate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

-- ========== 17. 统计表 ==========
DROP TABLE IF EXISTS statistics_report;
CREATE TABLE statistics_report (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    period VARCHAR(20) COMMENT 'day/week/month/quarter/year',
    period_value VARCHAR(20),
    dept_id BIGINT,
    user_id BIGINT,
    total_tasks INT DEFAULT 0,
    completed_tasks INT DEFAULT 0,
    overdue_tasks INT DEFAULT 0,
    report_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统计报表';

-- ========== 18. 任务指派表 ==========
DROP TABLE IF EXISTS task_assignee;
CREATE TABLE task_assignee (
    assignee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    assignee_name VARCHAR(50),
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务指派';

-- ========== 19. 任务模板表 ==========
DROP TABLE IF EXISTS task_template;
CREATE TABLE task_template (
    template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    group_id BIGINT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务模板';

-- ========== 20. 任务模板字段表 ==========
DROP TABLE IF EXISTS task_template_field;
CREATE TABLE task_template_field (
    field_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT NOT NULL,
    field_name VARCHAR(100),
    field_label VARCHAR(100),
    field_type VARCHAR(50),
    required TINYINT DEFAULT 0,
    sort INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_template (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务模板字段';

-- ========== 21. 任务进度节点表 ==========
DROP TABLE IF EXISTS task_progress_node;
CREATE TABLE task_progress_node (
    node_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    node_name VARCHAR(100),
    sequence INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'pending',
    operator_id BIGINT,
    operator_name VARCHAR(50),
    operate_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务进度节点';
