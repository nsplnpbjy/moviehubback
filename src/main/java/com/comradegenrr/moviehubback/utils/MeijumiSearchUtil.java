package com.comradegenrr.moviehubback.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.script.ScriptException;

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

import com.comradegenrr.moviehubback.standerio.MoviePojo;

@Component(value = "MeijumiSearchUtil")
@ConditionalOnProperty(prefix = "searchutil.meijumi",name = "enable",havingValue = "true")
public class MeijumiSearchUtil implements SearchUtil{

    private String baseUrl = "https://www.meijumi.net/";
    private static final String lootFrom = "Meijumi";

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<MoviePojo> doSearchWithInternet(String searchText) throws NoSuchMethodException, IOException, ScriptException{
        return doParse(getFirstHtml(searchText), searchText);
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
    public Document getFirstHtml(String searchText) throws IOException, NoSuchMethodException, ScriptException {
        String encodedSearchText = URLEncoder.encode(searchText, "UTF-8");
        String url = baseUrl + "?s=" + encodedSearchText;
        return Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
        .header("Accept","*/*")
        .header("Content-Type","application/json")
        .header("Accept-Encoding","gzip,deflate,br")
        .header("Connection","keep-alive").timeout(30000).validateTLSCertificates(false).get();
    }

    @Override
    public List<MoviePojo> getMoviePageHtml(String url, String searchText) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
        .header("Accept","*/*")
        .header("Content-Type","application/json")
        .header("Accept-Encoding","gzip,deflate,br")
        .header("Connection","keep-alive").timeout(30000).validateTLSCertificates(false).get();

        if(Objects.isNull(doc)){
            return new ArrayList<MoviePojo>();
        }
        Element entryDiv = doc.select("div[class=entry-content-2]").first();
        if(Objects.isNull(entryDiv)){
            return new ArrayList<MoviePojo>();
        }
        Element singleDiv = entryDiv.select("div[class=single-content]").first();
        if(Objects.isNull(singleDiv)){
            return new ArrayList<MoviePojo>();
        }
        Element img = singleDiv.select("img[decoding=async]").first();
        if(Objects.isNull(img)){
            return new ArrayList<MoviePojo>();
        }
        String picUrl = img.attr("src");
        Element mainTitleElement = doc.select("h1[class=entry-title]").first();
        if(Objects.isNull(mainTitleElement)){
            return new ArrayList<MoviePojo>();
        }
        String mainTitle = mainTitleElement.text();
        Elements ps = singleDiv.select("p");
        if(Objects.isNull(ps)){
            return new ArrayList<MoviePojo>();
        }
        ps.remove(0);
        Elements as = ps.select("a[href]");
        List<MoviePojo> moviePojoList = new ArrayList<>();
        for(Element a:as){
            if(!isMagnet(a.attr("href"))){
                continue;
            }
            String subTitle = a.text();
            MoviePojo moviePojo = new MoviePojo(mainTitle+subTitle,a.attr("href"),picUrl,lootFrom,searchText);
            moviePojoList.add(moviePojo);
        }
        return moviePojoList;
    }

    @Override
    public List<MoviePojo> doParse(Document doc, String searchText) throws IOException {
        if(Objects.isNull(doc)){
            return new ArrayList<MoviePojo>();
        }
        Elements articles = doc.select("article[class=archive-list]");
        if(Objects.isNull(articles)){
            return new ArrayList<MoviePojo>();
        }
        List<MoviePojo> moviePojoList = new ArrayList<>();
        for(Element article:articles){
            if(article.select("a[href]").first()==null){
                continue;
            }
            String url = article.select("a[href]").first().attr("href");
            moviePojoList.addAll(getMoviePageHtml(url, searchText));
        }
        return moviePojoList;
    }

    private Boolean isMagnet(String link){
        if(Objects.isNull(link)||link.isEmpty()){
            return false;
        }
        if(link.contains("www.meijumi.net")){
            return false;
        }
        if(link.contains("pan.baidu.com")){
            return false;
        }
        if(link.contains("pan.xunlei.com")){
            return false;
        }
        if(link.contains("www.aliyundrive.com")){
            return false;
        }
        if(link.contains("pan.quark.cn")){
            return false;
        }
        return true;
    }
    
}
