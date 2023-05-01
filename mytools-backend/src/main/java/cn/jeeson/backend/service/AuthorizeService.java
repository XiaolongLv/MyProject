package cn.jeeson.backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @Description None
 * @Author fjz
 * @Date 2023-4-24
 * @Version 1.0
 */
public interface AuthorizeService extends UserDetailsService {

    /**
     * 发送验证邮件
     */
    String sendValidateEmail(String email, String sessionId, boolean hasAccount, String used);

    /**
     * 验证并注册
     */
    String validateAndRegister(String username, String password, String email, String code, String sessionId);

    /**
     * 验证邮件验证码正确
     */
    String validateOnly(String email, String code, String sessionId);

    /**
     * 重置密码
     */
    boolean resetPassword(String email, String password);
}
