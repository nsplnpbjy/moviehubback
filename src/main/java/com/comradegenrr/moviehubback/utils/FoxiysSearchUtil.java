package com.comradegenrr.moviehubback.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.comradegenrr.moviehubback.standerio.MoviePojo;

@Component
public class FoxiysSearchUtil implements SearchUtil{

    private String baseUrl = "https://www.foxiys.com/";
    
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<MoviePojo> doSearchWithMongoDBAndInternet(String searchText) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MoviePojo> doSearchWithMongoDB(String searchText) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Document getFirstHtml(String searchText) throws IOException {
        String encodedSearchText = URLEncoder.encode(searchText, StandardCharsets.UTF_8);
        String url = baseUrl+"query/"+encodedSearchText;
        return Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
        .header("Accept","*/*")
        .header("Content-Type","application/json")
        .header("Accept-Encoding","gzip,deflate,br")
        .header("Connection","keep-alive").timeout(3000).validateTLSCertificates(false).get();
    }

    @Override
    public List<MoviePojo> getMoviePageHtml(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<MoviePojo> doParse(Document doc) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
