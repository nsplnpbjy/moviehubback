package com.comradegenrr.moviehubback.service.mainfunc;

import java.util.List;

import com.comradegenrr.moviehubback.standerio.SearchCachePojo;

public interface SearchCacheManagerService {

    public void newSearchCache(String searchText);

    public List<SearchCachePojo> getAllValiedSearchCache();

    public SearchCachePojo getSearchCachePojoBySearchText(String searchText);
    
}
