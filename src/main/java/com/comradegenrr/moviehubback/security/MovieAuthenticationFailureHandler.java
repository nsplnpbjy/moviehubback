package com.comradegenrr.moviehubback.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MovieAuthenticationFailureHandler implements AuthenticationFailureHandler{

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code", "-2");
                jsonObject.put("msg", "login failure");
                String json = objectMapper.writeValueAsString(jsonObject);
                PrintWriter out = response.getWriter();
                out.write(json);
                out.flush();
                out.close();
          }
    
}
