package com.todaysoft.ghealth.migrate.model;

public class Locus
{
    private String id;
    
    private String locusCode;
    
    private String gene;
    
    public String getLocusCode()
    {
        return locusCode;
    }
    
    public void setLocusCode(String locusCode)
    {
        this.locusCode = locusCode;
    }
    
    public String getGene()
    {
        return gene;
    }
    
    public void setGene(String gene)
    {
        this.gene = gene;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
}
