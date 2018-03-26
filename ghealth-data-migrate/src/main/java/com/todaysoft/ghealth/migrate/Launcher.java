package com.todaysoft.ghealth.migrate;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.todaysoft.ghealth.migrate.core.Facade;

public class Launcher
{
    public static void main(String[] args)
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/context.xml");
        Facade facade = context.getBean(Facade.class);
        facade.startup();
        context.close();
    }
}
