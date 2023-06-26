package com.comradegenrr.moviehubback.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.comradegenrr.moviehubback.service.securityfunc.MovieUserService;
import com.comradegenrr.moviehubback.standerio.UserForLogin;

@RestController
public class SecurityController {

    @Resource
    MovieUserService movieUserService;

    @PostMapping("/login")
    public void login() {
    }

    @PostMapping("/regist")
    public JSONObject regist(@RequestParam String username,@RequestParam String password){
        UserForLogin userForLogin = new UserForLogin(username, password);
        JSONObject jsonObject = new JSONObject();
        if(!movieUserService.doRegist(userForLogin)){
            jsonObject.put("code", "-3");
            jsonObject.put("msg", "注册失败，请尝试换一个用户名");
            return jsonObject;
        }
        jsonObject.put("code", "0");
        jsonObject.put("msg", "注册成功");
        return jsonObject;
    }

}
