package com.todaysoft.ghealth.migrate.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@PropertySource({"classpath:jdbc.properties"})
public class JdbcConfig
{
    @Autowired
    private Environment environment;
    
    @Bean(name = "csmsDataSource")
    public DataSource getCSMSDataSource()
        throws SQLException
    {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("hsgene.csms.datasource.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("hsgene.csms.datasource.url"));
        dataSource.setUsername(environment.getRequiredProperty("hsgene.csms.datasource.username"));
        dataSource.setPassword(environment.getRequiredProperty("hsgene.csms.datasource.password"));
        dataSource.setFilters("stat,config");
        
        dataSource.setInitialSize(environment.getRequiredProperty("hsgene.csms.datasource.initial", int.class));
        dataSource.setMaxActive(environment.getRequiredProperty("hsgene.csms.datasource.max.active", int.class));
        dataSource.setMinIdle(environment.getRequiredProperty("hsgene.csms.datasource.min.idle", int.class));
        dataSource.setMaxWait(environment.getRequiredProperty("hsgene.csms.datasource.max.wait", long.class));
        
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(30000);
        
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(1800000);
        
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestOnReturn(false);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        
        Properties properties = new Properties();
        properties.setProperty("config.decrypt", "true");
        properties.setProperty("config.decrypt.key", environment.getRequiredProperty("hsgene.csms.datasource.public.key"));
        dataSource.setConnectProperties(properties);
        return dataSource;
    }
    
    @Bean(name = "cdcDataSource")
    public DataSource getCDCDataSource()
        throws SQLException
    {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("hsgene.cdc.datasource.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("hsgene.cdc.datasource.url"));
        dataSource.setUsername(environment.getRequiredProperty("hsgene.cdc.datasource.username"));
        dataSource.setPassword(environment.getRequiredProperty("hsgene.cdc.datasource.password"));
        dataSource.setFilters("stat,config");
        
        dataSource.setInitialSize(environment.getRequiredProperty("hsgene.cdc.datasource.initial", int.class));
        dataSource.setMaxActive(environment.getRequiredProperty("hsgene.cdc.datasource.max.active", int.class));
        dataSource.setMinIdle(environment.getRequiredProperty("hsgene.cdc.datasource.min.idle", int.class));
        dataSource.setMaxWait(environment.getRequiredProperty("hsgene.cdc.datasource.max.wait", long.class));
        
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(30000);
        
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(1800000);
        
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestOnReturn(false);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        
        Properties properties = new Properties();
        properties.setProperty("config.decrypt", "true");
        properties.setProperty("config.decrypt.key", environment.getRequiredProperty("hsgene.cdc.datasource.public.key"));
        dataSource.setConnectProperties(properties);
        return dataSource;
    }
    
    @Bean(name = "ghealthDataSource")
    public DataSource getGhealthDataSource()
        throws SQLException
    {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("ghealth.datasource.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("ghealth.datasource.url"));
        dataSource.setUsername(environment.getRequiredProperty("ghealth.datasource.username"));
        dataSource.setPassword(environment.getRequiredProperty("ghealth.datasource.password"));
        dataSource.setFilters("stat,config");
        
        dataSource.setInitialSize(environment.getRequiredProperty("ghealth.datasource.initial", int.class));
        dataSource.setMaxActive(environment.getRequiredProperty("ghealth.datasource.max.active", int.class));
        dataSource.setMinIdle(environment.getRequiredProperty("ghealth.datasource.min.idle", int.class));
        dataSource.setMaxWait(environment.getRequiredProperty("ghealth.datasource.max.wait", long.class));
        
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(30000);
        
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(1800000);
        
        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestOnReturn(false);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        
        Properties properties = new Properties();
        properties.setProperty("config.decrypt", "true");
        properties.setProperty("config.decrypt.key", environment.getRequiredProperty("ghealth.datasource.public.key"));
        dataSource.setConnectProperties(properties);
        return dataSource;
    }
}
