package com.enterprise.tasksuperviseserver.config;

import com.enterprise.tasksuperviseserver.common.UserContext;
import com.enterprise.tasksuperviseserver.module.org.entity.SysUser;
import com.enterprise.tasksuperviseserver.module.org.mapper.SysUserMapper;
import com.enterprise.tasksuperviseserver.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器：从请求头读取 Authorization Bearer token，解析后放入 UserContext
 *
 * @author grq
 * @date 2026-08-26
 * @version v2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final SysUserMapper sysUserMapper;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(authHeader)) {
            authHeader = request.getParameter("token");
        }

        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());
            if (jwtUtil.validateToken(token, "access")) {
                Claims claims = jwtUtil.parseToken(token);
                if (claims != null) {
                    Long userId = ((Number) claims.get(JwtUtil.CLAIM_USER_ID)).longValue();
                    String username = (String) claims.get(JwtUtil.CLAIM_USERNAME);
                    String role = (String) claims.get(JwtUtil.CLAIM_ROLE);
                    UserContext.setUserId(userId);
                    UserContext.setUsername(username);
                    UserContext.setRole(role);
                    // 加载用户部门信息
                    try {
                        SysUser user = sysUserMapper.selectById(userId);
                        if (user != null && user.getDeptId() != null) {
                            UserContext.setDeptId(user.getDeptId());
                        }
                    } catch (Exception ignored) {
                    }
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
