package com.comradegenrr.moviehubback.standerio;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "movie")
public class MoviePojo {
    private String movieTitle;
    private String movieUrl;
    private String avatarUrl;
    private String lootFrom;
    public MoviePojo(String movieTtile,String movieUrl,String avatarUrl){
        this.movieTitle = movieTtile;
        this.movieUrl = movieUrl;
        this.avatarUrl = avatarUrl;
    }
    public MoviePojo(String movieTtile,String movieUrl){
        this.movieTitle = movieTtile;
        this.movieUrl = movieUrl;
    }
    public MoviePojo(){
        
    }
}
