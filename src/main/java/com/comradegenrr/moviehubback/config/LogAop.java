package com.comradegenrr.moviehubback.config;

import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Aspect
@ConditionalOnProperty(prefix = "aspect.logaop",name = "enable",havingValue = "true")
public class LogAop {

    @Around("execution(* com.comradegenrr.moviehubback.service.MainServiceImp.*(..))")
    public StanderOutput doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(LogAop.class);
        Object[] args = joinPoint.getArgs();
        if (args[0].getClass().equals(StanderInput.class)){
            StanderInput standerInput = (StanderInput) args[0];
            logger.info("搜索内容："+standerInput.getSearchText());
            Object obj = joinPoint.proceed();
            StanderOutput standerOutput = (StanderOutput) obj;
            logger.info("返回条数："+standerOutput.getMoviePojoList().size());
            return standerOutput;
        }
        else {
            logger.error("获取搜索内容失败");
            throw new IOException();
        }
    }

}