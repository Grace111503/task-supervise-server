package com.enterprise.tasksuperviseserver.config;

import com.enterprise.tasksuperviseserver.common.UserContext;
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
 * @version v1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

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
            // 尝试从参数获取 (兼容部分场景)
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
                    return true;
                }
            }
        }

        // 未通过鉴权也放行，由 Controller 层根据业务决定是否需要登录态
        // 真正严格控制的接口可以在 Controller 中校验 UserContext.getUserId()
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
