package cn.jeeson.backend.entity.auth;

import lombok.Data;

/**
 * @Description 登录账号信息
 * @Author fjz
 * @Date 2023-4-23
 * @Version 1.0
 */
@Data
public class Account {

    Long id;
    String email;
    String username;
    String password;


}
