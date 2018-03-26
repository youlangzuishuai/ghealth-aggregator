package com.todaysoft.ghealth.open.api.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@ComponentScan("com.todaysoft.ghealth.open.api")
@EnableAspectJAutoProxy
@EnableTransactionManagement
@MapperScan("com.todaysoft.ghealth.open.api.mybatis.mapper")
@PropertySource({"classpath:application-context.properties"})
public class RootContext implements ApplicationContextAware
{
    private static ApplicationContext context;
    
    @Autowired
    private Environment environment;
    
    @Bean(name = "characterEncodingFilter")
    public CharacterEncodingFilter getCharacterEncodingFilter()
    {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("utf-8");
        return filter;
    }
    
    @Bean(name = "dataSource")
    public DataSource getDataSource()
        throws SQLException
    {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("datasource.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("datasource.url"));
        dataSource.setUsername(environment.getRequiredProperty("datasource.username"));
        dataSource.setPassword(environment.getRequiredProperty("datasource.password"));
        dataSource.setFilters("stat,config");
        
        dataSource.setInitialSize(environment.getRequiredProperty("datasource.initial", int.class));
        dataSource.setMaxActive(environment.getRequiredProperty("datasource.max.active", int.class));
        dataSource.setMinIdle(environment.getRequiredProperty("datasource.min.idle", int.class));
        dataSource.setMaxWait(environment.getRequiredProperty("datasource.max.wait", long.class));
        
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
        properties.setProperty("config.decrypt.key", environment.getRequiredProperty("datasource.public.key"));
        dataSource.setConnectProperties(properties);
        return dataSource;
    }
    
    @Bean
    public DataSourceTransactionManager transactionManager()
        throws SQLException
    {
        return new DataSourceTransactionManager(getDataSource());
    }
    
    @Bean
    public SqlSessionFactory sqlSessionFactory()
        throws Exception
    {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(getDataSource());
        return sessionFactory.getObject();
    }
    
    @Override
    public void setApplicationContext(ApplicationContext context)
        throws BeansException
    {
        RootContext.context = context;
    }
    
    public static <T> T getBean(Class<T> requiredType)
    {
        return context.getBean(requiredType);
    }
    
    public static <T> T getBean(String name, Class<T> requiredType)
    {
        return context.getBean(name, requiredType);
    }
}
