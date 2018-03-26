package com.todaysoft.ghealth.migrate.model;

import java.util.List;

public class Agent
{
    private String id;
    
    private String name;
    
    private String code;
    
    private String agentAccount;
    
    private String abbr;
    
    private List<User> users;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getAgentAccount()
    {
        return agentAccount;
    }
    
    public void setAgentAccount(String agentAccount)
    {
        this.agentAccount = agentAccount;
    }
    
    public String getAbbr()
    {
        return abbr;
    }
    
    public void setAbbr(String abbr)
    {
        this.abbr = abbr;
    }
    
    public List<User> getUsers()
    {
        return users;
    }
    
    public void setUsers(List<User> users)
    {
        this.users = users;
    }
}
