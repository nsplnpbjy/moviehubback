package com.comradegenrr.moviehubback.service.mainfunc;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) mergeIntoMongoDB(makeListSingleByMovieUrl(moviePojoList)));
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
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) mergeIntoMongoDB(makeListSingleByMovieUrl(moviePojoList)));
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





    //--------以下是实现类的私有方法-----------------------------------------------------------------------------------------------------------------------------------------
    //这个方法用于返回所有注册在容器中的SearchUtil的实现
    private List<SearchUtil> getSearchUtilImpList() {
        Map<String, SearchUtil> searchUtilMap = applicationContext.getBeansOfType(SearchUtil.class);
        List<SearchUtil> searchUtilList = searchUtilMap.values().stream().collect(Collectors.toList());
        return searchUtilList; 
    }


    //将列表向数据库进行查改
    //参数moviePojoListFromInternet中MoviePojo的movieUrl不能有重复
    private List<MoviePojo> mergeIntoMongoDB(List<MoviePojo> moviePojoListFromInternet) {
        List<MoviePojo> returnList = new ArrayList<>();
        if(moviePojoListFromInternet.isEmpty()){
            returnList = moviePojoListFromInternet;
            return returnList;
        }
        else{
            for(MoviePojo moviePojoFromInternet:moviePojoListFromInternet){
                Query query = new Query(Criteria.where("movieUrl").is(moviePojoFromInternet.getMovieUrl()));
                List<MoviePojo> moviePojoListFromDB = mongoTemplate.find(query, MoviePojo.class);
                if(moviePojoListFromDB.isEmpty()){
                    returnList.add(moviePojoFromInternet);
                    mongoTemplate.save(moviePojoFromInternet);
                }
                else if(moviePojoListFromDB.size()>1){
                    MoviePojo insertMoviePojo = moviePojoListFromDB.get(0);
                    Set<String> restBeenSearchedLike = new HashSet<>();
                    for(MoviePojo m:moviePojoListFromDB){
                        restBeenSearchedLike.addAll(new HashSet<>(m.getBeenSearchedLike()));
                    }
                    restBeenSearchedLike.addAll(new HashSet<>(moviePojoFromInternet.getBeenSearchedLike()));
                    insertMoviePojo.setBeenSearchedLike(new ArrayList<>(restBeenSearchedLike));
                    for(MoviePojo m:moviePojoListFromDB){
                        Query deletQuery = new Query(Criteria.where("id").is(m.getId()));
                        mongoTemplate.remove(deletQuery,MoviePojo.class);
                    }
                    returnList.add(insertMoviePojo);
                    mongoTemplate.save(insertMoviePojo);
                }
                else{
                    MoviePojo updateMoviePojo = moviePojoListFromDB.get(0);
                    if(!updateMoviePojo.getBeenSearchedLike().containsAll(moviePojoFromInternet.getBeenSearchedLike())){
                        updateMoviePojo.getBeenSearchedLike().addAll(moviePojoFromInternet.getBeenSearchedLike());
                    }
                    returnList.add(updateMoviePojo);
                    mongoTemplate.save(updateMoviePojo);
                }
            }
            return returnList;
        }
    }


    //根据movieUrl去重
    private List<MoviePojo> makeListSingleByMovieUrl(List<MoviePojo> moviePojoList) {
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
