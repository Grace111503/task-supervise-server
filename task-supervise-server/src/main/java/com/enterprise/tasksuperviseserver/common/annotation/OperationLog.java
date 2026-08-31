package com.enterprise.tasksuperviseserver.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * <p>
 * 标注在 Controller 方法上，AOP 切面自动拦截并记录操作日志。
 * 支持通过 SpEL 表达式动态提取 taskId 等参数。
 *
 * @author grq
 * @date 2026-08-28
 * @version v1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 模块名: task / feedback / acceptance / warn / file
     */
    String module() default "";

    /**
     * 操作类型: CREATE / UPDATE / DELETE / VERIFY / UPLOAD / REJECT / ASSIGN
     */
    String action() default "";

    /**
     * 操作描述模板，支持 {0} {1} 占位符引用方法参数
     */
    String detail() default "";

    /**
     * taskId 参数在方法参数列表中的索引位置（从0开始），用于自动提取关联任务ID。
     * 默认 -1 表示不提取。
     */
    int taskIdParamIndex() default -1;
}
