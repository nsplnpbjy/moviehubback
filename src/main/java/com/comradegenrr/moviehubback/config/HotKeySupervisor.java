package com.comradegenrr.moviehubback.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.comradegenrr.moviehubback.standerio.SearchCachePojo;

@Component
public class HotKeySupervisor {

    @Autowired
    MongoTemplate mongoTemplate;

    @Scheduled(cron = "0 15 10 ? * *")
    public void keepItHot(){
        Logger logger = LoggerFactory.getLogger(HotKeySupervisor.class);
        List<SearchCachePojo> searchCachePojoList =  mongoTemplate.findAll(SearchCachePojo.class); 
        if(searchCachePojoList.isEmpty()){
            return;
        }
        for(SearchCachePojo searchCachePojo:searchCachePojoList){
            long hoursDiff = ChronoUnit.HOURS.between(searchCachePojo.getLastDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),ZonedDateTime
            .now().toLocalDate());
            if(hoursDiff>6){
                Query query = new Query(Criteria.where("id").is(searchCachePojo.getId()));
                mongoTemplate.remove(query, SearchCachePojo.class);
                logger.info("因超过六小时无人使用，已丢弃:"+searchCachePojo.getRecentSearchText());
            }
        }
    }
    
}
