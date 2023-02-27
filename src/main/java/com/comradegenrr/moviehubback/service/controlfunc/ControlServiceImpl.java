package com.comradegenrr.moviehubback.service.controlfunc;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.comradegenrr.moviehubback.standerio.MoviePojo;
import com.comradegenrr.moviehubback.standerio.SearchCachePojo;
import com.comradegenrr.moviehubback.standerio.StanderOutput;

@Service
public class ControlServiceImpl implements ControlService{

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public StanderOutput getAllMovies() {
        StanderOutput standerOutput = new StanderOutput();
        standerOutput.setMoviePojoList((ArrayList<MoviePojo>) mongoTemplate.findAll(MoviePojo.class));
        return standerOutput;
    }

    @Override
    public StanderOutput cleanAllSearchTextCache() {
        mongoTemplate.dropCollection(SearchCachePojo.class);
        return new StanderOutput("DONE",null,null);
    }

    @Override
    public StanderOutput cleanAllMovies() {
        mongoTemplate.dropCollection(MoviePojo.class);
        cleanAllSearchTextCache();
        return new StanderOutput("DONE",null,null);
    }
    
}
