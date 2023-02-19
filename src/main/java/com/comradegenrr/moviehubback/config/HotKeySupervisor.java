package com.comradegenrr.moviehubback.config;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.comradegenrr.moviehubback.standerio.SearchCachePojo;

@Component
@ConditionalOnProperty(prefix = "hotkey.auto-clean",name = "enable",havingValue = "true")
public class HotKeySupervisor {

    @Autowired
    MongoTemplate mongoTemplate;

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void keepItHot(){
        Logger logger = LoggerFactory.getLogger(HotKeySupervisor.class);
        List<SearchCachePojo> searchCachePojoList =  mongoTemplate.findAll(SearchCachePojo.class); 
        logger.info("执行自动清除热键");
        if(searchCachePojoList.isEmpty()){
            logger.info("暂无热键");
            return;
        }
        for(SearchCachePojo searchCachePojo:searchCachePojoList){
            long hoursDiff = ChronoUnit.HOURS.between(searchCachePojo.getLastDate(),LocalDateTime.now());
            if(hoursDiff>100){
                Query query = new Query(Criteria.where("id").is(searchCachePojo.getId()));
                mongoTemplate.remove(query, SearchCachePojo.class);
                logger.info("因超过一百小时无人使用，已丢弃:"+searchCachePojo.getRecentSearchText());
            }
        }
    }
    
}
