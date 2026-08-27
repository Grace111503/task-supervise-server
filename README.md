# 企业任务督办跟踪管理 APP - Code Wiki

> 版本：v1.0
> 适用范围：跨安卓 / iOS 移动端任务督办跟踪与完成反馈管理 APP
> 文档定位：项目整体架构、模块职责、关键类与函数、依赖关系、运行方式的完整结构化说明文档

***

## 目录

- [1. 项目概述](#1-项目概述)
    - [1.1 项目背景](#11-项目背景)
    - [1.2 技术目标](#12-技术目标)
    - [1.3 业务价值](#13-业务价值)
- [2. 整体架构](#2-整体架构)
    - [2.1 技术栈总览](#21-技术栈总览)
    - [2.2 系统架构图](#22-系统架构图)
    - [2.3 分层架构](#23-分层架构)
    - [2.4 部署架构](#24-部署架构)
- [3. 七大业务模块职责](#3-七大业务模块职责)
    - [3.1 企业组织架构与分级角色权限管理模块](#31-企业组织架构与分级角色权限管理模块)
    - [3.2 多类型标准化任务模板与批量创建分派模块](#32-多类型标准化任务模板与批量创建分派模块)
    - [3.3 任务进度分阶段线上反馈与成果材料上传模块](#33-任务进度分阶段线上反馈与成果材料上传模块)
    - [3.4 任务全生命周期状态可视化跟踪模块](#34-任务全生命周期状态可视化跟踪模块)
    - [3.5 三级任务到期预警与自动督办提醒模块](#35-三级任务到期预警与自动督办提醒模块)
    - [3.6 任务完成验收、退回整改与逾期追责闭环模块](#36-任务完成验收退回整改与逾期追责闭环模块)
    - [3.7 操作日志与任务督办运营统计报表模块](#37-操作日志与任务督办运营统计报表模块)
- [4. 关键类与函数说明](#4-关键类与函数说明)
    - [4.1 后端核心类](#41-后端核心类)
    - [4.2 后端核心函数](#42-后端核心函数)
    - [4.3 前端核心组件](#43-前端核心组件)
- [5. 数据库设计](#5-数据库设计)
    - [5.1 核心数据表清单](#51-核心数据表清单)
    - [5.2 关键表结构](#52-关键表结构)
    - [5.3 表关系说明](#53-表关系说明)
- [6. 接口设计](#6-接口设计)
    - [6.1 RESTful 接口规范](#61-restful-接口规范)
    - [6.2 核心接口清单](#62-核心接口清单)
- [7. 依赖关系](#7-依赖关系)
    - [7.1 后端依赖](#71-后端依赖)
    - [7.2 前端依赖](#72-前端依赖)
    - [7.3 中间件依赖](#73-中间件依赖)
    - [7.4 模块间依赖](#74-模块间依赖)
- [8. 项目运行方式](#8-项目运行方式)
    - [8.1 环境准备](#81-环境准备)
    - [8.2 本地开发启动](#82-本地开发启动)
    - [8.3 打包发布](#83-打包发布)
    - [8.4 部署上线](#84-部署上线)
- [9. 目录结构](#9-目录结构)
- [10. 开发规范](#10-开发规范)

***

## 1. 项目概述

### 1.1 项目背景

企业内部行政、项目、跨部门协同过程中，普遍存在以下任务督办管理痛点：

- 任务口头分派无留存，责任划分模糊
- 任务进度无法实时跟进，到期任务无自动督办
- 执行人员完成工作无标准化反馈渠道，验收流程缺失
- 大量逾期任务无完整记录，管理层无法统一查看全员任务执行情况

本项目旨在研发一套跨安卓、iOS 移动端的任务督办跟踪与完成反馈管理 APP，实现任务创建分派、任务分级优先级标记、全过程进度跟踪、到期分级督办提醒、执行人员线上进度反馈、完成成果上传验收、逾期任务追责记录、任务执行数据统计的一体化移动端闭环管控。

### 1.2 技术目标

1. 跨安卓、iOS 移动端任务督办跟踪与完成反馈管理 APP，覆盖任务创建分派、分级优先级标记、全过程进度跟踪、到期分级督办提醒、执行人员线上进度反馈、完成成果上传验收、逾期任务追责记录、任务执行数据统计一体化移动端闭环管控。
2. 企业管理层、部门主管可随时创建、分派各类工作任务，设置完成时限、优先级、协同参与人员；一线执行人员可实时接收分派任务，定期线上填报工作进度、上传阶段性工作成果；督办人员、主管可实时跟踪全部任务执行状态，接收到期、逾期督办提醒，线上验收已完成任务。
3. 搭建企业组织架构与人员权限管理体系，自动同步企业部门、员工组织信息，划分督办管理员、部门主管、普通执行人员三级角色权限；支持按部门、项目组创建专属任务分组。
4. 内置行政办公、项目推进、客户对接、会议待办、整改落实多类任务模板，支持单人分派、多人协同分派两种模式，自定义任务完成截止时间、三级优先级（普通 / 重要 / 紧急）、阶段性进度填报节点。
5. 构建任务全过程进度跟踪与线上反馈模块，支持分阶段填报工作推进进度，上传 Word/Excel/PDF 文档、图片、短视频等各类工作佐证成果，所有进度反馈、成果材料永久绑定对应任务。
6. 搭建任务到期分级督办、逾期追责闭环体系，自定义三级预警机制（到期前 7 天普通提醒、到期前 3 天重要督办、任务逾期每日高频推送紧急督办提醒）；任务完成后执行人员提交验收申请，督办 / 主管线上核验工作成果，验收不达标退回补充完善。
7. 完整留存任务创建分派、进度反馈、成果上传、督办提醒、验收处置、逾期追责全部操作日志；按月、季度、分部门、分人员统计任务派发总量、任务按期完成率、逾期任务占比、任务平均完成时长，生成企业任务督办综合统计报表。

### 1.3 业务价值

- 解决企业任务口头分派无留存、责任划分模糊问题
- 解决任务进度无法实时跟进、到期任务无自动督办问题
- 解决执行人员完成工作无标准化反馈渠道、验收流程缺失问题
- 解决大量逾期任务无完整记录、管理层无法统一查看全员任务执行情况问题
- 为企业员工工作绩效考核、部门工作效率优化提供完整数据支撑

***

## 2. 整体架构

### 2.1 技术栈总览

| 层级   | 技术选型                                  | 说明                                      |
| ---- | ------------------------------------- | --------------------------------------- |
| 移动端  | UniApp (Vue3) + uView UI              | 一套代码同步发布安卓、iOS 安装包，手机、平板均可适配            |
| 后端   | Spring Boot 3 + JDK 21 + MyBatis-Plus | 提供业务接口与流程引擎                             |
| 数据库  | MySQL 8                               | 存储人员组织、任务信息、进度反馈、督办记录、验收台账核心业务数据        |
| 缓存   | Redis 7                               | 缓存高频访问任务列表、人员基础信息，提升 APP 页面加载速度         |
| 对象存储 | MinIO / 阿里云 OSS                       | 安全存储工作反馈文档、图片、短视频成果材料，文件加密上传存储          |
| 消息队列 | RabbitMQ                              | 实现任务预警、督办提醒实时推送 APP 站内通知，保障提醒消息送达及时性    |
| 流程引擎 | 轻量化自研流程引擎                             | 可视化配置任务验收、逾期处置流程节点，适配企业不同部门差异化任务验收审批规则  |
| 离线缓存 | UniApp 本地存储 + SQLite                  | 弱网办公环境缓存任务基础信息、未提交进度反馈内容，断网可离线编辑，联网自动同步 |
| 构建工具 | Maven (后端) + HBuilderX (前端)           | 后端依赖管理与打包；前端打包到安卓/iOS                   |
| 部署   | Docker + Nginx                        | 容器化部署，Nginx 反向代理与静态资源服务                 |

### 2.2 系统架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                       移动端 (UniApp)                            │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌─────────┐ │
│  │  任务列表页  │ │  任务详情页  │ │  进度反馈页  │ │ 统计报表│ │
│  └──────────────┘ └──────────────┘ └──────────────┘ └─────────┘ │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌─────────┐ │
│  │  验收整改页  │ │  逾期追责页  │ │  组织架构页  │ │ 离线缓存│ │
│  └──────────────┘ └──────────────┘ └──────────────┘ └─────────┘ │
└───────────────────────────────┬─────────────────────────────────┘
                                │ HTTPS (RESTful + WebSocket)
┌───────────────────────────────▼─────────────────────────────────┐
│                        Nginx 反向代理                            │
└───────────────────────────────┬─────────────────────────────────┘
                                │
┌───────────────────────────────▼─────────────────────────────────┐
│                    Spring Boot 应用服务                         │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌──────────────┐  │
│  │ 组织权限   │ │ 任务模板   │ │ 进度反馈   │ │ 状态跟踪     │  │
│  │ 模块       │ │ 分派模块   │ │ 上传模块   │ │ 模块         │  │
│  └────────────┘ └────────────┘ └────────────┘ └──────────────┘  │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌──────────────┐  │
│  │ 预警督办   │ │ 验收整改   │ │ 日志统计   │ │ 轻量流程引擎│  │
│  │ 模块       │ │ 追责模块   │ │ 报表模块   │ │              │  │
│  └────────────┘ └────────────┘ └────────────┘ └──────────────┘  │
└──┬──────────┬──────────┬──────────┬──────────┬──────────┬──────┘
   │          │          │          │          │          │
┌──▼───┐  ┌───▼──┐  ┌────▼───┐  ┌───▼────┐  ┌──▼────┐  ┌──▼─────┐
│MySQL │  │Redis │  │ MinIO  │  │RabbitMQ│  │定时调度│  │WebSocket│
│  8   │  │  7   │  │ / OSS  │  │        │  │ XxlJob│  │ 推送    │
└──────┘  └──────┘  └────────┘  └────────┘  └───────┘  └────────┘
```

### 2.3 分层架构

后端采用经典分层架构：

```
┌─────────────────────────────────────────────┐
│   Controller 层 (接口层)                     │
│   - 接收 APP 请求, 参数校验, 统一返回格式        │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│   Service 层 (业务层)                         │
│   - 业务逻辑编排, 事务管理, 跨模块协作            │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│   Domain / Manager 层 (领域层)               │
│   - 领域模型, 业务规则, 流程引擎                │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│   Mapper / Repository 层 (数据访问层)         │
│   - MyBatis-Plus, 数据持久化                  │
└─────────────────────────────────────────────┘
```

### 2.4 部署架构

- 单体应用容器化部署（Docker）
- Nginx 作为反向代理与前端静态资源服务
- MySQL、Redis、MinIO、RabbitMQ 可独立部署或容器编排
- 移动端 APP 通过 HTTPS 访问后端服务
- 定时任务调度服务（XxlJob）独立部署，负责预警、督办、统计任务

***

## 3. 七大业务模块职责

### 3.1 企业组织架构与分级角色权限管理模块

**模块定位：** 基础支撑模块，为其他业务模块提供人员、部门、角色权限数据基础。

**核心职责：**

1. 自动录入企业部门、员工组织信息，支持人员新增、调岗、离职归档管理。
2. 划分三级权限：
    - **普通执行人员**：仅可查看自身任务、提交进度反馈
    - **部门主管**：可创建分派本部门任务、验收本部门任务、查看部门任务统计
    - **督办管理员**：拥有全公司任务查看、全局督办、系统参数配置全部管理权限
3. 支持按部门、项目组创建专属任务分组，任务自动归属对应分组管理，实现任务分类精细化管控。

**关键能力：**

- 部门树形结构管理（增删改查）
- 员工信息管理（新增、调岗、离职归档）
- 角色权限分配与鉴权
- 任务分组（按部门 / 项目组）管理
- 组织信息与企业 IM / HR 系统同步对接

### 3.2 多类型标准化任务模板与批量创建分派模块

**模块定位：** 任务生命周期的起点，负责任务的标准化创建与分派。

**核心职责：**

1. 内置行政、项目、整改、会议、客户对接多类任务模板，模板可自定义表单字段、标准反馈要求。
2. 支持单条任务手动创建、多条同类任务 Excel 批量导入创建。
3. 支持单人分派、多人协同协办模式，设置任务截止时间、三级优先级（普通 / 重要 / 紧急）、阶段性进度填报节点。
4. 批量任务可一键批量分派至对应执行人员，减少管理人员重复操作。

**关键能力：**

- 任务模板 CRUD 与字段定义
- 任务单条创建（手动填写）
- 任务批量导入（Excel 解析）
- 单人分派 / 多人协办模式
- 截止时间、优先级、进度节点配置
- 批量分派与任务分组归属

### 3.3 任务进度分阶段线上反馈与成果材料上传模块

**模块定位：** 任务执行阶段的核心交互模块，承载执行人员与任务的持续交互。

**核心职责：**

1. 执行人员可多次分阶段提交进度反馈，填写当期工作完成详情、后续工作计划。
2. 支持多格式文件（Word/Excel/PDF）、图片、短视频上传工作成果佐证。
3. 所有反馈记录、上传文件自动绑定对应任务，不可删除篡改。
4. 主管、督办人员可随时查看全部历史进度反馈与成果材料。

**关键能力：**

- 分阶段进度反馈提交（当期完成内容 + 下一步计划）
- 多格式成果材料上传（文档 / 图片 / 短视频）
- 成果材料与任务永久绑定（不可删除篡改）
- 历史进度反馈查询
- 进度反馈实时同步至创建人、督办管理员
- 弱网离线编辑，联网自动同步

### 3.4 任务全生命周期状态可视化跟踪模块

**模块定位：** 任务执行过程的可视化呈现层，面向不同角色提供差异化视图。

**核心职责：**

1. APP 首页集中展示全部任务分类卡片：待接收、进行中、待反馈、待验收、已完成、已逾期六大状态。
2. 直观展示各任务优先级、剩余工期、逾期时长。
3. 单任务详情页面完整展示任务创建信息、分派人员、全部历史进度反馈、督办提醒记录、验收处置意见，完整还原任务全流程执行过程。

**关键能力：**

- 六大状态任务卡片聚合展示
- 任务优先级、剩余工期、逾期时长直观标识
- 任务详情全流程还原
- 多角色差异化任务视图（执行人 / 主管 / 督办）
- 任务列表分页与筛选

### 3.5 三级任务到期预警与自动督办提醒模块

**模块定位：** 任务到期前的主动干预模块，保障任务按时推进。

**核心职责：**

1. 自定义三级预警规则：
    - **到期前 7 天**：推送普通提醒
    - **到期前 3 天**：推送重要督办提醒
    - **任务逾期每日**：推送紧急督办提醒
2. 提醒以 APP 站内消息推送至任务执行人、创建主管、督办管理员。
3. 逾期任务自动高亮标记，汇总逾期任务清单，支持批量查看、批量处置逾期任务。

**关键能力：**

- 三级预警规则配置
- 定时任务扫描剩余工期
- APP 站内消息推送（WebSocket / 长连接）
- 逾期任务自动高亮与清单汇总
- 批量查看与批量处置逾期任务

### 3.6 任务完成验收、退回整改与逾期追责闭环模块

**模块定位：** 任务生命周期的终点，形成完整闭环。

**核心职责：**

1. 执行人员全部工作完成后，线上提交验收申请，附带全套工作成果材料。
2. 主管 / 督办线上核验成果，填写验收意见，分为验收通过、退回整改两类结果。
3. 退回整改的任务自动生成二次待办，推送至执行人员补充完善后重新提交验收。
4. 逾期任务页面支持线上登记逾期客观原因、人员处置追责记录，逾期台账永久归档留存，作为人员绩效考核依据。

**关键能力：**

- 验收申请提交（附带成果材料）
- 线上验收核验与意见填写
- 验收通过 / 退回整改两类结果
- 退回整改任务自动生成二次待办
- 逾期原因登记与追责处置记录
- 逾期台账永久归档
- 轻量化流程引擎支撑差异化验收审批规则

### 3.7 操作日志与任务督办运营统计报表模块

**模块定位：** 全局审计与数据支撑模块，为绩效考核提供数据基础。

**核心职责：**

1. 完整记录 APP 内任务创建、分派、进度反馈、成果上传、督办提醒、验收、逾期追责所有操作。
2. 日志加密云端存储不可篡改，支持按部门、人员、时间区间检索审计。
3. 系统周期汇总全企业任务数据，生成任务派发总量、按期完成率、逾期任务数量、任务平均办结时长可视化统计报表。
4. APP 端支持在线查看、导出报表文件。

**关键能力：**

- 全操作行为日志采集（加密存储不可篡改）
- 日志按部门 / 人员 / 时间区间检索审计
- 周期性任务数据汇总（月 / 季度 / 部门 / 人员）
- 可视化统计报表（派发总量、按期完成率、逾期数量、平均办结时长）
- 报表在线查看与文件导出

***

## 4. 关键类与函数说明

### 4.1 后端核心类

> 包结构约定：`com.enterprise.task督办.*`

#### 4.1.1 组织权限模块

| 类名                    | 类型         | 职责                                                       |
| --------------------- | ---------- | -------------------------------------------------------- |
| `SysDeptController`   | Controller | 部门树形结构增删改查接口                                             |
| `SysUserController`   | Controller | 员工信息管理、调岗、离职归档接口                                         |
| `SysRoleController`   | Controller | 角色与权限分配接口                                                |
| `TaskGroupController` | Controller | 任务分组（部门 / 项目组）管理接口                                       |
| `SysDeptService`      | Service    | 部门业务逻辑，维护部门树形结构                                          |
| `SysUserService`      | Service    | 员工业务逻辑，调岗离职流程                                            |
| `SysRoleService`      | Service    | 角色权限业务逻辑，三级权限分配                                          |
| `TaskGroupService`    | Service    | 任务分组业务逻辑                                                 |
| `SysDept`             | Entity     | 部门实体（deptId, parentId, deptName, sort, status）           |
| `SysUser`             | Entity     | 员工实体（userId, deptId, userName, phone, roleCode, status）  |
| `SysRole`             | Entity     | 角色实体（roleId, roleCode, roleName, dataScope）              |
| `TaskGroup`           | Entity     | 任务分组实体（groupId, groupName, groupType, deptId, projectId） |
| `SysDeptMapper`       | Mapper     | 部门数据访问                                                   |
| `SysUserMapper`       | Mapper     | 员工数据访问                                                   |
| `RoleEnum`            | Enum       | 三级角色枚举（EXECUTOR、DEPT\_LEADER、SUPERVISOR）                 |

#### 4.1.2 任务模板与分派模块

| 类名                         | 类型         | 职责                                                                                                     |
| -------------------------- | ---------- | ------------------------------------------------------------------------------------------------------ |
| `TaskTemplateController`   | Controller | 任务模板 CRUD 接口                                                                                           |
| `TaskController`           | Controller | 任务单条创建、详情、列表接口                                                                                         |
| `TaskBatchController`      | Controller | 任务 Excel 批量导入、批量分派接口                                                                                   |
| `TaskTemplateService`      | Service    | 模板字段定义与解析                                                                                              |
| `TaskService`              | Service    | 任务创建、分派、查询业务逻辑                                                                                         |
| `TaskBatchService`         | Service    | 批量导入解析与批量分派逻辑                                                                                          |
| `Task`                     | Entity     | 任务实体（taskId, templateId, title, content, priority, deadline, creatorId, assigneeMode, groupId, status） |
| `TaskTemplate`             | Entity     | 任务模板实体（templateId, templateName, category, formFields）                                                 |
| `TaskAssignee`             | Entity     | 任务分派对象实体（taskId, userId, assigneeType: MAIN/COOPERATOR）                                                |
| `TaskBatchImportDTO`       | DTO        | Excel 批量导入数据传输对象                                                                                       |
| `PriorityEnum`             | Enum       | 三级优先级枚举（NORMAL、IMPORTANT、URGENT）                                                                       |
| `TaskTemplateCategoryEnum` | Enum       | 模板类别枚举（ADMIN、PROJECT、RECTIFY、MEETING、CLIENT）                                                           |

#### 4.1.3 进度反馈与成果上传模块

| 类名                           | 类型         | 职责                                                                                        |
| ---------------------------- | ---------- | ----------------------------------------------------------------------------------------- |
| `ProgressFeedbackController` | Controller | 进度反馈提交、查询接口                                                                               |
| `FileController`             | Controller | 成果材料上传、下载接口                                                                               |
| `ProgressFeedbackService`    | Service    | 分阶段进度反馈业务逻辑                                                                               |
| `FileService`                | Service    | 文件加密上传存储，按任务划分目录                                                                          |
| `ProgressFeedback`           | Entity     | 进度反馈实体（feedbackId, taskId, userId, completedContent, nextPlan, feedbackTime, stage）       |
| `TaskFile`                   | Entity     | 任务成果文件实体（fileId, taskId, feedbackId, fileName, filePath, fileType, fileSize, encryptHash） |
| `MinioClient`                | 工具类        | 对象存储客户端封装                                                                                 |

#### 4.1.4 状态跟踪模块

| 类名                        | 类型         | 职责                                                                                        |
| ------------------------- | ---------- | ----------------------------------------------------------------------------------------- |
| `TaskDashboardController` | Controller | 首页六大状态卡片聚合接口                                                                              |
| `TaskDetailController`    | Controller | 单任务详情全流程还原接口                                                                              |
| `TaskDashboardService`    | Service    | 六大状态任务聚合查询                                                                                |
| `TaskDetailService`       | Service    | 任务详情全流程数据组装                                                                               |
| `TaskStatusEnum`          | Enum       | 六大状态枚举（PENDING\_RECEIVE、IN\_PROGRESS、PENDING\_FEEDBACK、PENDING\_ACCEPT、COMPLETED、OVERDUE） |

#### 4.1.5 预警督办模块

| 类名                   | 类型         | 职责                                                                  |
| -------------------- | ---------- | ------------------------------------------------------------------- |
| `WarnRuleController` | Controller | 三级预警规则配置接口                                                          |
| `WarnJob`            | Job        | 定时扫描剩余工期，触发预警                                                       |
| `WarnService`        | Service    | 预警规则匹配与消息推送                                                         |
| `MessagePushService` | Service    | APP 站内消息推送                                                          |
| `WarnRule`           | Entity     | 预警规则实体（ruleId, level, beforeDays, pushFrequency, targetRoles）       |
| `WarnRecord`         | Entity     | 预警记录实体（recordId, taskId, level, pushTime, targetUserId, readStatus） |
| `WarnLevelEnum`      | Enum       | 三级预警枚举（NORMAL\_7D、IMPORTANT\_3D、URGENT\_OVERDUE）                    |

#### 4.1.6 验收整改与追责模块

| 类名                                | 类型         | 职责                                                                              |
| --------------------------------- | ---------- | ------------------------------------------------------------------------------- |
| `AcceptanceController`            | Controller | 验收申请提交、验收核验接口                                                                   |
| `RectifyController`               | Controller | 退回整改、二次待办接口                                                                     |
| `OverdueAccountabilityController` | Controller | 逾期原因登记、追责处置接口                                                                   |
| `FlowEngineService`               | Service    | 轻量化流程引擎，可视化配置验收审批节点                                                             |
| `AcceptanceService`               | Service    | 验收申请与核验业务逻辑                                                                     |
| `RectifyService`                  | Service    | 退回整改与二次待办生成                                                                     |
| `OverdueAccountabilityService`    | Service    | 逾期台账与追责记录                                                                       |
| `Acceptance`                      | Entity     | 验收实体（acceptId, taskId, applicantId, acceptorId, result, opinion, acceptTime）    |
| `RectifyTask`                     | Entity     | 退回整改任务实体（rectifyId, taskId, reason, retryCount）                                 |
| `OverdueAccountability`           | Entity     | 逾期追责实体（accountabilityId, taskId, overdueDays, reason, disposition, archiveTime） |
| `AcceptResultEnum`                | Enum       | 验收结果枚举（PASS、RETURN\_RECTIFY）                                                    |

#### 4.1.7 日志统计模块

| 类名                       | 类型         | 职责                                                                                                 |
| ------------------------ | ---------- | -------------------------------------------------------------------------------------------------- |
| `OperationLogController` | Controller | 操作日志检索审计接口                                                                                         |
| `StatisticsController`   | Controller | 统计报表查询、导出接口                                                                                        |
| `OperationLogService`    | Service    | 全操作日志采集与加密存储                                                                                       |
| `StatisticsService`      | Service    | 周期性任务数据汇总与报表生成                                                                                     |
| `OperationLog`           | Entity     | 操作日志实体（logId, module, operation, operatorId, operatorName, operateTime, encryptedContent）          |
| `StatisticsReport`       | Entity     | 统计报表实体（reportId, period, deptId, userId, totalDispatch, onTimeRate, overdueCount, avgCompleteDays） |

### 4.2 后端核心函数

#### 4.2.1 任务创建分派核心函数

```java
// TaskService
public class TaskService {
    /**
     * 创建并分派任务（单条）
     * @param dto 任务创建数据传输对象
     * @return 任务ID
     */
    Long createAndAssignTask(TaskCreateDTO dto);

    /**
     * 批量分派任务
     * @param taskIds 任务ID列表
     * @param assigneeMap 任务-执行人映射
     */
    void batchAssign(List<Long> taskIds, Map<Long, List<Long>> assigneeMap);
}

// TaskBatchService
public class TaskBatchService {
    /**
     * Excel 批量导入任务
     * @param file Excel 文件
     * @param templateId 模板ID
     * @return 导入结果（成功/失败条数）
     */
    BatchImportResult batchImport(MultipartFile file, Long templateId);
}
```

#### 4.2.2 进度反馈核心函数

```java
// ProgressFeedbackService
public class ProgressFeedbackService {
    /**
     * 提交分阶段进度反馈
     * @param dto 进度反馈数据传输对象
     * @return 反馈记录ID
     */
    Long submitFeedback(ProgressFeedbackDTO dto);

    /**
     * 查询任务全部历史进度反馈
     * @param taskId 任务ID
     * @return 进度反馈列表
     */
    List<ProgressFeedback> listHistoryFeedback(Long taskId);
}

// FileService
public class FileService {
    /**
     * 加密上传成果材料（按任务划分独立目录）
     * @param file 文件
     * @param taskId 任务ID
     * @param feedbackId 进度反馈ID
     * @return 文件元数据
     */
    TaskFile uploadEncrypted(MultipartFile file, Long taskId, Long feedbackId);
}
```

#### 4.2.3 预警督办核心函数

```java
// WarnJob
public class WarnJob {
    /**
     * 定时扫描任务剩余工期，触发三级预警
     * 每日固定时间执行
     */
    void scanAndWarn();

    /**
     * 逾期任务每日高频推送紧急督办
     */
    void pushUrgentWarnForOverdue();
}

// WarnService
public class WarnService {
    /**
     * 匹配预警规则并推送消息
     * @param task 任务
     * @param level 预警级别
     */
    void matchAndPushWarn(Task task, WarnLevelEnum level);
}

// MessagePushService
public class MessagePushService {
    /**
     * 推送 APP 站内消息
     * @param userIds 目标用户ID列表
     * @param message 消息内容
     */
    void pushInAppMessage(List<Long> userIds, PushMessage message);
}
```

#### 4.2.4 验收整改核心函数

```java
// AcceptanceService
public class AcceptanceService {
    /**
     * 提交验收申请（附带成果材料）
     * @param dto 验收申请数据传输对象
     */
    void submitAcceptanceApplication(AcceptanceDTO dto);

    /**
     * 验收核验，填写意见
     * @param acceptId 验收ID
     * @param result 验收结果（通过/退回整改）
     * @param opinion 验收意见
     */
    void verifyAcceptance(Long acceptId, AcceptResultEnum result, String opinion);
}

// RectifyService
public class RectifyService {
    /**
     * 退回整改任务自动生成二次待办
     * @param taskId 任务ID
     * @param reason 退回原因
     */
    void generateRectifyTodo(Long taskId, String reason);
}

// FlowEngineService
public class FlowEngineService {
    /**
     * 可视化配置验收审批流程节点
     * @param flowConfig 流程配置
     */
    void configAcceptanceFlow(FlowConfig flowConfig);

    /**
     * 执行验收流程节点流转
     * @param taskId 任务ID
     * @param action 流转动作
     */
    void executeFlow(Long taskId, FlowAction action);
}
```

#### 4.2.5 日志统计核心函数

```java
// OperationLogService
public class OperationLogService {
    /**
     * 采集操作日志（加密存储不可篡改）
     * @param operation 操作行为
     */
    void collectLog(OperationLog operation);

    /**
     * 按部门/人员/时间区间检索审计日志
     * @param query 检索条件
     * @return 日志列表
     */
    List<OperationLog> auditQuery(LogQueryDTO query);
}

// StatisticsService
public class StatisticsService {
    /**
     * 周期性汇总全企业任务数据
     * @param period 统计周期（月/季度）
     */
    void summarizeTaskData(StatisticsPeriod period);

    /**
     * 生成可视化统计报表（派发总量、按期完成率、逾期数量、平均办结时长）
     * @param period 统计周期
     * @param deptId 部门ID（可选）
     * @return 统计报表
     */
    StatisticsReport generateReport(StatisticsPeriod period, Long deptId);

    /**
     * 导出报表文件
     * @param reportId 报表ID
     * @return 文件下载路径
     */
    String exportReport(Long reportId);
}
```

### 4.3 前端核心组件

> 目录约定：`src/pages/`、`src/components/`、`src/store/`

| 组件 / 页面                        | 类型         | 职责                    |
| ------------------------------ | ---------- | --------------------- |
| `pages/index/index`            | 页面         | 首页六大状态任务卡片聚合展示        |
| `pages/task/list`              | 页面         | 任务列表（分页、筛选、状态切换）      |
| `pages/task/detail`            | 页面         | 单任务详情全流程还原            |
| `pages/task/create`            | 页面         | 任务单条创建（选择模板、填写字段）     |
| `pages/task/batch-import`      | 页面         | 任务 Excel 批量导入         |
| `pages/task/batch-assign`      | 页面         | 批量分派任务                |
| `pages/feedback/submit`        | 页面         | 分阶段进度反馈提交             |
| `pages/feedback/history`       | 页面         | 历史进度反馈查询              |
| `pages/file/upload`            | 页面         | 成果材料上传（文档/图片/短视频）     |
| `pages/acceptance/apply`       | 页面         | 验收申请提交                |
| `pages/acceptance/verify`      | 页面         | 验收核验与意见填写             |
| `pages/rectify/todo`           | 页面         | 退回整改二次待办              |
| `pages/overdue/list`           | 页面         | 逾期任务清单与批量处置           |
| `pages/overdue/accountability` | 页面         | 逾期原因登记与追责记录           |
| `pages/statistics/report`      | 页面         | 可视化统计报表与导出            |
| `pages/log/audit`              | 页面         | 操作日志检索审计              |
| `pages/org/dept`               | 页面         | 部门树形管理                |
| `pages/org/user`               | 页面         | 员工信息管理                |
| `pages/org/role`               | 页面         | 角色权限分配                |
| `components/task-card`         | 组件         | 任务卡片（优先级、剩余工期、逾期时长标识） |
| `components/priority-tag`      | 组件         | 三级优先级标签               |
| `components/status-badge`      | 组件         | 六大状态徽标                |
| `components/file-preview`      | 组件         | 文件预览（文档/图片/视频）        |
| `components/offline-cache`     | 组件         | 离线缓存管理                |
| `store/modules/task`           | Vuex/Pinia | 任务状态管理                |
| `store/modules/user`           | Vuex/Pinia | 用户与权限状态               |
| `store/modules/offline`        | Vuex/Pinia | 离线缓存队列状态              |
| `api/task`                     | API 模块     | 任务相关接口封装              |
| `api/feedback`                 | API 模块     | 进度反馈相关接口封装            |
| `api/warn`                     | API 模块     | 预警督办相关接口封装            |
| `utils/offline-sync`           | 工具         | 离线编辑内容联网自动同步          |
| `utils/websocket`              | 工具         | WebSocket 站内消息接收      |

***

## 5. 数据库设计

### 5.1 核心数据表清单

| 序号 | 表名                       | 说明        | 所属模块 |
| -- | ------------------------ | --------- | ---- |
| 1  | `sys_dept`               | 部门表       | 组织权限 |
| 2  | `sys_user`               | 员工表       | 组织权限 |
| 3  | `sys_role`               | 角色表       | 组织权限 |
| 4  | `sys_user_role`          | 用户角色关联表   | 组织权限 |
| 5  | `task_group`             | 任务分组表     | 组织权限 |
| 6  | `task_template`          | 任务模板表     | 模板分派 |
| 7  | `task_template_field`    | 模板字段表     | 模板分派 |
| 8  | `task`                   | 任务主表      | 模板分派 |
| 9  | `task_assignee`          | 任务分派对象表   | 模板分派 |
| 10 | `task_progress_node`     | 任务进度填报节点表 | 模板分派 |
| 11 | `progress_feedback`      | 进度反馈记录表   | 进度反馈 |
| 12 | `task_file`              | 任务成果文件表   | 进度反馈 |
| 13 | `warn_rule`              | 预警规则表     | 预警督办 |
| 14 | `warn_record`            | 预警推送记录表   | 预警督办 |
| 15 | `in_app_message`         | APP 站内消息表 | 预警督办 |
| 16 | `acceptance`             | 验收记录表     | 验收追责 |
| 17 | `rectify_task`           | 退回整改任务表   | 验收追责 |
| 18 | `overdue_accountability` | 逾期追责台账表   | 验收追责 |
| 19 | `flow_config`            | 验收流程配置表   | 验收追责 |
| 20 | `operation_log`          | 操作日志表     | 日志统计 |
| 21 | `statistics_report`      | 统计报表表     | 日志统计 |

### 5.2 关键表结构

#### 5.2.1 sys\_dept（部门表）

| 字段           | 类型           | 说明          |
| ------------ | ------------ | ----------- |
| dept\_id     | bigint       | 部门ID（主键）    |
| parent\_id   | bigint       | 父部门ID       |
| dept\_name   | varchar(100) | 部门名称        |
| sort         | int          | 显示排序        |
| status       | tinyint      | 状态（0正常 1停用） |
| create\_time | datetime     | 创建时间        |
| update\_time | datetime     | 更新时间        |

#### 5.2.2 sys\_user（员工表）

| 字段           | 类型          | 说明                                     |
| ------------ | ----------- | -------------------------------------- |
| user\_id     | bigint      | 员工ID（主键）                               |
| dept\_id     | bigint      | 部门ID                                   |
| user\_name   | varchar(50) | 员工姓名                                   |
| phone        | varchar(20) | 手机号                                    |
| role\_code   | varchar(20) | 角色编码（EXECUTOR/DEPT\_LEADER/SUPERVISOR） |
| status       | tinyint     | 状态（0在职 1调岗 2离职归档）                      |
| create\_time | datetime    | 创建时间                                   |

#### 5.2.3 task（任务主表）

| 字段             | 类型           | 说明                                  |
| -------------- | ------------ | ----------------------------------- |
| task\_id       | bigint       | 任务ID（主键）                            |
| template\_id   | bigint       | 模板ID                                |
| group\_id      | bigint       | 任务分组ID                              |
| title          | varchar(200) | 任务标题                                |
| content        | text         | 任务内容                                |
| priority       | tinyint      | 优先级（1普通 2重要 3紧急）                    |
| deadline       | datetime     | 截止时间                                |
| creator\_id    | bigint       | 创建人ID                               |
| assignee\_mode | tinyint      | 分派模式（1单人 2多人协同）                     |
| status         | tinyint      | 任务状态（1待接收 2进行中 3待反馈 4待验收 5已完成 6已逾期） |
| create\_time   | datetime     | 创建时间                                |

#### 5.2.4 task\_assignee（任务分派对象表）

| 字段             | 类型       | 说明          |
| -------------- | -------- | ----------- |
| id             | bigint   | 主键          |
| task\_id       | bigint   | 任务ID        |
| user\_id       | bigint   | 执行人ID       |
| assignee\_type | tinyint  | 类型（1主办 2协办） |
| receive\_time  | datetime | 接收时间        |

#### 5.2.5 progress\_feedback（进度反馈记录表）

| 字段                 | 类型       | 说明       |
| ------------------ | -------- | -------- |
| feedback\_id       | bigint   | 反馈ID（主键） |
| task\_id           | bigint   | 任务ID     |
| user\_id           | bigint   | 反馈人ID    |
| completed\_content | text     | 当期工作完成详情 |
| next\_plan         | text     | 后续工作计划   |
| stage              | int      | 阶段序号     |
| feedback\_time     | datetime | 反馈时间     |

#### 5.2.6 task\_file（任务成果文件表）

| 字段            | 类型           | 说明                  |
| ------------- | ------------ | ------------------- |
| file\_id      | bigint       | 文件ID（主键）            |
| task\_id      | bigint       | 任务ID                |
| feedback\_id  | bigint       | 进度反馈ID（可空）          |
| file\_name    | varchar(200) | 文件名                 |
| file\_path    | varchar(500) | 存储路径                |
| file\_type    | varchar(20)  | 文件类型（DOC/IMG/VIDEO） |
| file\_size    | bigint       | 文件大小（字节）            |
| encrypt\_hash | varchar(100) | 加密哈希（防篡改）           |
| upload\_time  | datetime     | 上传时间                |

#### 5.2.7 warn\_rule（预警规则表）

| 字段              | 类型           | 说明                |
| --------------- | ------------ | ----------------- |
| rule\_id        | bigint       | 规则ID（主键）          |
| level           | tinyint      | 预警级别（1普通 2重要 3紧急） |
| before\_days    | int          | 到期前天数（7/3/0表示逾期）  |
| push\_frequency | varchar(20)  | 推送频率（ONCE/DAILY）  |
| target\_roles   | varchar(100) | 目标角色（逗号分隔）        |
| enabled         | tinyint      | 是否启用              |

#### 5.2.8 acceptance（验收记录表）

| 字段            | 类型       | 说明              |
| ------------- | -------- | --------------- |
| accept\_id    | bigint   | 验收ID（主键）        |
| task\_id      | bigint   | 任务ID            |
| applicant\_id | bigint   | 验收申请人ID         |
| acceptor\_id  | bigint   | 验收人ID           |
| result        | tinyint  | 验收结果（1通过 2退回整改） |
| opinion       | text     | 验收意见            |
| apply\_time   | datetime | 申请时间            |
| accept\_time  | datetime | 验收时间            |

#### 5.2.9 overdue\_accountability（逾期追责台账表）

| 字段                 | 类型       | 说明         |
| ------------------ | -------- | ---------- |
| accountability\_id | bigint   | 追责ID（主键）   |
| task\_id           | bigint   | 任务ID       |
| overdue\_days      | int      | 逾期时长（天）    |
| reason             | text     | 逾期客观原因     |
| disposition        | text     | 人员处置追责记录   |
| archive\_time      | datetime | 归档时间（永久留存） |

#### 5.2.10 operation\_log（操作日志表）

| 字段                 | 类型           | 说明                                                    |
| ------------------ | ------------ | ----------------------------------------------------- |
| log\_id            | bigint       | 日志ID（主键）                                              |
| module             | varchar(50)  | 模块（CREATE/ASSIGN/FEEDBACK/UPLOAD/WARN/ACCEPT/OVERDUE） |
| operation          | varchar(100) | 操作行为                                                  |
| operator\_id       | bigint       | 操作人ID                                                 |
| operator\_name     | varchar(50)  | 操作人姓名                                                 |
| operate\_time      | datetime     | 操作时间                                                  |
| encrypted\_content | text         | 加密操作内容（不可篡改）                                          |

#### 5.2.11 statistics\_report（统计报表表）

| 字段                  | 类型           | 说明                      |
| ------------------- | ------------ | ----------------------- |
| report\_id          | bigint       | 报表ID（主键）                |
| period              | varchar(20)  | 统计周期（MONTHLY/QUARTERLY） |
| period\_value       | varchar(20)  | 周期值（如 2026-08）          |
| dept\_id            | bigint       | 部门ID（可空，空表示全公司）         |
| user\_id            | bigint       | 人员ID（可空）                |
| total\_dispatch     | int          | 任务派发总量                  |
| on\_time\_rate      | decimal(5,2) | 按期完成率（%）                |
| overdue\_count      | int          | 逾期任务数量                  |
| avg\_complete\_days | decimal(8,2) | 任务平均办结时长（天）             |
| generate\_time      | datetime     | 生成时间                    |

### 5.3 表关系说明

```
sys_dept  1───N  sys_user
sys_user  N───N  sys_role  (通过 sys_user_role)

task_group  N───1  sys_dept
task  N───1  task_template
task  N───1  task_group
task  1───N  task_assignee
task  1───N  task_progress_node
task  1───N  progress_feedback
progress_feedback  1───N  task_file
task  1───1  acceptance
task  1───N  rectify_task
task  1───1  overdue_accountability
task  1───N  warn_record
warn_record  N───1  in_app_message
flow_config  1───N  task (验收流程配置)
```

***

## 6. 接口设计

### 6.1 RESTful 接口规范

- 统一前缀：`/api/v1`
- 统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- 鉴权方式：JWT Token（Header: `Authorization: Bearer {token}`）
- 状态码：200 成功 / 400 参数错误 / 401 未授权 / 403 无权限 / 500 服务异常

### 6.2 核心接口清单

#### 6.2.1 组织权限模块

| 方法     | 路径                               | 说明     |
| ------ | -------------------------------- | ------ |
| GET    | `/api/v1/dept/tree`              | 获取部门树  |
| POST   | `/api/v1/dept`                   | 新增部门   |
| PUT    | `/api/v1/dept/{deptId}`          | 修改部门   |
| DELETE | `/api/v1/dept/{deptId}`          | 删除部门   |
| GET    | `/api/v1/user/page`              | 员工分页查询 |
| POST   | `/api/v1/user`                   | 新增员工   |
| PUT    | `/api/v1/user/{userId}/transfer` | 员工调岗   |
| PUT    | `/api/v1/user/{userId}/archive`  | 员工离职归档 |
| GET    | `/api/v1/role/list`              | 角色列表   |
| POST   | `/api/v1/role/assign`            | 分配角色权限 |
| GET    | `/api/v1/task-group/list`        | 任务分组列表 |
| POST   | `/api/v1/task-group`             | 新增任务分组 |

#### 6.2.2 任务模板与分派模块

| 方法   | 路径                                   | 说明           |
| ---- | ------------------------------------ | ------------ |
| GET  | `/api/v1/task-template/list`         | 模板列表         |
| POST | `/api/v1/task-template`              | 新增模板         |
| PUT  | `/api/v1/task-template/{templateId}` | 修改模板         |
| POST | `/api/v1/task`                       | 创建任务（单条）     |
| GET  | `/api/v1/task/page`                  | 任务分页查询       |
| GET  | `/api/v1/task/{taskId}`              | 任务详情         |
| POST | `/api/v1/task/batch-import`          | Excel 批量导入任务 |
| POST | `/api/v1/task/batch-assign`          | 批量分派任务       |

#### 6.2.3 进度反馈与成果上传模块

| 方法   | 路径                               | 说明         |
| ---- | -------------------------------- | ---------- |
| POST | `/api/v1/feedback`               | 提交分阶段进度反馈  |
| GET  | `/api/v1/feedback/list`          | 查询任务历史进度反馈 |
| POST | `/api/v1/file/upload`            | 上传成果材料（加密） |
| GET  | `/api/v1/file/download/{fileId}` | 下载成果材料     |
| GET  | `/api/v1/file/preview/{fileId}`  | 预览成果材料     |

#### 6.2.4 状态跟踪模块

| 方法  | 路径                                | 说明         |
| --- | --------------------------------- | ---------- |
| GET | `/api/v1/dashboard/cards`         | 首页六大状态任务卡片 |
| GET | `/api/v1/dashboard/task/{status}` | 按状态查询任务列表  |
| GET | `/api/v1/task/{taskId}/detail`    | 任务详情全流程还原  |

#### 6.2.5 预警督办模块

| 方法   | 路径                                 | 说明                 |
| ---- | ---------------------------------- | ------------------ |
| GET  | `/api/v1/warn-rule/list`           | 预警规则列表             |
| POST | `/api/v1/warn-rule`                | 配置预警规则             |
| GET  | `/api/v1/warn-record/page`         | 预警推送记录分页           |
| GET  | `/api/v1/message/page`             | APP 站内消息分页         |
| PUT  | `/api/v1/message/{messageId}/read` | 标记消息已读             |
| WS   | `/ws/message`                      | WebSocket 站内消息推送连接 |

#### 6.2.6 验收整改与追责模块

| 方法   | 路径                                        | 说明            |
| ---- | ----------------------------------------- | ------------- |
| POST | `/api/v1/acceptance/apply`                | 提交验收申请        |
| POST | `/api/v1/acceptance/{acceptId}/verify`    | 验收核验（通过/退回整改） |
| GET  | `/api/v1/rectify/page`                    | 退回整改二次待办分页    |
| GET  | `/api/v1/overdue/list`                    | 逾期任务清单        |
| POST | `/api/v1/overdue/{taskId}/reason`         | 登记逾期原因        |
| POST | `/api/v1/overdue/{taskId}/accountability` | 登记追责处置        |
| GET  | `/api/v1/flow-config/list`                | 验收流程配置列表      |
| POST | `/api/v1/flow-config`                     | 配置验收流程节点      |

#### 6.2.7 日志统计模块

| 方法  | 路径                                 | 说明         |
| --- | ---------------------------------- | ---------- |
| GET | `/api/v1/log/page`                 | 操作日志检索审计分页 |
| GET | `/api/v1/statistics/report`        | 统计报表查询     |
| GET | `/api/v1/statistics/report/export` | 导出报表文件     |

***

## 7. 依赖关系

### 7.1 后端依赖

`pom.xml` 核心依赖：

| 依赖                                                        | 说明                |
| --------------------------------------------------------- | ----------------- |
| `org.springframework.boot:spring-boot-starter-web`        | Web 接口支持          |
| `org.springframework.boot:spring-boot-starter-data-redis` | Redis 缓存          |
| `org.springframework.boot:spring-boot-starter-amqp`       | RabbitMQ 消息队列     |
| `org.springframework.boot:spring-boot-starter-websocket`  | WebSocket 站内消息推送  |
| `com.baomidou:mybatis-plus-spring-boot3-starter`          | MyBatis-Plus 数据访问 |
| `mysql:mysql-connector-j`                                 | MySQL 驱动          |
| `io.minio:minio`                                          | MinIO 对象存储客户端     |
| `com.alibaba:easyexcel`                                   | Excel 批量导入解析      |
| `com.auth0:java-jwt`                                      | JWT 鉴权            |
| `org.projectlombok:lombok`                                | Lombok 简化代码       |
| `cn.hutool:hutool-all`                                    | 通用工具类             |
| `com.alibaba:fastjson2`                                   | JSON 序列化          |
| `io.springfox:springfox-boot-starter`                     | 接口文档              |
| `org.apache.poi:poi-ooxml`                                | 文档处理（备用）          |
| `com.xuxueli:xxl-job-core`                                | 定时任务调度            |

### 7.2 前端依赖

`package.json` 核心依赖：

| 依赖                             | 说明                 |
| ------------------------------ | ------------------ |
| `@dcloudio/uni-app`            | UniApp 跨端框架        |
| `vue@3`                        | Vue3 框架            |
| `uview-plus`                   | UniApp Vue3 UI 组件库 |
| `pinia`                        | 状态管理               |
| `uni-request` / `luch-request` | HTTP 请求封装          |
| `dayjs`                        | 日期处理（剩余工期、逾期时长）    |
| `crypto-js`                    | 离线缓存内容加密           |
| `echarts`                      | 可视化统计报表图表          |
| `uni-file-picker`              | 文件选择上传             |
| `uni-upgrade-lite`             | APP 版本升级           |

### 7.3 中间件依赖

| 中间件      | 版本    | 用途                    |
| -------- | ----- | --------------------- |
| MySQL    | 8.x   | 核心业务数据存储              |
| Redis    | 7.x   | 高频访问数据缓存              |
| MinIO    | 最新稳定版 | 成果材料对象存储（可替换为阿里云 OSS） |
| RabbitMQ | 3.x   | 预警督办消息队列              |
| Nginx    | 1.2x  | 反向代理与静态资源             |
| XxlJob   | 2.x   | 定时任务调度（预警扫描、周期统计）     |

### 7.4 模块间依赖

```
组织权限模块 ──基础支撑──> 其他所有业务模块
任务模板分派模块 ──依赖──> 组织权限模块
进度反馈上传模块 ──依赖──> 任务模板分派模块、组织权限模块
状态跟踪模块 ──依赖──> 任务模板分派模块、进度反馈模块、验收追责模块
预警督办模块 ──依赖──> 任务模板分派模块、组织权限模块
验收整改追责模块 ──依赖──> 任务模板分派模块、进度反馈模块、流程引擎
日志统计模块 ──横切──> 所有业务模块（采集操作日志）
```

***

## 8. 项目运行方式

### 8.1 环境准备

#### 8.1.1 基础环境

| 软件        | 版本要求          |
| --------- | ------------- |
| JDK       | 21            |
| Maven     | 3.8+          |
| Node.js   | 18+           |
| HBuilderX | 最新版（前端打包）     |
| MySQL     | 8.x           |
| Redis     | 7.x           |
| MinIO     | 最新稳定版         |
| RabbitMQ  | 3.x           |
| Docker    | 20+（可选，容器化部署） |

#### 8.1.2 中间件初始化

1. **MySQL**：创建数据库 `task_db`，执行 `sql/init.sql` 初始化表结构与基础数据。
2. **Redis**：启动 Redis 服务，默认端口 6379。
3. **MinIO**：启动 MinIO 服务，创建 bucket `task-file`，配置访问密钥。
4. **RabbitMQ**：启动服务，创建虚拟主机 `/task`，配置预警消息队列 `task.warn.queue`。
5. **XxlJob**：部署 XxlJob-Admin 调度中心，注册预警扫描与周期统计任务。

### 8.2 本地开发启动

#### 8.2.1 后端启动

1. 修改 `src/main/resources/application-dev.yml`，配置 MySQL、Redis、MinIO、RabbitMQ 连接信息。
2. 执行 Maven 编译：

```bash
mvn clean compile
```

1. 启动 Spring Boot 应用：

```bash
mvn spring-boot:run
```

1. 访问接口文档：`http://localhost:8080/doc.html`

#### 8.2.2 前端启动

1. 安装依赖：

```bash
npm install
```

1. HBuilderX 打开前端项目目录。
2. 修改 `src/config/env.js`，配置后端接口地址：

```js
export const BASE_URL = 'http://localhost:8080/api/v1'
```

1. HBuilderX 选择「运行」→「运行到手机或模拟器」→ 选择安卓/iOS 设备进行真机调试。
2. 或选择「运行到浏览器」进行 H5 调试。

### 8.3 打包发布

#### 8.3.1 后端打包

```bash
mvn clean package -DskipTests
```

生成 `target/task-server.jar`。

#### 8.3.2 前端打包

1. HBuilderX 选择「发行」→「原生 App-云打包」。
2. 选择安卓/iOS 平台，配置证书与签名。
3. 提交云端打包，生成 `apk` / `ipa` 安装包。

### 8.4 部署上线

#### 8.4.1 Docker 容器化部署

后端 `Dockerfile` 示例：

```dockerfile
FROM openjdk:21-jdk-slim
COPY target/task-server.jar /app/task-server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/task-server.jar", "--spring.profiles.active=prod"]
```

#### 8.4.2 docker-compose 编排

```yaml
version: '3.8'
services:
  task-server:
    build: task-supervise-server
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
      - minio
      - rabbitmq
    environment:
      - SPRING_PROFILES_ACTIVE=prod

  mysql:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: xxxxxx
      MYSQL_DATABASE: task_db
    volumes:
      - mysql-data:/var/lib/mysql

  redis:
    image: redis:7
    ports:
      - "6379:6379"

  minio:
    image: minio/minio
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: xxxxxx
      MINIO_ROOT_PASSWORD: xxxxxx
    command: server /data --console-address ":9001"

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"

  nginx:
    image: nginx:1.25
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/conf.d:/etc/nginx/conf.d
    depends_on:
      - task-server

volumes:
  mysql-data:
```

#### 8.4.3 Nginx 反向代理配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 后端接口代理
    location /api/ {
        proxy_pass http://task-server:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # WebSocket 代理（站内消息推送）
    location /ws/ {
        proxy_pass http://task-server:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

#### 8.4.4 启动顺序

1. 启动中间件：MySQL → Redis → MinIO → RabbitMQ → XxlJob
2. 启动后端 Spring Boot 应用
3. 启动 Nginx 反向代理
4. 移动端 APP 安装并配置服务端地址

***

## 9. 目录结构

```
task-supervision-app/
├── backend/                              # 后端 Spring Boot 工程
│   ├── pom.xml
│   ├── src/main/java/com/enterprise/task督办/
│   │   ├── TaskSupervisionApplication.java   # 启动类
│   │   ├── config/                           # 配置类
│   │   │   ├── RedisConfig.java
│   │   │   ├── MinioConfig.java
│   │   │   ├── RabbitMqConfig.java
│   │   │   ├── WebSocketConfig.java
│   │   │   └── WebMvcConfig.java
│   │   ├── common/                           # 公共组件
│   │   │   ├── result/Result.java            # 统一返回
│   │   │   ├── exception/                    # 全局异常
│   │   │   ├── constant/                     # 常量与枚举
│   │   │   └── utils/                        # 工具类
│   │   ├── module/                           # 业务模块
│   │   │   ├── org/                          # 组织权限模块
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── mapper/
│   │   │   │   └── entity/
│   │   │   ├── task/                         # 任务模板分派模块
│   │   │   ├── feedback/                     # 进度反馈上传模块
│   │   │   ├── dashboard/                    # 状态跟踪模块
│   │   │   ├── warn/                         # 预警督办模块
│   │   │   ├── acceptance/                   # 验收整改追责模块
│   │   │   └── statistics/                  # 日志统计模块
│   │   ├── flow/                             # 轻量化流程引擎
│   │   └── job/                              # 定时任务
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-prod.yml
│       └── mapper/                           # MyBatis XML
│
├── frontend/                             # 前端 UniApp 工程
│   ├── package.json
│   ├── src/
│   │   ├── pages/                       # 页面
│   │   │   ├── index/
│   │   │   ├── task/
│   │   │   ├── feedback/
│   │   │   ├── file/
│   │   │   ├── acceptance/
│   │   │   ├── rectify/
│   │   │   ├── overdue/
│   │   │   ├── statistics/
│   │   │   ├── log/
│   │   │   └── org/
│   │   ├── components/                  # 公共组件
│   │   ├── store/                       # 状态管理
│   │   ├── api/                         # 接口封装
│   │   ├── utils/                       # 工具类
│   │   ├── config/                     # 环境配置
│   │   └── static/                      # 静态资源
│   └── manifest.json                    # UniApp 配置
│
├── sql/                                 # 数据库脚本
│   └── init.sql
│
├── docker/                              # Docker 相关
│   ├── Dockerfile
│   └── docker-compose.yml
│
├── nginx/
│   └── conf.d/
│       └── default.conf
│
└── README.md
```

***

## 10. 开发规范

### 10.1 代码规范

- **后端**：遵循阿里巴巴 Java 开发手册，使用 Lombok 简化 POJO。
- **前端**：遵循 Vue3 `<script setup>` 语法，组件命名采用 kebab-case。
- **命名**：类名 PascalCase，方法名/变量名 camelCase，数据库字段 snake\_case。
- **注释**：类、公共方法必须有 JavaDoc 注释，复杂业务逻辑需补充说明。

### 10.2 接口规范

- 统一返回 `Result<T>` 包装，包含 code、message、data。
- RESTful 风格，资源名词复数，动作用 HTTP 方法区分。
- 所有接口需 JWT 鉴权（登录接口除外）。
- 接口需通过 Swagger 生成文档。

### 10.3 权限规范

- 三级角色：EXECUTOR（执行人员）、DEPT\_LEADER（部门主管）、SUPERVISOR（督办管理员）。
- 接口通过注解 `@RequiresRole` 控制访问权限。
- 数据权限：执行人员仅可见自身任务，主管可见本部门，督办可见全公司。

### 10.4 日志规范

- 关键业务操作（创建、分派、反馈、验收、追责）必须记录操作日志。
- 操作日志加密存储，不可删除篡改。
- 日志需包含模块、操作、操作人、操作时间、加密内容。

### 10.5 文件存储规范

- 成果材料按任务划分独立目录：`/task-file/{taskId}/{feedbackId}/{fileName}`。
- 文件加密上传存储，记录 encryptHash 防篡改。
- 文件元数据永久绑定任务，不可删除。

### 10.6 离线缓存规范

- 弱网环境缓存任务基础信息与未提交进度反馈内容。
- 离线编辑内容本地加密存储。
- 联网后自动同步至云端，冲突以云端为准并提示用户。

***

> 本 Code Wiki 文档基于项目技术规格说明编制，随项目迭代持续更新。如需补充具体实现细节，请结合实际代码仓库进一步完善。

