package com.comradegenrr.moviehubback.service.testfunc;

import com.comradegenrr.moviehubback.standerio.StanderOutput;

public interface TestService {
    
    public StanderOutput getAllMovies();

    public StanderOutput cleanAllSearchTextCache();

    public StanderOutput cleanAllMovies();
}
