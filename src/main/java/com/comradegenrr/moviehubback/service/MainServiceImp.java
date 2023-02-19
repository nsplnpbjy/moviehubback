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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
        moviePojoList = makeListSingleByMovieUrl(moviePojoList);
        mergeIntoMongoDB(moviePojoList);
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

    //单独删除电影缓存是危险的，要一并删除热键
    @Override
    public StanderOutput movieCacheClean() {
        mongoTemplate.dropCollection(MoviePojo.class);
        mongoTemplate.dropCollection(SearchCachePojo.class);
        return new StanderOutput("DONE",null,null);
    }


    @Override
    public List<SearchUtil> getSearchUtilImpList() {
        Map<String, SearchUtil> searchUtilMap = applicationContext.getBeansOfType(SearchUtil.class);
        List<SearchUtil> searchUtilList = searchUtilMap.values().stream().collect(Collectors.toList());
        return searchUtilList; 
    }


    //将列表向数据库进行查改
    //参数moviePojoListFromInternet中MoviePojo的movieUrl不能有重复
    @Override
    public List<MoviePojo> mergeIntoMongoDB(List<MoviePojo> moviePojoListFromInternet) {
        if(moviePojoListFromInternet.isEmpty()){
            return moviePojoListFromInternet;
        }
        else{
            for(MoviePojo moviePojoFromInternet:moviePojoListFromInternet){
                Query query = new Query(Criteria.where("movieUrl").is(moviePojoFromInternet.getMovieUrl()));
                List<MoviePojo> moviePojoListFromDB = mongoTemplate.find(query, MoviePojo.class);
                if(moviePojoListFromDB.isEmpty()){
                    mongoTemplate.save(moviePojoFromInternet);
                }
                else if(moviePojoListFromDB.size()>1){
                    MoviePojo insertMoviePojo = moviePojoListFromDB.get(0);
                    if(!insertMoviePojo.getBeenSearchedLike().containsAll(moviePojoFromInternet.getBeenSearchedLike())){
                        insertMoviePojo.getBeenSearchedLike().addAll(moviePojoFromInternet.getBeenSearchedLike());
                    }
                    for(MoviePojo m:moviePojoListFromDB){
                        Query deletQuery = new Query(Criteria.where("id").is(m.getId()));
                        mongoTemplate.remove(deletQuery,MoviePojo.class);
                    }
                    mongoTemplate.save(insertMoviePojo);
                }
                else{
                    MoviePojo updateMoviePojo = moviePojoListFromDB.get(0);
                    if(!updateMoviePojo.getBeenSearchedLike().containsAll(moviePojoFromInternet.getBeenSearchedLike())){
                        updateMoviePojo.getBeenSearchedLike().addAll(moviePojoFromInternet.getBeenSearchedLike());
                    }
                    mongoTemplate.save(updateMoviePojo);
                }
            }
            return moviePojoListFromInternet;
        }
    }


    //根据movieUrl去重
    @Override
    public List<MoviePojo> makeListSingleByMovieUrl(List<MoviePojo> moviePojoList) {
        Map<String,MoviePojo> moviePojoMap = new HashMap<>();
        for(MoviePojo moviePojo:moviePojoList){
            if(moviePojoMap.get(moviePojo.getMovieUrl())==null){
                moviePojoMap.put(moviePojo.getMovieUrl(), moviePojo);
            }
        }
        List<MoviePojo> retList = new ArrayList<MoviePojo>(moviePojoMap.values());
        retList.sort(Comparator.comparing(MoviePojo::getMovieTitle));
        return retList;
    }
}
