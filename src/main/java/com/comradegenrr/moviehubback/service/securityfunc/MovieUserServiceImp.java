package com.comradegenrr.moviehubback.service.securityfunc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.comradegenrr.moviehubback.standerio.UserForLogin;
import com.comradegenrr.moviehubback.standerio.UserPojo;

@Service
public class MovieUserServiceImp implements MovieUserService{
    
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Boolean doRegist(UserForLogin userForLogin) {
        Query query = new Query(Criteria.where("username").is(userForLogin.getUsername()));
        List<UserPojo> userList =  mongoTemplate.find(query,UserPojo.class);
        if(!userList.isEmpty()){
            return false;
        }
        String username = userForLogin.getUsername();
        String password = passwordEncoder.encode(userForLogin.getPassword());
        mongoTemplate.insert(new UserPojo(username, password));
        return true;

    }
    
}
