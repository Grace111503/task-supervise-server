package com.enterprise.tasksuperviseserver.common.constant;

/**
 * 任务常量类
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public class TaskConstant {

    private TaskConstant() {
    }

    public static final String API_PREFIX = "/api/v1";

    public static final int STATUS_PENDING_RECEIVE = 1;
    public static final int STATUS_IN_PROGRESS = 2;
    public static final int STATUS_PENDING_FEEDBACK = 3;
    public static final int STATUS_PENDING_ACCEPT = 4;
    public static final int STATUS_COMPLETED = 5;
    public static final int STATUS_OVERDUE = 6;

    // 状态字符串常量
    public static final String STATUS_STR_PENDING = "pending";
    public static final String STATUS_STR_IN_PROGRESS = "in_progress";
    public static final String STATUS_STR_PENDING_FEEDBACK = "pending_feedback";
    public static final String STATUS_STR_PENDING_ACCEPT = "pending_accept";
    public static final String STATUS_STR_COMPLETED = "completed";
    public static final String STATUS_STR_OVERDUE = "overdue";

    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_IMPORTANT = 2;
    public static final int PRIORITY_URGENT = 3;

    public static final int ASSIGNEE_TYPE_PRIMARY = 1;
    public static final int ASSIGNEE_TYPE_ASSIST = 2;

    public static final int ASSIGNEE_MODE_SINGLE = 1;
    public static final int ASSIGNEE_MODE_MULTI = 2;

    public static final int ACCEPT_RESULT_PENDING = 0;
    public static final int ACCEPT_RESULT_PASS = 1;
    public static final int ACCEPT_RESULT_REJECT = 2;

    public static final int WARN_LEVEL_NORMAL = 1;
    public static final int WARN_LEVEL_IMPORTANT = 2;
    public static final int WARN_LEVEL_URGENT = 3;

    public static final int RECTIFY_STATUS_PENDING = 0;
    public static final int RECTIFY_STATUS_PROCESSING = 1;
    public static final int RECTIFY_STATUS_RESUBMITTED = 2;

    public static final String ROLE_EXECUTOR = "EXECUTOR";
    public static final String ROLE_DEPT_LEADER = "DEPT_LEADER";
    public static final String ROLE_SUPERVISOR = "SUPERVISOR";

    public static final String FILE_TYPE_DOC = "DOC";
    public static final String FILE_TYPE_IMG = "IMG";
    public static final String FILE_TYPE_VIDEO = "VIDEO";

    public static final String MSG_TYPE_WARN = "WARN";
    public static final String MSG_TYPE_TASK = "TASK";
    public static final String MSG_TYPE_ACCEPT = "ACCEPT";

    // ========== 模板类型 ==========
    /** 行政任务 */
    public static final int TEMPLATE_TYPE_ADMIN = 1;
    /** 项目任务 */
    public static final int TEMPLATE_TYPE_PROJECT = 2;
    /** 整改任务 */
    public static final int TEMPLATE_TYPE_RECTIFY = 3;
    /** 会议任务 */
    public static final int TEMPLATE_TYPE_MEETING = 4;
    /** 客户对接任务 */
    public static final int TEMPLATE_TYPE_CLIENT = 5;

    // ========== 模板状态 ==========
    public static final int TEMPLATE_STATUS_DISABLED = 0;
    public static final int TEMPLATE_STATUS_ENABLED = 1;

    // ========== 字段类型 ==========
    public static final String FIELD_TYPE_TEXT = "text";
    public static final String FIELD_TYPE_TEXTAREA = "textarea";
    public static final String FIELD_TYPE_NUMBER = "number";
    public static final String FIELD_TYPE_DATE = "date";
    public static final String FIELD_TYPE_SELECT = "select";
    public static final String FIELD_TYPE_FILE = "file";
    public static final String FIELD_TYPE_CHECKBOX = "checkbox";
    public static final String FIELD_TYPE_RADIO = "radio";
}
