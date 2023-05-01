package cn.jeeson.backend.controller;

import cn.jeeson.backend.entity.RestBean;
import cn.jeeson.backend.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 用户登录功能，/api/auth/**全面放行
 * @Author fjz
 * @Date 2023-4-24
 * @Version 1.0
 */
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
    private final String USERNAME_REGEX = "^[a-zA-Z0-9一-龥]+$";

    @Resource
    AuthorizeService service;

    /**
     * 注册(用户不存在)，发送验证邮件
     */
    @PostMapping("/valid-register-email")
    public RestBean<String> validateRegisterEmail(@Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                                  HttpSession session) {
        String s = service.sendValidateEmail(email, session.getId(), false, "register");
        if(s == null)
            return RestBean.success("邮件发送成功，请查看邮箱");
        else
            return RestBean.failure(400, s);
    }

    /**
     * 用户存在，发送验证邮件
     */
    @PostMapping("/valid-email")
    public RestBean<String> validateResetEmail(@Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                               @RequestParam("used") String used,
                                               HttpSession session) {
        String s = service.sendValidateEmail(email, session.getId(), true, used);
        if(s == null)
            return RestBean.success("邮件发送成功，请查看邮箱");
        else
            return RestBean.failure(400, s);
    }

    /**
     * 验证并注册
     */
    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp = USERNAME_REGEX) @Length(min = 6, max = 16) @RequestParam("username") String username,
                                         @Length(min = 6, max = 16) @RequestParam("password") String password,
                                         @Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                         @Length(min = 4, max = 4) @RequestParam("code") String code,
                                         HttpSession session) {
        String s = service.validateAndRegister(username, password, email, code, session.getId());
        if(s == null)
            return RestBean.success("注册成功");
        else
            return RestBean.failure(400, s);
    }

    /**
     * 重置密码，验证邮件验证码
     * 1. 发送验证邮件
     * 2. 验证验证码正确，正确则在session中存标记
     * 3. 用户发起重置密码请求，存在标记则重置
     */
    @PostMapping("/start-reset")
    public RestBean<String> startReset(@Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                          @Length(min = 4, max = 4) @RequestParam("code") String code,
                                          HttpSession session) {
        String s = service.validateOnly(email, code, session.getId());
        if(s == null) {
            session.setAttribute("reset-password", email);
            return  RestBean.success();
        } else {
            return RestBean.failure(400, s);
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/do-reset")
    public RestBean<String> resetPassword(@Length(min = 6, max = 16) @RequestParam("password") String password,
                                          HttpSession session) {
        String email = (String) session.getAttribute("reset-password");
        if(email == null) {
            return RestBean.failure(401, "请先完成邮箱验证");
        } else if(service.resetPassword(email, password)) {
            session.removeAttribute("reset-password");
            return RestBean.success("密码重置成功");
        } else {
            return RestBean.failure(500, "系统错误，请联系管理员");
        }
    }

}
