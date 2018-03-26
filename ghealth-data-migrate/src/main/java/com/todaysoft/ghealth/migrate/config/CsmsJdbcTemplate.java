package com.todaysoft.ghealth.migrate.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CsmsJdbcTemplate extends JdbcTemplate
{
    @Override
    @Resource(name = "csmsDataSource")
    public void setDataSource(DataSource dataSource)
    {
        super.setDataSource(dataSource);
    }
}
