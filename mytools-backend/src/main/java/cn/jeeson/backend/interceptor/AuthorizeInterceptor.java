package cn.jeeson.backend.interceptor;

import cn.jeeson.backend.entity.user.AccountUser;
import cn.jeeson.backend.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description 拦截器
 * @Author fjz
 * @Date 2023-4-28
 * @Version 1.0
 */
@Component
public class AuthorizeInterceptor implements HandlerInterceptor {

    @Resource
    UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        AccountUser accountUser = userMapper.findAccountUserByNameOrEmail(username);
        request.getSession().setAttribute("account", accountUser);
        return true;
    }
}
