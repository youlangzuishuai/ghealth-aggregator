package com.todaysoft.ghealth.migrate.core;

import java.util.UUID;

public class KeyGenerator
{
    public static String uuid()
    {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
