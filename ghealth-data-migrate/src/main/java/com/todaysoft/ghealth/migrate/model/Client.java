package com.todaysoft.ghealth.migrate.model;

import java.sql.Timestamp;
import java.util.Date;

public class Client
{
    private String id;
    
    private String name;
    
    private String sex;
    
    private Timestamp birth;
    
    private String maritalStatus;
    
    private String phone;
    
    private String email;
    
    private String createId;
    
    private String agentId;
    
    private String isDeleted;
    
    private String company;
    
    private String createName;
    
    private String nation;
    
    private String hometown;
    
    private String occupation;
    
    public String getOccupation()
    {
        return occupation;
    }
    
    public void setOccupation(String occupation)
    {
        this.occupation = occupation;
    }
    
    public String getHometown()
    {
        return hometown;
    }
    
    public void setHometown(String hometown)
    {
        this.hometown = hometown;
    }
    
    public String getNation()
    {
        return nation;
    }
    
    public void setNation(String nation)
    {
        this.nation = nation;
    }
    
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
    
    public String getPhone()
    {
        return phone;
    }
    
    public void setPhone(String phone)
    {
        this.phone = phone;
    }
    
    public String getAgentId()
    {
        return agentId;
    }
    
    public void setAgentId(String agentId)
    {
        this.agentId = agentId;
    }
    
    public String getSex()
    {
        return sex;
    }
    
    public void setSex(String sex)
    {
        this.sex = sex;
    }
    
    public Timestamp getBirth()
    {
        return birth;
    }
    
    public void setBirth(Timestamp birth)
    {
        this.birth = birth;
    }
    
    public String getMaritalStatus()
    {
        return maritalStatus;
    }
    
    public void setMaritalStatus(String maritalStatus)
    {
        this.maritalStatus = maritalStatus;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public String getCreateId()
    {
        return createId;
    }
    
    public void setCreateId(String createId)
    {
        this.createId = createId;
    }
    
    public String getIsDeleted()
    {
        return isDeleted;
    }
    
    public void setIsDeleted(String isDeleted)
    {
        this.isDeleted = isDeleted;
    }
    
    public String getCompany()
    {
        return company;
    }
    
    public void setCompany(String company)
    {
        this.company = company;
    }
    
    public String getCreateName()
    {
        return createName;
    }
    
    public void setCreateName(String createName)
    {
        this.createName = createName;
    }
}
