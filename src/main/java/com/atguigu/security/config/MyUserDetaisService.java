package com.atguigu.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

public class MyUserDetaisService implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT * FROM t_admin WHERE loginacct=?";
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, username);

        return new User(map.get("loginacct").toString(),map.get("userpswd").toString(),
                AuthorityUtils.createAuthorityList("ROLE_学徒","罗汉拳")); // 授权规则 这里写死了 其实也应该从数据库查
    }
}
