package com.todaysoft.ghealth.migrate.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CdcJdbcTemplate extends JdbcTemplate
{
    @Override
    @Resource(name = "cdcDataSource")
    public void setDataSource(DataSource dataSource)
    {
        super.setDataSource(dataSource);
    }
}
