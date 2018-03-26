package com.todaysoft.ghealth.migrate.utils;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesReader
{
    public Map<String, String> getProperties()
    {
        Properties props = new Properties();
        Map<String, String> map = new HashMap<String, String>();
        try
        {
            InputStream in = PropertiesReader.class.getClassLoader().getResourceAsStream("nation.properties");
            props.load(new InputStreamReader(in, "UTF-8"));
            Enumeration en = props.propertyNames();
            while (en.hasMoreElements())
            {
                String key = (String)en.nextElement();
                String property = props.getProperty(key);
                map.put(key, property);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return map;
    }
}
