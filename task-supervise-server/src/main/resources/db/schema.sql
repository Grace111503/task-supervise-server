-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 任务表
CREATE TABLE IF NOT EXISTS task (
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
);
