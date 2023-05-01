package cn.jeeson.backend.controller;

import cn.jeeson.backend.entity.RestBean;
import cn.jeeson.backend.entity.user.AccountUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * @Description None
 * @Author fjz
 * @Date 2023-4-28
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 验证登录状态，获取用户登录所需基础信息
     */
    @GetMapping("/status")
    public RestBean<AccountUser> status(@SessionAttribute("account") AccountUser accountUser) {
        return RestBean.success(accountUser);
    }

}
