package com.comradegenrr.moviehubback.service;

import com.comradegenrr.moviehubback.standerio.StanderInput;
import com.comradegenrr.moviehubback.standerio.StanderOutput;

import java.io.IOException;

public interface MainService {
    public StanderOutput doSearch(StanderInput standerInput) throws IOException;
}
