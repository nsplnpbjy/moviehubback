package com.comradegenrr.moviehubback.service.mainfunc;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.comradegenrr.moviehubback.standerio.SearchCachePojo;

@Service
public class SearchCacheManagerServiceImpl implements SearchCacheManagerService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void newSearchCache(String searchText) {
        Query query = new Query(Criteria.where("recentSearchText").is(searchText));
        List<SearchCachePojo> searchCachePojoListFromDB = mongoTemplate.find(query, SearchCachePojo.class);
        if(searchCachePojoListFromDB.size()>1){
            for(SearchCachePojo searchCachePojoFromDB:searchCachePojoListFromDB){
                query = new Query(Criteria.where("id").is(searchCachePojoFromDB.getId()));
                mongoTemplate.remove(query, SearchCachePojo.class);
            }
            SearchCachePojo insertSearchCachePojo = new SearchCachePojo(searchText,LocalDateTime.now());
            mongoTemplate.insert(insertSearchCachePojo);
        }
        else if(searchCachePojoListFromDB.size()==0){
            SearchCachePojo insertSearchCachePojo = new SearchCachePojo(searchText,LocalDateTime.now());
            mongoTemplate.insert(insertSearchCachePojo);
        }
        else{
            SearchCachePojo searchCachePojo = searchCachePojoListFromDB.get(0);
            query = new Query(Criteria.where("id").is(searchCachePojo.getId()));
            Update update = Update.update("LocalDateTime", LocalDateTime.now());
            mongoTemplate.updateMulti(query, update,SearchCachePojo.class);
        }
    }

    @Override
    public List<SearchCachePojo> getAllValiedSearchCache() {
        return mongoTemplate.findAll(SearchCachePojo.class);
    }

    @Override
    public SearchCachePojo getSearchCachePojoBySearchText(String searchText) {
        Query query = new Query(Criteria.where("recentSearchText").is(searchText));
        List<SearchCachePojo> searchCachePojoListFromDB = mongoTemplate.find(query, SearchCachePojo.class);
        if(searchCachePojoListFromDB.size()==0){
            return null;
        }
        else if(searchCachePojoListFromDB.size()>1){
            for(SearchCachePojo searchCachePojoFromDB:searchCachePojoListFromDB){
                query = new Query(Criteria.where("id").is(searchCachePojoFromDB.getId()));
                mongoTemplate.remove(query, SearchCachePojo.class);
            }
            SearchCachePojo insertSearchCachePojo = new SearchCachePojo(searchText,LocalDateTime.now());
            mongoTemplate.insert(insertSearchCachePojo);
            
            query = new Query(Criteria.where("recentSearchText").is(searchText));
            return mongoTemplate.findOne(query,SearchCachePojo.class);
        }
        else{
            return searchCachePojoListFromDB.get(0);
        }
    }
    
}
