package com.todaysoft.ghealth.mybatis.searcher;

import java.util.Set;

public class AgencySearcher
{
    private String code;
    
    private boolean codeExactMatches;
    
    private String name;
    
    private String primaryUsername;
    
    private String productId;
    
    private boolean primaryUsernameExactMatches;
    
    private Set<String> excludeKeys;
    
    private Integer offset;
    
    private Integer limit;
    
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
    
    public String getPrimaryUsername()
    {
        return primaryUsername;
    }
    
    public void setPrimaryUsername(String primaryUsername)
    {
        this.primaryUsername = primaryUsername;
    }
    
    public boolean isPrimaryUsernameExactMatches()
    {
        return primaryUsernameExactMatches;
    }
    
    public void setPrimaryUsernameExactMatches(boolean primaryUsernameExactMatches)
    {
        this.primaryUsernameExactMatches = primaryUsernameExactMatches;
    }
    
    public Set<String> getExcludeKeys()
    {
        return excludeKeys;
    }
    
    public void setExcludeKeys(Set<String> excludeKeys)
    {
        this.excludeKeys = excludeKeys;
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
    
    public String getProductId()
    {
        return productId;
    }
    
    public void setProductId(String productId)
    {
        this.productId = productId;
    }
}
