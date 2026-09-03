-- 任务督办系统数据库脚本
-- 版本: v2.0.1
-- 说明: 使用 CREATE TABLE IF NOT EXISTS，首次启动建表，后续启动跳过，保护已有数据

-- ========== 1. 用户表 ==========
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密存储)',
    name VARCHAR(50) NOT NULL COMMENT '姓名/昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    dept_id BIGINT COMMENT '所属部门ID',
    position VARCHAR(50) COMMENT '职位',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色: user-普通执行人员/manager-部门主管/admin-督办管理员',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ========== 2. 部门表 ==========
CREATE TABLE IF NOT EXISTS sys_dept (
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
CREATE TABLE IF NOT EXISTS sys_role (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    data_scope INT DEFAULT 1 COMMENT '数据范围: 1-全部 2-本部门 3-仅本人',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-停用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ========== 4. 用户角色关联 ==========
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联';

-- ========== 5. 任务组表 ==========
CREATE TABLE IF NOT EXISTS task_group (
    group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    dept_id BIGINT COMMENT '所属部门ID',
    group_type INT DEFAULT 1 COMMENT '组类型: 1-普通 2-专项',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-停用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务组';

-- ========== 6. 任务表 ==========
CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending待接收/in_progress进行中/pending_feedback待反馈/pending_accept待验收/completed已完成/overdue已逾期',
    priority VARCHAR(20) DEFAULT 'medium' COMMENT '优先级: high高/medium中/low低',
    dept_id BIGINT COMMENT '所属部门ID',
    template_id BIGINT COMMENT '关联模板ID',
    assignee_mode INT DEFAULT 1 COMMENT '分派模式: 1-单人 2-多人协办',
    group_id BIGINT COMMENT '任务组ID',
    deadline TIMESTAMP NULL COMMENT '截止时间',
    remark VARCHAR(500) COMMENT '备注',
    reject_remark VARCHAR(500) COMMENT '驳回原因',
    rejected_at TIMESTAMP NULL COMMENT '驳回时间',
    accept_result INT DEFAULT 0 COMMENT '验收结果: 0待验收 1通过 2驳回',
    accept_remark VARCHAR(500) COMMENT '验收意见',
    accepted_at TIMESTAMP NULL COMMENT '验收时间',
    accepted_by BIGINT COMMENT '验收人ID',
    attachments VARCHAR(2000) COMMENT '附件(多个URL以逗号分隔)',
    creator_id BIGINT COMMENT '创建人ID',
    creator_name VARCHAR(50) COMMENT '创建人姓名',
    assignee_id BIGINT COMMENT '主指派人ID',
    assignee_name VARCHAR(50) COMMENT '主指派人姓名',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    overdue_marked TINYINT DEFAULT 0 COMMENT '逾期标记 0-未标记 1-已标记',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_creator (creator_id),
    INDEX idx_assignee (assignee_id),
    INDEX idx_dept (dept_id),
    INDEX idx_template (template_id),
    INDEX idx_group (group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- ========== 7. 预警规则表 ==========
CREATE TABLE IF NOT EXISTS warn_rule (
    rule_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL,
    level INT DEFAULT 1 COMMENT '预警级别 1-普通 2-重要 3-紧急',
    before_days INT COMMENT '提前提醒天数',
    push_frequency VARCHAR(20) COMMENT '推送频率: once-一次 daily-每天',
    target_roles VARCHAR(200) COMMENT '目标角色(逗号分隔)',
    enabled TINYINT DEFAULT 1 COMMENT '是否启用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警规则';

-- ========== 8. 预警记录表 ==========
CREATE TABLE IF NOT EXISTS warn_record (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    rule_id BIGINT,
    level INT,
    warn_content VARCHAR(1000),
    push_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警记录';

-- ========== 9. 站内消息表 ==========
CREATE TABLE IF NOT EXISTS in_app_message (
    msg_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    content VARCHAR(2000),
    level INT DEFAULT 1 COMMENT '消息级别 1-普通 2-重要 3-紧急',
    msg_type VARCHAR(20) COMMENT '消息类型 TASK-任务通知 WARN-预警 ACCEPT-验收通知',
    related_id BIGINT COMMENT '关联任务ID，用于点击跳转',
    read_status TINYINT DEFAULT 0 COMMENT '0-未读 1-已读',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_read (user_id, read_status),
    INDEX idx_related (related_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息';

-- ========== 10. 进度反馈表 ==========
CREATE TABLE IF NOT EXISTS progress_feedback (
    feedback_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL COMMENT '关联任务ID',
    user_id BIGINT COMMENT '反馈人ID',
    user_name VARCHAR(50) COMMENT '反馈人姓名',
    completed_content TEXT COMMENT '当期完成内容',
    next_plan TEXT COMMENT '下一步工作计划',
    progress_percent INT DEFAULT 0 COMMENT '进度百分比(0-100)',
    stage INT DEFAULT 1 COMMENT '反馈阶段/轮次',
    feedback_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '反馈时间',
    INDEX idx_task_id (task_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='进度反馈';

-- ========== 11. 任务文件表 ==========
CREATE TABLE IF NOT EXISTS task_file (
    file_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT COMMENT '关联任务ID',
    feedback_id BIGINT COMMENT '关联反馈ID',
    original_name VARCHAR(200) COMMENT '原始文件名',
    stored_name VARCHAR(500) COMMENT '存储文件名',
    file_path VARCHAR(1000) COMMENT '文件路径',
    file_size BIGINT COMMENT '文件大小(字节)',
    file_type VARCHAR(50) COMMENT '文件类型扩展名',
    uploader_id BIGINT COMMENT '上传人ID',
    uploader_name VARCHAR(50) COMMENT '上传人姓名',
    encrypt_hash VARCHAR(64) COMMENT '文件哈希(防篡改)',
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
    INDEX idx_task (task_id),
    INDEX idx_feedback (feedback_id),
    INDEX idx_uploader (uploader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务文件';

-- ========== 12. 验收表 ==========
CREATE TABLE IF NOT EXISTS acceptance (
    accept_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    applicant_id BIGINT COMMENT '申请人ID',
    applicant_name VARCHAR(50) COMMENT '申请人姓名',
    acceptor_id BIGINT COMMENT '验收人ID',
    acceptor_name VARCHAR(50) COMMENT '验收人姓名',
    result INT DEFAULT 0 COMMENT '验收结果: 0-待验收 1-通过 2-退回',
    opinion VARCHAR(1000) COMMENT '验收意见',
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    accept_time TIMESTAMP NULL COMMENT '验收时间',
    INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='验收表';

-- ========== 13. 流程配置表 ==========
CREATE TABLE IF NOT EXISTS flow_config (
    flow_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flow_name VARCHAR(100) NOT NULL,
    dept_id BIGINT COMMENT '所属部门ID',
    node_config TEXT COMMENT '节点配置(JSON)',
    status TINYINT DEFAULT 1 COMMENT '状态 1-启用 0-停用',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程配置';

-- ========== 14. 逾期问责表 ==========
CREATE TABLE IF NOT EXISTS overdue_accountability (
    overdue_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    reason VARCHAR(1000) COMMENT '逾期原因',
    disposition VARCHAR(500) COMMENT '处置措施',
    overdue_days INT COMMENT '逾期天数',
    archive_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '归档时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='逾期问责';

-- ========== 15. 整改任务表 ==========
CREATE TABLE IF NOT EXISTS rectify_task (
    rectify_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    accept_id BIGINT COMMENT '关联验收ID',
    rectify_reason VARCHAR(1000) COMMENT '整改原因',
    rectify_opinion VARCHAR(1000) COMMENT '整改意见',
    status INT DEFAULT 0 COMMENT '状态: 0-待整改 1-处理中 2-已重新提交',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    complete_time TIMESTAMP NULL COMMENT '完成时间',
    INDEX idx_task (task_id),
    INDEX idx_accept (accept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='整改任务';

-- ========== 16. 操作日志表 ==========
CREATE TABLE IF NOT EXISTS operation_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    module VARCHAR(50) COMMENT '模块: task/feedback/acceptance/warn/file',
    action VARCHAR(50) COMMENT '操作类型: CREATE/UPDATE/DELETE/VERIFY/UPLOAD/REJECT',
    task_id BIGINT COMMENT '关联任务ID',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    dept_id BIGINT COMMENT '操作人部门ID',
    detail TEXT COMMENT '操作详情',
    encrypted_content VARCHAR(64) COMMENT 'SHA-256哈希值(防篡改)',
    ip VARCHAR(50) COMMENT '操作人IP',
    operate_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_module_time (module, operate_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

-- ========== 17. 统计报表表 ==========
CREATE TABLE IF NOT EXISTS statistics_report (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    period VARCHAR(20) COMMENT '统计周期: day/week/month/quarter/year',
    period_value VARCHAR(20) COMMENT '周期值(如 2026-09)',
    dept_id BIGINT COMMENT '部门ID',
    user_id BIGINT COMMENT '用户ID',
    total_dispatch INT DEFAULT 0 COMMENT '总派发数',
    on_time_rate DECIMAL(5,2) DEFAULT 0 COMMENT '按时完成率(%)',
    overdue_count INT DEFAULT 0 COMMENT '逾期数',
    avg_complete_days DECIMAL(10,2) DEFAULT 0 COMMENT '平均完成天数',
    generate_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统计报表';

-- ========== 18. 任务指派表 ==========
CREATE TABLE IF NOT EXISTS task_assignee (
    assignee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    assignee_name VARCHAR(50),
    assignee_type INT DEFAULT 1 COMMENT '指派类型: 1-主负责人 2-协助人',
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task (task_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务指派';

-- ========== 19. 任务模板表 ==========
CREATE TABLE IF NOT EXISTS task_template (
    template_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    template_type INT DEFAULT 1 COMMENT '模板类型: 1-行政 2-项目 3-整改 4-会议 5-客户对接',
    description VARCHAR(500),
    default_content TEXT COMMENT '默认任务内容',
    default_priority INT DEFAULT 1 COMMENT '默认优先级: 1-普通 2-重要 3-紧急',
    standard_feedback_req TEXT COMMENT '标准反馈要求',
    group_id BIGINT COMMENT '任务组ID',
    status INT DEFAULT 1 COMMENT '状态: 1-启用 0-停用',
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务模板';

-- ========== 20. 任务模板字段表 ==========
CREATE TABLE IF NOT EXISTS task_template_field (
    field_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT NOT NULL,
    field_name VARCHAR(100) COMMENT '字段名称',
    field_key VARCHAR(100) COMMENT '字段标识',
    field_type VARCHAR(50) COMMENT '字段类型: text/textarea/number/date/select/file/checkbox/radio',
    required TINYINT DEFAULT 0 COMMENT '是否必填: 0-否 1-是',
    default_value VARCHAR(500) COMMENT '默认值',
    options TEXT COMMENT '字段选项(JSON格式，用于select/radio/checkbox)',
    placeholder VARCHAR(200) COMMENT '提示信息',
    sort INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_template (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务模板字段';

-- ========== 21. 任务进度节点表 ==========
CREATE TABLE IF NOT EXISTS task_progress_node (
    node_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    node_name VARCHAR(100) COMMENT '节点名称',
    stage INT DEFAULT 1 COMMENT '阶段序号',
    plan_date DATE COMMENT '计划日期',
    status INT DEFAULT 0 COMMENT '状态: 0-未开始 1-进行中 2-已完成',
    INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务进度节点';
