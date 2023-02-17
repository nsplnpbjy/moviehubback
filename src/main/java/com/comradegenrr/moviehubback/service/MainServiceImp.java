package com.comradegenrr.moviehubback.service;

import com.comradegenrr.moviehubback.standerio.MoviePojo;
import com.comradegenrr.moviehubback.standerio.SearchCachePojo;
import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;
import com.comradegenrr.moviehubback.utils.SearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MainServiceImp implements MainService{

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public StanderOutput doSearchWithInternet(StanderInput standerInput) throws IOException {
        Map<String, SearchUtil> searchUtilMap = applicationContext.getBeansOfType(SearchUtil.class);
        List<SearchUtil> searchUtilList =  searchUtilMap.values().stream().collect(Collectors.toList()); 
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
        Map<String, SearchUtil> searchUtilMap = applicationContext.getBeansOfType(SearchUtil.class);
        List<SearchUtil> searchUtilList =  searchUtilMap.values().stream().collect(Collectors.toList()); 
        Set<MoviePojo> moviePojoSet = new HashSet<>();
        for(SearchUtil searchUtil:searchUtilList){
            moviePojoSet.addAll(searchUtil.doSearchWithMongoDB(standerInput.getSearchText()));
        }
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setMoviePojoList(new ArrayList<>(moviePojoSet));
        return standerOutput;
    }

    @Override
    public StanderOutput doSearchSmart(StanderInput standerInput) throws IOException {
        List<SearchCachePojo> searchCachePojoList =  mongoTemplate.findAll(SearchCachePojo.class); 
        if(searchCachePojoList.isEmpty()){
            mongoTemplate.insert(new SearchCachePojo(standerInput.getSearchText(), new Date()));
            return doSearchWithInternet(standerInput);
        }
        for(SearchCachePojo searchCachePojo:searchCachePojoList){
            if(searchCachePojo.getRecentSearchText().equals(standerInput.getSearchText())){

                //更新热词
                Query query = Query.query(Criteria.where("recentSearchText").is(standerInput.getSearchText()));
                Update update = Update.update("lastDate", new Date());
                mongoTemplate.updateFirst(query, update, SearchCachePojo.class);

                List<MoviePojo> returnList = doSearchWithMongoDB(standerInput).getMoviePojoList();
                if(returnList.isEmpty()){
                    return doSearchWithInternet(standerInput);
                }
                else{
                    StanderOutput standerOutput = new StanderOutput();
                    standerOutput.setMoviePojoList((ArrayList<MoviePojo>) returnList);
                    return standerOutput;
                }
            }
        }
        mongoTemplate.insert(new SearchCachePojo(standerInput.getSearchText(), new Date()));
        return doSearchWithInternet(standerInput);
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
}
