package com.comradegenrr.moviehubback.standerio;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class StanderOutput {
    private ArrayList<MoviePojo> moviePojoList;
    public StanderOutput(){
        moviePojoList = new ArrayList<MoviePojo>();
    }

    public void add(MoviePojo moviePojo){
        moviePojoList.add(moviePojo);
    }

    public JSONArray jsonLized(){
        return new JSONArray(moviePojoList);
    }
}
