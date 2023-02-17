package com.comradegenrr.moviehubback.service;

import com.comradegenrr.moviehubback.standerio.MoviePojo;
import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;
import com.comradegenrr.moviehubback.utils.SearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MainServiceImp implements MainService{

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public StanderOutput doSearchWithInternet(StanderInput standerInput) throws IOException {
        Map<String, SearchUtil> searchUtilMap = applicationContext.getBeansOfType(SearchUtil.class);
        List<SearchUtil> searchUtilList =  searchUtilMap.values().stream().collect(Collectors.toList()); 
        List<MoviePojo> moviePojoList = new ArrayList<MoviePojo>();
        for(SearchUtil searchUtil:searchUtilList){
            moviePojoList.addAll(searchUtil.doParse(searchUtil.getFirstHtml(standerInput.getSearchText())));
        }
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) moviePojoList);
        return standerOutput;
    }

    @Override
    public StanderOutput doSearchWithInternetAndMongoDB(StanderInput standerInput) throws IOException {
        Map<String, SearchUtil> searchUtilMap = applicationContext.getBeansOfType(SearchUtil.class);
        List<SearchUtil> searchUtilList =  searchUtilMap.values().stream().collect(Collectors.toList()); 
        List<MoviePojo> moviePojoList = new ArrayList<MoviePojo>();
        for(SearchUtil searchUtil:searchUtilList){
            moviePojoList.addAll(searchUtil.doSearchWithMongoDBAndInternet(standerInput.getSearchText()));
        }
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) moviePojoList);
        return standerOutput;
    }

    @Override
    public StanderOutput doSearchWithMongoDB(StanderInput standerInput) throws IOException {
        Map<String, SearchUtil> searchUtilMap = applicationContext.getBeansOfType(SearchUtil.class);
        List<SearchUtil> searchUtilList =  searchUtilMap.values().stream().collect(Collectors.toList()); 
        List<MoviePojo> moviePojoList = new ArrayList<MoviePojo>();
        for(SearchUtil searchUtil:searchUtilList){
            moviePojoList.addAll(searchUtil.doSearchWithMongoDB(standerInput.getSearchText()));
        }
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) moviePojoList);
        return standerOutput;
    }
}
