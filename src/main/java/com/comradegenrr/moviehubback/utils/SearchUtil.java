package com.comradegenrr.moviehubback.utils;

import com.comradegenrr.moviehubback.standerio.MoviePojo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class SearchUtil {

    private String baseUrl = "https://www.xbshare.cc/";


    public Document getFirstHtml(String searchText) throws IOException {
        String encodedSearchText = URLEncoder.encode(searchText, StandardCharsets.UTF_8);
        String url = baseUrl+"query/"+encodedSearchText;
        //return  JsoupSSLHelper.getConnection(url).get();//
        return Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
                .header("Accept","*/*")
                .header("Content-Type","application/json")
                .header("Accept-Encoding","gzip,deflate,br")
                .header("Connection","keep-alive").timeout(3000).validateTLSCertificates(false).get();
    }

    public List<MoviePojo> getMoviePageHtml(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36").timeout(3000).validateTLSCertificates(false).get();
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
            MoviePojo addition = new MoviePojo(movieTitle,magnetUrl);
            moviePojoList.add(addition);
        }
        return moviePojoList;
    }


    //这个分析方法只适用于www.dyjihe2.com网站
    public List<MoviePojo> doParse(Document doc) throws IOException {
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
            moviePojoList.addAll(getMoviePageHtml(link));
        }
        return moviePojoList;
    }

}
