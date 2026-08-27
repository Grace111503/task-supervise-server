-- 初始用户数据
-- 开发模式密码均为明文 123456，AuthServiceImpl 有明文兜底便于测试
INSERT INTO sys_user (id, username, password, name, email, phone, avatar, role) VALUES
(1, 'admin', '123456', '系统管理员', 'admin@example.com', '13800000001', '', 'admin'),
(2, 'manager', '123456', '项目经理', 'manager@example.com', '13800000002', '', 'admin'),
(3, 'user1', '123456', '张三', 'zhangsan@example.com', '13800000003', '', 'user'),
(4, 'user2', '123456', '李四', 'lisi@example.com', '13800000004', '', 'user');

-- 初始角色数据
INSERT INTO sys_role (role_id, role_name, role_code, description) VALUES
(1, '系统管理员', 'admin', '系统最高权限'),
(2, '项目经理', 'manager', '项目管理权限'),
(3, '普通用户', 'user', '基本操作权限');

-- 用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 3);

-- 初始部门数据
INSERT INTO sys_dept (dept_id, parent_id, dept_name, sort, leader, phone) VALUES
(1, 0, '总公司', 0, '张总', '13800000000'),
(2, 1, '技术部', 1, '李工', '13800000001'),
(3, 1, '运营部', 2, '王运营', '13800000002');

-- 初始任务数据
INSERT INTO task (id, title, description, status, priority, deadline, remark, creator_id, creator_name, assignee_id, assignee_name) VALUES
(1, '完成项目需求文档', '整理并输出项目需求规格说明书，含功能清单和原型说明', 'pending', 'high', '2026-09-15 18:00:00', '需与产品经理确认', 1, '系统管理员', 3, '张三'),
(2, '用户登录模块开发', '实现用户名密码登录、邮箱登录、token刷新等接口', 'in_progress', 'high', '2026-09-10 18:00:00', '注意密码加密', 2, '项目经理', 3, '张三'),
(3, '任务管理页面联调', '前端页面与后端接口联调测试', 'pending', 'medium', '2026-09-20 18:00:00', '', 2, '项目经理', 4, '李四'),
(4, '数据库性能优化', '对慢查询进行分析并建立合适索引', 'completed', 'medium', '2026-08-20 18:00:00', '已完成，性能提升40%', 1, '系统管理员', 4, '李四'),
(5, '旧版数据迁移脚本编写', '将历史数据迁移到新版本表结构', 'overdue', 'high', '2026-08-25 18:00:00', '已逾期，需要加快进度', 2, '项目经理', 3, '张三'),
(6, '编写单元测试', '为核心业务逻辑编写单元测试用例，覆盖率不低于80%', 'pending', 'low', '2026-09-30 18:00:00', '', 1, '系统管理员', 4, '李四');

-- 初始预警规则
INSERT INTO warn_rule (rule_id, rule_name, condition_expr, level, enabled) VALUES
(1, '任务逾期预警', 'task.deadline < now() AND task.status != "completed"', 2, 1),
(2, '任务长期未更新', 'task.updated_at < now() - interval 7 days', 1, 1);

-- 初始流程配置
INSERT INTO flow_config (flow_id, flow_name, dept_id, config_json) VALUES
(1, '标准验收流程', 1, '{"steps":[{"role":"manager","timeoutHours":48},{"role":"admin","timeoutHours":24}]}');
