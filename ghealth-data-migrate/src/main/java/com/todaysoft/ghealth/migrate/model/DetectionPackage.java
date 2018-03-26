package com.todaysoft.ghealth.migrate.model;

public class DetectionPackage
{
    private String id;
    
    private String code;
    
    private String name;
    
    private String sexRestraint;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getSexRestraint()
    {
        return sexRestraint;
    }
    
    public void setSexRestraint(String sexRestraint)
    {
        this.sexRestraint = sexRestraint;
    }
}
