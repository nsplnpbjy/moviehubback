package com.comradegenrr.moviehubback.service;

import com.comradegenrr.moviehubback.standerio.MoviePojo;
import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;
import com.comradegenrr.moviehubback.utils.SearchUtil;

import java.io.IOException;
import java.util.List;

public interface MainService {
    
    public StanderOutput doSearchWithInternet(StanderInput standerInput) throws IOException;

    public StanderOutput doSearchWithMongoDB(StanderInput standerInput) throws IOException;

    public StanderOutput doSearchSmart(StanderInput standerInput) throws IOException;

    public List<MoviePojo> makeListSingleByMovieUrl(List<MoviePojo> moviePojoList);

    public List<MoviePojo> mergeIntoMongoDB(List<MoviePojo> moviePojoList);

    public StanderOutput hotKeyClean();

    public StanderOutput movieCacheClean();

    public List<SearchUtil> getSearchUtilImpList();

}
