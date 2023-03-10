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
import javax.script.ScriptException;

@Service
public class MainServiceImp implements MainService{

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    MongoTemplate mongoTemplate;

    @Resource
    SearchCacheManagerService searchCacheManagerService;

    @Override
    public StanderOutput doSearchWithInternet(StanderInput standerInput) throws IOException, NoSuchMethodException, ScriptException {
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
        //????????????SearchUtil????????????????????????????????????????????????????????????SearchUtil??????
        //????????????????????????????????????????????????
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
    public StanderOutput doSearchSmart(StanderInput standerInput) throws IOException, NoSuchMethodException, ScriptException {
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





    //--------?????????????????????????????????-----------------------------------------------------------------------------------------------------------------------------------------
    //???????????????????????????????????????????????????SearchUtil?????????
    private List<SearchUtil> getSearchUtilImpList() {
        Map<String, SearchUtil> searchUtilMap = applicationContext.getBeansOfType(SearchUtil.class);
        List<SearchUtil> searchUtilList = searchUtilMap.values().stream().collect(Collectors.toList());
        return searchUtilList; 
    }


    //?????????????????????????????????
    //??????moviePojoListFromInternet???MoviePojo???movieUrl???????????????
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


    //??????movieUrl??????
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
