package com.comradegenrr.moviehubback.standerio;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@Document(collection = "movie")
public class MoviePojo {
    @MongoId
    private String id;
    private String movieTitle;
    private String movieUrl;
    private String avatarUrl;
    private String lootFrom;
    private List<String> beenSearchedLike;

    public MoviePojo(String movieTtile,String movieUrl,String avatarUrl){
        this.movieTitle = movieTtile;
        this.movieUrl = movieUrl;
        this.avatarUrl = avatarUrl;
    }
    public MoviePojo(String movieTtile,String movieUrl,String avatarUrl,String lootFrom){
        this.movieTitle = movieTtile;
        this.movieUrl = movieUrl;
        this.avatarUrl = avatarUrl;
        this.lootFrom = lootFrom;
    }
    public MoviePojo(String movieTtile,String movieUrl){
        this.movieTitle = movieTtile;
        this.movieUrl = movieUrl;
    }

    public MoviePojo(String movieTtile,String movieUrl,String avatarUrl,String lootFrom,String beenSearchedLike){
        this.movieTitle = movieTtile;
        this.movieUrl = movieUrl;
        this.avatarUrl = avatarUrl;
        this.lootFrom = lootFrom;
        if(Objects.isNull(this.beenSearchedLike)){
            this.beenSearchedLike = new ArrayList<String>();
        }
        this.beenSearchedLike.add(beenSearchedLike);
    }

    public MoviePojo(){
        
    }
}
