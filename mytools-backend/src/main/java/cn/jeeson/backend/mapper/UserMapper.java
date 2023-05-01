package cn.jeeson.backend.mapper;

import cn.jeeson.backend.entity.auth.Account;
import cn.jeeson.backend.entity.user.AccountUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Description None
 * @Author fjz
 * @Date 2023-4-23
 * @Version 1.0
 */
@Mapper
public interface UserMapper {

    /**
     * 通过用户名或邮箱查询账号
     */
    @Select("select * from db_account where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmail(String text);

    /**
     * 查询用户登录所需基础信息
     */
    @Select("select * from db_account where username = #{text} or email = #{text}")
    AccountUser findAccountUserByNameOrEmail(String text);

    /**
     * 注册用户
     */
    @Insert("insert into db_account (email, username, password) values (#{email}, #{username}, #{password})")
    int createAccount(String username, String password, String email);

    /**
     * 重置密码
     */
    @Update("update db_account set password = #{password} where email = #{email}")
    int resetPasswordByEmail(String email, String password);
}
