package com.todaysoft.ghealth.migrate.model;

public class OrderTask
{
    private String createName;
    
    private String wordFile;
    
    private String pdfFile;
    
    public String getCreateName()
    {
        return createName;
    }
    
    public void setCreateName(String createName)
    {
        this.createName = createName;
    }
    
    public String getWordFile()
    {
        return wordFile;
    }
    
    public void setWordFile(String wordFile)
    {
        this.wordFile = wordFile;
    }
    
    public String getPdfFile()
    {
        return pdfFile;
    }
    
    public void setPdfFile(String pdfFile)
    {
        this.pdfFile = pdfFile;
    }
}
