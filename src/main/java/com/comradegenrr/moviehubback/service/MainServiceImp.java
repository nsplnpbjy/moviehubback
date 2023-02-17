package com.comradegenrr.moviehubback.service;

import com.comradegenrr.moviehubback.standerio.MoviePojo;
import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;
import com.comradegenrr.moviehubback.utils.XbshareSearchUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MainServiceImp implements MainService{

    @Resource
    XbshareSearchUtil searchUtil;

    @Override
    public StanderOutput doSearch(StanderInput standerInput) throws IOException {
        List<MoviePojo> moviePojoList = searchUtil.doParse(searchUtil.getFirstHtml(standerInput.getSearchText()));
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) moviePojoList);
        return standerOutput;
    }

    @Override
    public StanderOutput doSearchWithInternetAndMongoDB(StanderInput standerInput) throws IOException {
        List<MoviePojo> moviePojoList = searchUtil.doSearchWithMongoDBAndInternet(standerInput.getSearchText());
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) moviePojoList);
        return standerOutput;
    }
}
