package com.comradegenrr.moviehubback.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MovieAuthenticationEntryPoint implements AuthenticationEntryPoint{
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                response.setContentType("application/json;charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code", "-1");
                jsonObject.put("msg", "no permited");
                String json = objectMapper.writeValueAsString(jsonObject);
                PrintWriter out = response.getWriter();
                out.write(json);
                out.flush();
                out.close();
    }
    
}
