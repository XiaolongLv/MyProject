package cn.jeeson.backend.service.impl;

import cn.jeeson.backend.entity.auth.Account;
import cn.jeeson.backend.mapper.UserMapper;
import cn.jeeson.backend.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Description None
 * @Author fjz
 * @Date 2023-4-23
 * @Version 1.0
 */
@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Value("${spring.mail.username}")
    String from;

    @Resource
    UserMapper userMapper;

    @Resource
    MailSender mailSender;

    @Resource
    StringRedisTemplate template;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 登录验证
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null)
            throw new UsernameNotFoundException("用户名不能为空");
        Account account = userMapper.findAccountByNameOrEmail(username);
        if(account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }

    /**
     * 发送验证邮件
     * 1. 生成邮箱对应的验证码
     * 2. 把邮箱和验证码作为键值对放到Redis里（5分钟内有效，如果重新要求发邮件，剩余时间低于4分钟，可重发）
     * 3. 发送验证码到邮箱
     * 4. 如果发送失败，把Redis里的删掉
     * 5. 用户注册时，从Redis里取出对应键值对，确认验证码一致
     */
    @Override
    public String sendValidateEmail(String email, String sessionId, boolean hasAccount, String used) {
        String key = "email:" + sessionId + ":" + email + ":" + hasAccount;
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            Long expire = Optional.ofNullable(template.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if(expire > 240)
                return "请求频繁，请稍后再试";
        }

        Account account = userMapper.findAccountByNameOrEmail(email);
        if(hasAccount && account == null)
            return "此邮箱未注册";
        if(!hasAccount && account != null)
            return "此邮箱已被注册";

        Random random = new Random();
        int code = random.nextInt(9000) + 1000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("来自 JeesonTools 的验证邮件");

        String text = "验证码：" + code + "，有效期5分钟。如非您本人操作，请忽略此邮件。";
        if (used.equals("register"))
            message.setText("您正在注册JeesonTools帐号，" + text);
        else if (used.equals("reset-password"))
            message.setText("您正在重置密码，" + text);
        else
            message.setText(text);

        try {
            mailSender.send(message);
            template.opsForValue().set(key, String.valueOf(code), 5, TimeUnit.MINUTES);
            return null;
        } catch (MailException e) {
            e.printStackTrace();
            return "邮件发送失败，请检查邮件地址是否有效";
        }
    }

    /**
     * 验证并注册
     */
    @Override
    public String validateAndRegister(String username, String password, String email, String code, String sessionId) {
        String key = "email:" + sessionId + ":" + email + ":false";
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            String s = template.opsForValue().get(key);
            if (s == null)
                return "验证码失效，请重新获取验证码";
            if (s.equals(code)) {

                if (userMapper.findAccountByNameOrEmail(username) != null)
                    return "用户名已存在";

                password = encoder.encode(password);
                template.delete(key);
                if (userMapper.createAccount(username, password, email) > 0)
                    return null;
                else
                    return "系统错误，请联系管理员";

            } else {
                return "验证码错误，请检查后再提交";
            }
        } else {
            return "请先获取验证码";
        }
    }

    /**
     * 验证邮件验证码正确
     */
    @Override
    public String validateOnly(String email, String code, String sessionId) {
        String key = "email:" + sessionId + ":" + email + ":true";
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            String s = template.opsForValue().get(key);
            if (s == null)
                return "验证码失效，请重新获取验证码";
            if (s.equals(code)) {
                template.delete(key);
                return null;
            } else {
                return "验证码错误，请检查后再提交";
            }
        } else {
            return "请先获取验证码";
        }
    }

    /**
     * 重置密码
     */
    @Override
    public boolean resetPassword(String email, String password) {
        password = encoder.encode(password);
        return userMapper.resetPasswordByEmail(email, password) > 0;
    }
}
