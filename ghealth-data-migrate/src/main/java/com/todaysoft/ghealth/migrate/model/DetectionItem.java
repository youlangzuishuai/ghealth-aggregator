package com.todaysoft.ghealth.migrate.model;

public class DetectionItem
{
    public static String ITEM_CATEGORY_JB = "1";//疾病风险
    
    public static String ITEM_CATEGORY_YJ = "2";//烟酒
    
    public static String ITEM_CATEGORY_ET = "3";//儿童
    
    public static String ITEM_CATEGORY_YY = "4";//用药
    
    public static String ITEM_CATEGORY_QT = "5";//其他
    
    private String id;
    
    private String name;
    
    private String type;
    
    private String cancerId;
    
    private String semantic;
    
    private String gender;
    
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
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getCancerId()
    {
        return cancerId;
    }
    
    public void setCancerId(String cancerId)
    {
        this.cancerId = cancerId;
    }
    
    public String getSemantic()
    {
        return semantic;
    }
    
    public void setSemantic(String semantic)
    {
        this.semantic = semantic;
    }
    
    public String getGender()
    {
        return gender;
    }
    
    public void setGender(String gender)
    {
        this.gender = gender;
    }
}
