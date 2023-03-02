package com.comradegenrr.moviehubback.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.comradegenrr.moviehubback.standerio.MoviePojo;

@Component(value = "FoxiysSearchUtil")
@ConditionalOnProperty(prefix = "searchutil.foxiys",name = "enable",havingValue = "true")
public class FoxiysSearchUtil implements SearchUtil{

    private String baseUrl = "https://www.foxiys.com/";
    public final static String lootFrom = "FoxiysSearchUtil";

    @Autowired
    MongoTemplate mongoTemplate;

    @Resource(name = "jsEngine")
    ScriptEngine scriptEngine;

    @Resource(name = "jsInvocable")
    Invocable invocable;


    //网页搜索法
    @Override
    public List<MoviePojo> doSearchWithInternet(String searchText) throws IOException, NoSuchMethodException, ScriptException {
        return doParse(getFirstHtml(searchText),searchText);
    }


    //数据库搜索法
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



    //----------------分隔线-----------------------------------------------------------------------------------------------------------------------------------------------------
    //以下为分析方法，必须为每一个不同的网址自行配置
    //得到搜索后首页
    @Override
    public Document getFirstHtml(String searchText) throws IOException, NoSuchMethodException, ScriptException {
        String encodedSearchText = encryptMetond(searchText);
        String url = baseUrl+"query/"+encodedSearchText;
        return Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
        .header("Accept","*/*")
        .header("Content-Type","application/json")
        .header("Accept-Encoding","gzip,deflate,br")
        .header("Connection","keep-alive").timeout(30000).validateTLSCertificates(false).get();
    }


    //得到每一个搜索结果的子页面(带磁力连接页),并装载信息
    //这一页的url参数必须是https://www.foxiys.com/d-4d1m-magnet.html类似
    @Override
    public List<MoviePojo> getMoviePageHtml(String url,String searchText) throws IOException {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36").timeout(30000).validateTLSCertificates(false).get();
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
            String movieSubTitle = "";
            Element movieSubTitleElement = movieLi.select("button[data-name]").first();
            if(!Objects.isNull(movieSubTitleElement)){
                movieSubTitle = movieSubTitleElement.attr("data-name");
            }
            String movieTitle = movieMainTitle+movieSubTitle;
            String magnet = movieLi.select("input[type=text]").first().attr("value");
            moviePojoList.add(new MoviePojo(movieTitle, magnet, moviePicUrl,FoxiysSearchUtil.lootFrom,searchText));
        }
        return moviePojoList;
    }


    //逐个进入搜索后的主页面内的各个电影连接
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

    private String encryptMetond(String t) throws ScriptException, NoSuchMethodException, FileNotFoundException{
        String output = "";
        if(t.length()<7){
            t = t + "       ".substring(0, 7 - t.length());
          }
          if(t.length() > 15){
            t = t.substring(0, 15);
        }
        output = (String) invocable.invokeFunction("encode", t);
        output = output.replaceAll("[/]", "_");
        output = output.replaceAll("[+]", "-");
        output = output.replaceAll("[=]", "");
        String output1 = output.substring(0, 6);
        String output2 = output.substring(6);
        output = output1+"j"+output2;
        return output;
    }
    
}
