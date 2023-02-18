package com.comradegenrr.moviehubback.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.comradegenrr.moviehubback.standerio.MoviePojo;

@Component(value = "FoxiysSearchUtil")
public class FoxiysSearchUtil implements SearchUtil{

    private String baseUrl = "https://www.foxiys.com/";
    public final static String lootFrom = "FoxiysSearchUtil";

    @Autowired
    MongoTemplate mongoTemplate;


    @Override
    public List<MoviePojo> doSearchWithInternet(String searchText) throws IOException {
        List<MoviePojo> returnList = new ArrayList<>();
        List<MoviePojo> interList = doParse(getFirstHtml(searchText),searchText);
        for (MoviePojo m :interList) {
            Query insertQuery = new Query(Criteria.where("movieTitle").is(m.getMovieTitle()).and("movieUrl").is(m.getMovieUrl()));
            MoviePojo moviePojoListFromDb = mongoTemplate.findOne(insertQuery, MoviePojo.class);
            if(Objects.isNull(moviePojoListFromDb)){
                mongoTemplate.insert(m);
                returnList.add(m);
            }
            else{
                if(!moviePojoListFromDb.getBeenSearchedLike().contains(m.getBeenSearchedLike().get(0))){
                    moviePojoListFromDb.getBeenSearchedLike().addAll(m.getBeenSearchedLike());
                    Update update = Update.update("beenSearchedLike", moviePojoListFromDb.getBeenSearchedLike());
                    mongoTemplate.updateFirst(insertQuery, update, MoviePojo.class);
                }
                returnList.add(mongoTemplate.findOne(insertQuery, MoviePojo.class));
            }
        }
        return returnList;
    }


    @Override
    public List<MoviePojo> doSearchWithMongoDB(String searchText) throws IOException {
        Query query = new Query(Criteria.where("beenSearchedLike").is(searchText));
        List<MoviePojo> returnList = new ArrayList<MoviePojo>();
        try {
            returnList = mongoTemplate.find(query, MoviePojo.class);
        } catch (Exception e) {
            throw new IOException("查询失败");
        }
        return returnList;
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

    //这一页的url参数必须是https://www.foxiys.com/d-4d1m-magnet.html类似
    @Override
    public List<MoviePojo> getMoviePageHtml(String url,String searchText) throws IOException {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36").timeout(3000).validateTLSCertificates(false).get();
        } catch (IOException e) {
            return new ArrayList<MoviePojo>();
        }
        String movieMainTitle = doc.select("div[class=s row introduce-content]").first().select("div[class=title-wrapper]").first()
        .select("h1[class=head]").first().text();
        String moviePicUrl = doc.select("div[class=s row introduce-content]").first().select("img").first().attr("src");
        Element downloadContent = doc.select("div[id=download-content]").first();
        Element rowBody = downloadContent.select("ul[class=row body]").first();
        if(Objects.isNull(rowBody)){
            return new ArrayList<MoviePojo>();
        }
        Elements movieLis = rowBody.select("li[data-type=magnet]");
        List<MoviePojo> moviePojoList = new ArrayList<>();
        for(Element movieLi : movieLis){
            String movieSubTitle = movieLi.select("span[class=input-group-addon content]").first().text();
            String movieTitle = movieMainTitle+movieSubTitle;
            String magnet = movieLi.select("input[type=text]").first().attr("value");
            moviePojoList.add(new MoviePojo(movieTitle, magnet, moviePicUrl,FoxiysSearchUtil.lootFrom,searchText));
        }
        return moviePojoList;
    }


    //这里进来的doc是搜索后出现的首页
    @Override
    public List<MoviePojo> doParse(Document doc,String searchText) throws IOException {
        Elements movieRows = doc.select("div[class=container]").get(1).select("div[class=row]").first().select("div[class=s]")
        .first().select("div[class=panel-body]").first().select("div[class=row]").first().select("div[class=col-xs-12 col-sm-6 col-md-6 col-lg-4]");
        List<MoviePojo> moviePojoList = new ArrayList<>();
        for(Element element:movieRows){
            String rowUrl = element.select("a[href]").first().attr("href");
            rowUrl = rowUrl.substring(2,rowUrl.length()-5);
            rowUrl = baseUrl+"d"+rowUrl+"-magnet.html";
            moviePojoList.addAll(getMoviePageHtml(rowUrl,searchText));
        }
        return moviePojoList;
    }
    
}
