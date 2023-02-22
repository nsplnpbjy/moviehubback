package com.comradegenrr.moviehubback.controller;

import com.comradegenrr.moviehubback.service.mainfunc.MainService;
import com.comradegenrr.moviehubback.service.testfunc.TestService;
import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@CrossOrigin("*")
@RestController
public class MainController {

    @Resource
    private MainService mainService;

    @Resource
    private TestService testService;

    @PostMapping("/s")
    public StanderOutput search(@RequestBody StanderInput standerInput) throws IOException {
        return mainService.doSearchSmart(standerInput);
    }


    @GetMapping("/hotkey/clean")
    public StanderOutput hotKeyClean(){
        return testService.cleanAllSearchTextCache();
    }

    @GetMapping("/movies/clean")
    public StanderOutput movieClean(){
        return testService.cleanAllMovies();
    }

    @GetMapping("/movies")
    public StanderOutput findAll(){
        return testService.getAllMovies();
    }

}
