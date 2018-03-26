package com.todaysoft.ghealth.service;

import com.todaysoft.ghealth.base.response.model.Config;
import com.todaysoft.ghealth.model.config.ConfigSearcher;
import com.todaysoft.ghealth.support.Pagination;

public interface IConfigService
{
    Pagination<Config> pagination(ConfigSearcher searcher, int pageNo, int pageSize);
    
    void modify(Config data);
    
    Config get(Config data);
}
