package com.comradegenrr.moviehubback.controller;

import com.comradegenrr.moviehubback.service.MainService;
import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;
import org.springframework.web.bind.annotation.CrossOrigin;
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

    @PostMapping("/s")
    public StanderOutput search(@RequestBody StanderInput standerInput) throws IOException {
        return mainService.doSearch(standerInput);
    }

}
