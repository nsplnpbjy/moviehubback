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
}
