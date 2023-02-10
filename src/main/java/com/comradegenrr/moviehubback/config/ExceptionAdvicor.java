package com.comradegenrr.moviehubback.config;

import com.comradegenrr.moviehubback.standerio.StanderOutput;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class ExceptionAdvicor {

    @ExceptionHandler(IOException.class)
    public StanderOutput ioexceptionHandler(IOException e){
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setErrMsg("查询出了一些问题");
        return standerOutput;
    }

}
