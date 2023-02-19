package com.comradegenrr.moviehubback.service;

import com.comradegenrr.moviehubback.standerio.MoviePojo;
import com.comradegenrr.moviehubback.standerio.SearchCachePojo;
import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;
import com.comradegenrr.moviehubback.utils.SearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

@Service
public class MainServiceImp implements MainService{

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    MongoTemplate mongoTemplate;

    @Resource
    SearchCacheManagerService searchCacheManagerService;

    @Override
    public StanderOutput doSearchWithInternet(StanderInput standerInput) throws IOException {
        List<SearchUtil> searchUtilList =  getSearchUtilImpList();
        List<MoviePojo> moviePojoList = new ArrayList<MoviePojo>();
        for(SearchUtil searchUtil:searchUtilList){
            moviePojoList.addAll(searchUtil.doSearchWithInternet(standerInput.getSearchText()));
        }
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) moviePojoList);
        return standerOutput;
    }


    @Override
    public StanderOutput doSearchWithMongoDB(StanderInput standerInput) throws IOException {
        List<SearchUtil> searchUtilList =  getSearchUtilImpList();
        //因为所有SearchUtil的实现都连接同一个数据库，故以下只用一个SearchUtil操作
        //如果使用不同的数据库，可参考注释
        /* 
        Set<MoviePojo> moviePojoSet = new HashSet<>();
        for(SearchUtil searchUtil:searchUtilList){
            moviePojoSet.addAll(searchUtil.doSearchWithMongoDB(standerInput.getSearchText()));
        }
        */
        List<MoviePojo> moviePojoList = searchUtilList.get(0).doSearchWithMongoDB(standerInput.getSearchText());
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) moviePojoList);
        return standerOutput;
    }

    @Override
    public StanderOutput doSearchSmart(StanderInput standerInput) throws IOException {
        SearchCachePojo searchCachePojo = searchCacheManagerService.getSearchCachePojoBySearchText(standerInput.getSearchText());
        if(Objects.isNull(searchCachePojo)){
            searchCacheManagerService.newSearchCache(standerInput.getSearchText());
            return doSearchWithInternet(standerInput);
        }
        else{
            searchCacheManagerService.newSearchCache(standerInput.getSearchText());
            return doSearchWithMongoDB(standerInput);
        }
    }

    @Override
    public StanderOutput hotKeyClean() {
        mongoTemplate.dropCollection(SearchCachePojo.class);
        return new StanderOutput("DONE",null,null);
    }

    @Override
    public StanderOutput movieCacheClean() {
        mongoTemplate.dropCollection(MoviePojo.class);
        return new StanderOutput("DONE",null,null);
    }


    @Override
    public List<SearchUtil> getSearchUtilImpList() {
        Map<String, SearchUtil> searchUtilMap = applicationContext.getBeansOfType(SearchUtil.class);
        List<SearchUtil> searchUtilList = searchUtilMap.values().stream().collect(Collectors.toList());
        return searchUtilList; 
    }
}
