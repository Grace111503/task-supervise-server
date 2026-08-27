-- 初始用户数据 (开发模式密码均为明文 123456，AuthServiceImpl 有明文兜底便于测试)
INSERT INTO sys_user (username, password, name, email, phone, avatar, role) VALUES
('admin', '123456', '系统管理员', 'admin@example.com', '13800000001', '', 'admin'),
('manager', '123456', '项目经理', 'manager@example.com', '13800000002', '', 'admin'),
('user1', '123456', '张三', 'zhangsan@example.com', '13800000003', '', 'user'),
('user2', '123456', '李四', 'lisi@example.com', '13800000004', '', 'user');

-- 初始任务数据
INSERT INTO task (title, description, status, priority, deadline, remark, creator_id, creator_name, assignee_id, assignee_name) VALUES
('完成项目需求文档', '整理并输出项目需求规格说明书，含功能清单和原型说明', 'pending', 'high', '2026-09-15 18:00:00', '需与产品经理确认', 1, '系统管理员', 3, '张三'),
('用户登录模块开发', '实现用户名密码登录、邮箱登录、token刷新等接口', 'in_progress', 'high', '2026-09-10 18:00:00', '注意密码加密', 2, '项目经理', 3, '张三'),
('任务管理页面联调', '前端页面与后端接口联调测试', 'pending', 'medium', '2026-09-20 18:00:00', '', 2, '项目经理', 4, '李四'),
('数据库性能优化', '对慢查询进行分析并建立合适索引', 'completed', 'medium', '2026-08-20 18:00:00', '已完成，性能提升40%', 1, '系统管理员', 4, '李四'),
('旧版数据迁移脚本编写', '将历史数据迁移到新版本表结构', 'overdue', 'high', '2026-08-25 18:00:00', '已逾期，需要加快进度', 2, '项目经理', 3, '张三'),
('编写单元测试', '为核心业务逻辑编写单元测试用例，覆盖率不低于80%', 'pending', 'low', '2026-09-30 18:00:00', '', 1, '系统管理员', 4, '李四');
