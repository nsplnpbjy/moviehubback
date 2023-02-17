package com.comradegenrr.moviehubback.utils;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Document;

import com.comradegenrr.moviehubback.standerio.MoviePojo;

public interface SearchUtil {

    //检查数据库+网页搜索法
    //如果数据库没有，则插入
    public List<MoviePojo> doSearchWithMongoDBAndInternet(String searchText) throws IOException;

    //仅检查数据库搜索法
    public List<MoviePojo> doSearchWithMongoDB(String searchText) throws IOException;

    //得到搜索后首页
    public Document getFirstHtml(String searchText) throws IOException;

     //得到每一个搜索结果的子页面(带磁力连接页),并装载信息
     public List<MoviePojo> getMoviePageHtml(String url);

    //逐个进入搜索后的主页面内的各个电影连接
    public List<MoviePojo> doParse(Document doc) throws IOException;
    
}
