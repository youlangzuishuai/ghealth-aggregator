package com.todaysoft.ghealth.migrate.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GhealthJdbcTemplate extends JdbcTemplate
{
    @Override
    @Resource(name = "ghealthDataSource")
    public void setDataSource(DataSource dataSource)
    {
        super.setDataSource(dataSource);
    }
}
