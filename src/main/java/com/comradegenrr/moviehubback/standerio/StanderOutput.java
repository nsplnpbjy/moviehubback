package com.comradegenrr.moviehubback.standerio;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class StanderOutput {

    private String errMsg;
    private String avatarUrl;
    private ArrayList<MoviePojo> moviePojoList;
    public StanderOutput(){
        moviePojoList = new ArrayList<MoviePojo>();
    }

}
