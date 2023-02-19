package com.comradegenrr.moviehubback.controller;

import com.comradegenrr.moviehubback.service.MainService;
import com.comradegenrr.moviehubback.standerio.MoviePojo;
import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;

@CrossOrigin("*")
@RestController
public class MainController {

    @Resource
    private MainService mainService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("/s")
    public StanderOutput search(@RequestBody StanderInput standerInput) throws IOException {
        return mainService.doSearchSmart(standerInput);
    }


    @GetMapping("/hotkey/clean")
    public StanderOutput hotKeyClean(){
        return mainService.hotKeyClean();
    }

    @GetMapping("/movies/clean")
    public StanderOutput movieClean(){
        return mainService.movieCacheClean();
    }

    @GetMapping("/movies")
    public StanderOutput findAll(){
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) mongoTemplate.findAll(MoviePojo.class));
        return standerOutput;
    }

}
