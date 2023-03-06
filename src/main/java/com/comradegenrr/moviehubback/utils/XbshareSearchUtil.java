package com.comradegenrr.moviehubback.utils;

import com.comradegenrr.moviehubback.standerio.MoviePojo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;


//这个分析方法只适用于https://www.xbshare.cc网站
@Component(value="XbshareSearchUtil")
@ConditionalOnProperty(prefix = "searchutil.xbshare",name = "enable",havingValue = "true")
public class XbshareSearchUtil implements SearchUtil{

    private String baseUrl = "https://www.xbshare.cc/";
    public final static String lootFrom = "XbshareSearchUtil";


    @Autowired
    MongoTemplate mongoTemplate;
    
    @Resource(name = "jsEngine")
    ScriptEngine scriptEngine;

    @Resource(name = "jsInvocable")
    Invocable invocable;


    //仅网页搜索法
    @Override
    public List<MoviePojo> doSearchWithInternet(String searchText) throws IOException, NoSuchMethodException, ScriptException {
        return doParse(getFirstHtml(searchText),searchText);
    }

    //仅检查数据库搜索法
    public List<MoviePojo> doSearchWithMongoDB(String searchText) throws IOException{
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
    public Document getFirstHtml(String searchText) throws IOException, NoSuchMethodException, ScriptException {
        String encodedSearchText = encryptMetond(searchText);
        String url = baseUrl+"query/"+encodedSearchText;
        //return  JsoupSSLHelper.getConnection(url).get();//
        return Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                .header("Accept","*/*")
                .header("Content-Type","application/json")
                .header("Accept-Encoding","gzip,deflate,br")
                .header("Connection","keep-alive").timeout(30000).validateTLSCertificates(false).get();
    }

    //得到每一个搜索结果的子页面(带磁力连接页),并装载信息
    public List<MoviePojo> getMoviePageHtml(String url,String searchText) throws IOException {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36").timeout(30000).validateTLSCertificates(false).get();
        } catch (IOException e) {
            return new ArrayList<MoviePojo>();
        }
        //List<Element> elementList = doc.select("ul.downurl").first().select("li").stream().toList();
        Element tempDivtables = doc.select("div.container").get(2).select("div.row").first()
                .select("div.col-xs-12").first();
        String baseTitle = tempDivtables.select("div.media-body").first().select("h1").first().text();
        Elements divtables = tempDivtables.select("div.s").get(1)
                .select("div.panel-body").first()
                .select("div.divtable");
        if (divtables.isEmpty()){
            return new ArrayList<MoviePojo>();
        }
        Elements movies = divtables.select("ul.body").select("li.thunder-deal");
        List<MoviePojo> moviePojoList = new ArrayList<MoviePojo>();
        for (Element element:movies){
            String magnetUrl = element.select("input").first().attr("value");
            String movieTitle = baseTitle+" "+element.select("span").first().text();
            String imgUrl = doc.select("img[src]").first().attr("src");
            MoviePojo addition = new MoviePojo(movieTitle,magnetUrl,imgUrl,XbshareSearchUtil.lootFrom,searchText);
            moviePojoList.add(addition);
        }
        return moviePojoList;
    }


    //逐个进入搜索后的主页面内的各个电影连接
    public List<MoviePojo> doParse(Document doc,String searchText) throws IOException {
        Elements searchPageElements = doc.select("div.container").get(2).select("div.row").get(1).select("div.s")
                .first().select("div.panel-w").first()
                .select("div.panel-body").first()
                .select("div.row").first()
                .select("div.col-lg-4");
        List<MoviePojo> moviePojoList = new ArrayList<MoviePojo>();
        List<Element> moviePageElementList = searchPageElements.stream().toList();
        for(Element element:moviePageElementList){
            String link = element.select("div.title-wrapper").first()
                    .select("a").first().attr("href");
            link = link.substring(0,link.length()-5);
            link = "d"+link.substring(2);
            link = baseUrl+link+"-magnet.html";
            moviePojoList.addAll(getMoviePageHtml(link,searchText));
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
