package com.todaysoft.ghealth.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.todaysoft.ghealth.base.response.model.Authority;
import com.todaysoft.ghealth.mybatis.mapper.AuthorityMapper;
import com.todaysoft.ghealth.service.IAuthorityService;

@Service
public class AuthorityService implements IAuthorityService
{
    @Autowired
    private AuthorityMapper mapper;
    
    @Override
    public List<Authority> query(Object searcher, int offset, int limit)
    {
        return null;
    }
    
    @Override
    public int count(Object searcher)
    {
        return 0;
    }
    
    @Override
    public List<Authority> list()
    {
        return mapper.list();
    }
    
    @Override
    public List<Authority> getAuthoritiesByParentId(String id)
    {
        return mapper.getAuthoritiesByParentId(id);
    }
    
    @Override
    public List<Authority> getParentAuthorities()
    {
        return mapper.getParentAuthorities();
    }
}
