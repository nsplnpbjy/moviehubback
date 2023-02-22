package com.comradegenrr.moviehubback.config;

import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@Aspect
@ConditionalOnProperty(prefix = "aspect.logaop",name = "enable",havingValue = "true")
public class LogAop {

    @Around("execution(* com.comradegenrr.moviehubback.service.mainfunc.MainService.*(..))")
    public StanderOutput doLogForSearch(ProceedingJoinPoint joinPoint) throws Throwable {
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
            logger.error("不可预知的意外发生了");
            throw new IOException();
        }
    }

    @Around("execution(* com.comradegenrr.moviehubback.service.testfunc.TestService.*(..))")
    public StanderOutput doLogForTest(ProceedingJoinPoint joinPoint) throws Throwable{
        Logger logger = LoggerFactory.getLogger(LogAop.class);
        String methodName = joinPoint.getSignature().getName();
        StanderOutput standerOutput = (StanderOutput) joinPoint.proceed();
        if(Objects.isNull(standerOutput.getMoviePojoList())){
            logger.info(methodName+" has been proceed");
            return standerOutput;
        }
        else{
            logger.info(methodName+" has been proceed, return movie count:"+standerOutput.getMoviePojoList().size());
            return standerOutput;
        }
    }

}
