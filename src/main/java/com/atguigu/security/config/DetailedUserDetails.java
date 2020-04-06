package com.atguigu.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class DetailedUserDetails implements UserDetailsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据loginacct查出相应的admin
        String adminSql = "select * from t_admin where loginacct=?";
        Map<String, Object> adminMap = jdbcTemplate.queryForMap(adminSql, username);

        log.info("adminMap={}",adminMap);

        // 根据adminId 连接查询出所拥有的role
        String roleSql = "SELECT t_role.* FROM t_role LEFT JOIN t_admin_role ON t_admin_role.roleid=t_role.id " +
                                 "WHERE t_admin_role.adminid=?";
        List<Map<String, Object>> roleList = jdbcTemplate.queryForList(roleSql, adminMap.get("id"));

        log.info("roleName={}",roleList);

        // 根据adminId 连接查询出所拥有的permission
        String permissionSql = "SELECT distinct t_permission.* FROM t_permission \n" +
                                    "LEFT JOIN t_role_permission ON t_permission .id=t_role_permission.permissionid\n" +
                                    "LEFT JOIN t_admin_role on t_role_permission.roleid=t_admin_role.roleid\n" +
                                    "WHERE t_admin_role.adminid=?";
        List<Map<String, Object>> permissionList = jdbcTemplate.queryForList(permissionSql, adminMap.get("id"));

        log.info("permissionList={}",permissionList);

        // 根据查询结果 拼接用户权限
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Map<String, Object> map : roleList) {
            String roleName = map.get("name").toString();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName)); // 角色权限拼接
        }

        for (Map<String, Object> map : permissionList) {
            String permissionName = map.get("name").toString();
            authorities.add(new SimpleGrantedAuthority(permissionName));
        }

        log.info("最终权限是:authorities={}",authorities);


        // 创建UserDetails实现类返回
        String password = adminMap.get("userpswd").toString();
        return  new User(username,password,authorities);
    }
}
