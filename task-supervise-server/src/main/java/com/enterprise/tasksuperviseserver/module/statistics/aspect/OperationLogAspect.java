package com.enterprise.tasksuperviseserver.module.statistics.aspect;

import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.module.statistics.annotation.LogOperation;
import com.enterprise.tasksuperviseserver.module.statistics.entity.OperationLog;
import com.enterprise.tasksuperviseserver.module.statistics.mapper.OperationLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;

/**
 * 操作日志 AOP 切面
 * <p>
 * 拦截所有标注了 {@link LogOperation} 的 Controller 方法，
 * 自动记录操作日志并计算 SHA-256 哈希值实现防篡改。
 *
 * @author grq
 * @date 2026-08-31
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;

    @Around("@annotation(logOperation)")
    public Object around(ProceedingJoinPoint joinPoint, LogOperation logOperation) throws Throwable {
        Object result = joinPoint.proceed();

        // 异步记录日志，不影响主流程
        try {
            recordLog(joinPoint, logOperation);
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }

        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, LogOperation logOperation) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        // 提取 taskId
        Long taskId = extractTaskId(parameters, args, logOperation.taskIdParam());

        // 构建操作详情
        String detail = buildDetail(logOperation, method.getName(), args);

        // 计算 SHA-256 哈希
        String hash = sha256(detail);

        // 获取客户端 IP
        String ip = getClientIp();

        // 获取部门ID
        Long deptId = UserContext.getDeptId();

        OperationLog operationLog = new OperationLog();
        operationLog.setModule(logOperation.module());
        operationLog.setAction(logOperation.action());
        operationLog.setTaskId(taskId);
        operationLog.setOperatorId(UserContext.getUserId());
        operationLog.setOperatorName(UserContext.getUsername());
        operationLog.setDeptId(deptId);
        operationLog.setDetail(detail);
        operationLog.setEncryptedContent(hash);
        operationLog.setIp(ip);
        operationLog.setOperateTime(LocalDateTime.now());

        operationLogMapper.insert(operationLog);
    }

    /**
     * 从方法参数中提取 taskId
     */
    private Long extractTaskId(Parameter[] parameters, Object[] args, String paramName) {
        for (int i = 0; i < parameters.length; i++) {
            // 检查 @PathVariable
            PathVariable pathVar = parameters[i].getAnnotation(PathVariable.class);
            if (pathVar != null && (paramName.equals(pathVar.value()) || paramName.equals(pathVar.name()))) {
                return toLong(args[i]);
            }
            // 检查 @RequestParam
            RequestParam reqParam = parameters[i].getAnnotation(RequestParam.class);
            if (reqParam != null && (paramName.equals(reqParam.value()) || paramName.equals(reqParam.name()))) {
                return toLong(args[i]);
            }
            // 直接匹配参数名
            if (paramName.equals(parameters[i].getName())) {
                return toLong(args[i]);
            }
        }
        return null;
    }

    private Long toLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return null;
    }

    /**
     * 构建操作详情描述
     */
    private String buildDetail(LogOperation logOperation, String methodName, Object[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("模块:").append(logOperation.module());
        sb.append(", 操作:").append(logOperation.action());
        sb.append(", 方法:").append(methodName);

        String detail = logOperation.detail();
        if (!detail.isEmpty()) {
            sb.append(", 详情:").append(detail);
        }

        // 附加关键参数
        if (args != null && args.length > 0) {
            sb.append(", 参数:[");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) sb.append(", ");
                if (args[i] != null) {
                    String argStr = args[i].toString();
                    // 截断过长的参数
                    sb.append(argStr.length() > 200 ? argStr.substring(0, 200) + "..." : argStr);
                }
            }
            sb.append("]");
        }

        return sb.toString();
    }

    /**
     * 计算 SHA-256 哈希
     */
    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (Exception e) {
            log.warn("SHA-256 计算失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "unknown";
            }
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
            ip = request.getHeader("X-Real-IP");
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
