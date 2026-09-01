-- 初始用户数据（三级权限体系）
-- 角色: user-普通执行人员 / manager-部门主管 / admin-督办管理员
-- 使用 INSERT IGNORE 避免主键冲突报错，可安全重复执行
INSERT IGNORE INTO sys_user (id, username, password, name, email, phone, avatar, dept_id, position, role) VALUES
(1, 'admin', '123456', '系统管理员', 'admin@example.com', '13800000000', '', 1, '系统管理员', 'admin'),
(2, 'manager', '123456', '项目经理', 'manager@example.com', '13800000001', '', 2, '技术部主管', 'manager'),
(3, 'user1', '123456', '张三', 'zhangsan@example.com', '13800000002', '', 2, '开发工程师', 'user'),
(4, 'user2', '123456', '李四', 'lisi@example.com', '13800000003', '', 3, '运营专员', 'user'),
(5, 'manager2', '123456', '王运营', 'wangyy@example.com', '13800000004', '', 3, '运营部主管', 'manager'),
(6, 'user3', '123456', '赵六', 'zhaoliu@example.com', '13800000005', '', 2, '测试工程师', 'user'),
(7, 'user4', '123456', '钱七', 'qianqi@example.com', '13800000006', '', 3, '市场专员', 'user');

-- 初始角色数据（三级权限）
INSERT IGNORE INTO sys_role (role_id, role_name, role_code, description) VALUES
(1, '督办管理员', 'admin', '全公司任务查看、全局督办、系统参数配置全部管理权限'),
(2, '部门主管', 'manager', '创建分派本部门任务、验收本部门任务、查看部门任务统计'),
(3, '普通执行人员', 'user', '仅可查看自身任务、提交进度反馈');

-- 用户角色关联
INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 3),
(5, 2),
(6, 3),
(7, 3);

-- 初始部门数据（树形结构）
INSERT IGNORE INTO sys_dept (dept_id, parent_id, dept_name, sort, leader, phone) VALUES
(1, 0, '总公司', 0, '张总', '13800000000'),
(2, 1, '技术部', 1, '项目经理', '13800000001'),
(3, 1, '运营部', 2, '王运营', '13800000004'),
(4, 1, '市场部', 3, '孙市场', '13800000007');

-- 初始任务数据（按部门归属，用于权限过滤）
INSERT IGNORE INTO task (id, title, description, status, priority, dept_id, deadline, remark, creator_id, creator_name, assignee_id, assignee_name) VALUES
(1, '完成项目需求文档', '整理并输出项目需求规格说明书，含功能清单和原型说明', 'pending', 'high', 1, '2026-09-15 18:00:00', '需与产品经理确认', 1, '系统管理员', 3, '张三'),
(2, '用户登录模块开发', '实现用户名密码登录、邮箱登录、token刷新等接口', 'in_progress', 'high', 2, '2026-09-10 18:00:00', '注意密码加密', 2, '项目经理', 3, '张三'),
(3, '任务管理页面联调', '前端页面与后端接口联调测试', 'pending', 'medium', 2, '2026-09-20 18:00:00', '', 2, '项目经理', 4, '李四'),
(4, '数据库性能优化', '对慢查询进行分析并建立合适索引', 'completed', 'medium', 1, '2026-08-20 18:00:00', '已完成，性能提升40%', 1, '系统管理员', 4, '李四'),
(5, '旧版数据迁移脚本编写', '将历史数据迁移到新版本表结构', 'overdue', 'high', 2, '2026-08-25 18:00:00', '已逾期，需要加快进度', 2, '项目经理', 6, '赵六'),
(6, '编写单元测试', '为核心业务逻辑编写单元测试用例，覆盖率不低于80%', 'pending', 'low', 2, '2026-09-30 18:00:00', '', 1, '系统管理员', 6, '赵六'),
(7, '运营活动策划方案', '撰写Q4运营活动策划方案，包括推广渠道和预算', 'pending', 'high', 3, '2026-09-25 18:00:00', '需市场部配合', 5, '王运营', 7, '钱七'),
(8, '用户增长数据分析', '分析近3个月用户增长数据，输出增长报告', 'in_progress', 'medium', 3, '2026-09-18 18:00:00', '', 5, '王运营', 4, '李四');

-- 初始预警规则
INSERT IGNORE INTO warn_rule (rule_id, rule_name, condition_expr, level, enabled) VALUES
(1, '任务逾期预警', 'task.deadline < now() AND task.status != "completed"', 2, 1),
(2, '任务长期未更新', 'task.updated_at < now() - interval 7 days', 1, 1);

-- 初始流程配置
INSERT IGNORE INTO flow_config (flow_id, flow_name, dept_id, config_json) VALUES
(1, '标准验收流程', 1, '{"steps":[{"role":"manager","timeoutHours":48},{"role":"admin","timeoutHours":24}]}');
