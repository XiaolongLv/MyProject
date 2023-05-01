package cn.jeeson.backend.config;

import cn.jeeson.backend.entity.RestBean;
import cn.jeeson.backend.service.AuthorizeService;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @Description None
 * @Author fjz
 * @Date 2023-4-21
 * @Version 1.0
 */
@Configuration
//@EnableWebSecurity //使用spring security无需加此注解
public class SecurityConfiguration {

    @Resource
    AuthorizeService authorizeService;

    @Resource
    DataSource dataSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                           PersistentTokenRepository persistentTokenRepository) throws Exception {
        return httpSecurity
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/api/auth/login")
                .successHandler(this::onAuthenticationSuccess)
                .failureHandler(this::onAuthenticationFailure)
                .and()
                .logout()
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(this::onAuthenticationSuccess)
                .and()
                .rememberMe()
                .rememberMeParameter("remember")
                .tokenRepository(persistentTokenRepository)
                .tokenValiditySeconds(3600*24*7)
                .and()
                .csrf()
                .disable()
                .cors()
                .configurationSource(this.corsConfigurationSource())
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(this::onAuthenticationFailure)
                .and()
                .build();
    }


    /**
     * token持久化存储
     */
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }

    /**
     * 解决前端跨域问题
     */
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern("*");//服务器地址http://localhost:5173
        cors.setAllowCredentials(true);
        cors.addAllowedHeader("*");//修改为服务器地址
        cors.addAllowedMethod("*");//修改为服务器地址
        cors.addExposedHeader("*");//修改为服务器地址
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(authorizeService)
                .and()
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setCharacterEncoding("utf-8");
        if(request.getRequestURI().endsWith("/login"))
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
        else if (request.getRequestURI().endsWith("/logout"))
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("已退出登录")));
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401,exception.getMessage())));
    }


}
