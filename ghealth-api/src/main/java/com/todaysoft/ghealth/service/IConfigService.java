package com.todaysoft.ghealth.service;

import com.todaysoft.ghealth.base.response.model.Config;
import com.todaysoft.ghealth.service.impl.PagerQueryHandler;

public interface IConfigService extends PagerQueryHandler<Config>
{
    void modify(Config data);
    
    Config get(String id);
}
