package com.comradegenrr.moviehubback.standerio;

import java.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Document(collection = "hotKey")
public class SearchCachePojo {

    @MongoId
    private String id;
    private String recentSearchText;
    private LocalDateTime lastDate;

    public SearchCachePojo(String recentSearchText,LocalDateTime lastDate){
        this.recentSearchText = recentSearchText;
        this.lastDate = lastDate;
    }

    public SearchCachePojo(){
        
    }
}
