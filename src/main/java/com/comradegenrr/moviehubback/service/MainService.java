package com.comradegenrr.moviehubback.service;

import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;

import java.io.IOException;

public interface MainService {
    public StanderOutput doSearchWithInternet(StanderInput standerInput) throws IOException;

    public StanderOutput doSearchWithMongoDB(StanderInput standerInput) throws IOException;

    public StanderOutput doSearchSmart(StanderInput standerInput) throws IOException;

    public StanderOutput hotKeyClean();

    public StanderOutput movieCacheClean();

}
