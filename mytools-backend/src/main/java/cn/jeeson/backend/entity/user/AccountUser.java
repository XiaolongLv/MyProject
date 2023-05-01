package cn.jeeson.backend.entity.user;

import lombok.Data;

/**
 * @Description 用户登录所需基础信息
 * @Author fjz
 * @Date 2023-4-28
 * @Version 1.0
 */
@Data
public class AccountUser {

    Long id;
    String username;
    String email;
    int role;
}
