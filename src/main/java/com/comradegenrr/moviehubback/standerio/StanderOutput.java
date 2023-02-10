package com.comradegenrr.moviehubback.standerio;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@JsonSerialize
public class StanderOutput {

    private String errMsg;
    private ArrayList<MoviePojo> moviePojoList;
    public StanderOutput(){
        moviePojoList = new ArrayList<MoviePojo>();
    }

}
