package com.comradegenrr.moviehubback.service.securityfunc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.comradegenrr.moviehubback.standerio.UserPojo;

@Service
public class MovieUserDetailsService implements UserDetailsService{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名从数据库中查询用户信息
        Query query = new Query(Criteria.where("username").is(username));
        UserPojo user =  mongoTemplate.findOne(query,UserPojo.class);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        // 返回一个UserDetails对象，包含用户名、密码和权限
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                AuthorityUtils.commaSeparatedStringToAuthorityList("all"));
    }
    
}
