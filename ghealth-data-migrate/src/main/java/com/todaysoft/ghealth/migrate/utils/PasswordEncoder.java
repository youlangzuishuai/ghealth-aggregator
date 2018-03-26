package com.todaysoft.ghealth.migrate.utils;

public interface PasswordEncoder
{
    String encode(CharSequence rawPassword);
    
    boolean matches(CharSequence rawPassword, String encodedPassword);
}