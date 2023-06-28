package com.comradegenrr.moviehubback.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.comradegenrr.moviehubback.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MovieAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

    @Resource
    JwtUtils jwtUtils;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
                String username = ((UserDetails)authentication.getPrincipal()).getUsername();
                String token = jwtUtils.generateToken(username);
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                response.setContentType("application/json;charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code", "0");
                jsonObject.put("msg", "login success");
                jsonObject.put("username",username);
                jsonObject.put("token", token);
                String json = objectMapper.writeValueAsString(jsonObject);
                PrintWriter out = response.getWriter();
                out.write(json);
                out.flush();
                out.close();
          } 
    
}
