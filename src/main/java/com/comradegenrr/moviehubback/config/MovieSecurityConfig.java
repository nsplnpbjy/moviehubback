package com.comradegenrr.moviehubback.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.comradegenrr.moviehubback.security.MovieAuthenticationEntryPoint;
import com.comradegenrr.moviehubback.security.MovieAuthenticationFailureHandler;
import com.comradegenrr.moviehubback.security.MovieAuthenticationSuccessHandler;
import com.comradegenrr.moviehubback.service.securityfunc.MovieUserDetailsService;

@Configurable
@EnableWebSecurity
public class MovieSecurityConfig extends WebSecurityConfigurerAdapter{
    
    @Autowired
    private MovieUserDetailsService movieUserDetailsService;

    @Autowired
    private MovieAuthenticationFailureHandler movieAuthenticationFailureHandler;
    @Autowired
    private MovieAuthenticationSuccessHandler movieAuthenticationSuccessHandler;

    @Autowired
    private MovieAuthenticationEntryPoint movieAuthenticationEntryPoint;

    @Resource
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests().antMatchers("/regist","/login/").permitAll()
            .anyRequest().authenticated().and().formLogin().loginProcessingUrl("/login")
            .usernameParameter("username").passwordParameter("password")
            .successHandler(movieAuthenticationSuccessHandler).failureHandler(movieAuthenticationFailureHandler)
            .and().exceptionHandling().authenticationEntryPoint(movieAuthenticationEntryPoint)
            .and().addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(movieUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    @Bean(name = "passwordEncoder")
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
