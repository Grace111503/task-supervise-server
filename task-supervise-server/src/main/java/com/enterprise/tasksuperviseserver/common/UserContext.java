package com.enterprise.tasksuperviseserver.common;

/**
 * 当前登录用户上下文 (基于 ThreadLocal)
 *
 * @author grq
 * @date 2026-08-26
 * @version v1.0.0
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE_HOLDER = new ThreadLocal<>();
    private static final ThreadLocal<Long> DEPT_ID_HOLDER = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    public static void setUsername(String username) {
        USERNAME_HOLDER.set(username);
    }

    public static String getUsername() {
        return USERNAME_HOLDER.get();
    }

    public static void setRole(String role) {
        ROLE_HOLDER.set(role);
    }

    public static String getRole() {
        return ROLE_HOLDER.get();
    }

    public static void setDeptId(Long deptId) {
        DEPT_ID_HOLDER.set(deptId);
    }

    public static Long getDeptId() {
        return DEPT_ID_HOLDER.get();
    }

    public static void clear() {
        USER_ID_HOLDER.remove();
        USERNAME_HOLDER.remove();
        ROLE_HOLDER.remove();
        DEPT_ID_HOLDER.remove();
    }
}
