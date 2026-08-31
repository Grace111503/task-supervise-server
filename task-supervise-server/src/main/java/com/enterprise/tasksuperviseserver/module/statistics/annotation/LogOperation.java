package com.enterprise.tasksuperviseserver.module.statistics.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * <p>
 * 标注在 Controller 方法上，由 AOP 切面自动采集操作日志。
 *
 * @author grq
 * @date 2026-08-31
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {

    /**
     * 所属模块: task/feedback/acceptance/warn/file/org
     */
    String module();

    /**
     * 操作类型: CREATE/UPDATE/DELETE/VERIFY/UPLOAD/REJECT/LOGIN/EXPORT
     */
    String action();

    /**
     * 操作描述（支持 SpEL 表达式，如 "#taskId"）
     */
    String detail() default "";

    /**
     * 从请求参数中提取 taskId 的参数名（支持路径变量和请求参数）
     */
    String taskIdParam() default "taskId";
}
