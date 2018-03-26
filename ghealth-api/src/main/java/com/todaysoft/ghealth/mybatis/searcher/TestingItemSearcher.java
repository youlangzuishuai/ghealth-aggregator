package com.todaysoft.ghealth.mybatis.searcher;

import java.util.Set;

public class TestingItemSearcher
{
    private String productId;
    
    private String code;
    
    private boolean codeExactMatches;
    
    private String name;
    
    private Set<String> excludeKeys;
    
    private Integer enabled;
    
    private Integer offset;
    
    private Integer limit;
    
    public String getProductId()
    {
        return productId;
    }
    
    public void setProductId(String productId)
    {
        this.productId = productId;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public boolean isCodeExactMatches()
    {
        return codeExactMatches;
    }
    
    public void setCodeExactMatches(boolean codeExactMatches)
    {
        this.codeExactMatches = codeExactMatches;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Set<String> getExcludeKeys()
    {
        return excludeKeys;
    }
    
    public void setExcludeKeys(Set<String> excludeKeys)
    {
        this.excludeKeys = excludeKeys;
    }
    
    public Integer getEnabled()
    {
        return enabled;
    }
    
    public void setEnabled(Integer enabled)
    {
        this.enabled = enabled;
    }
    
    public Integer getOffset()
    {
        return offset;
    }
    
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    public Integer getLimit()
    {
        return limit;
    }
    
    public void setLimit(Integer limit)
    {
        this.limit = limit;
    }
    
}
