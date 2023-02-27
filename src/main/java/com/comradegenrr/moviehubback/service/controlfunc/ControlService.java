package com.comradegenrr.moviehubback.service.controlfunc;

import com.comradegenrr.moviehubback.standerio.StanderOutput;

public interface ControlService {
    
    public StanderOutput getAllMovies();

    public StanderOutput cleanAllSearchTextCache();

    public StanderOutput cleanAllMovies();
}
