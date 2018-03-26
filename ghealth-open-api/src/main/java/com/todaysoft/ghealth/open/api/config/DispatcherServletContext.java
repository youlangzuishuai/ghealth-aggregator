package com.todaysoft.ghealth.open.api.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.MappedInterceptor;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaysoft.ghealth.open.api.service.credentials.CredentialsInterceptor;

@EnableWebMvc
@ComponentScan(basePackages = "com.todaysoft.ghealth.open.api.mvc")
@EnableAspectJAutoProxy
public class DispatcherServletContext extends WebMvcConfigurerAdapter
{
    @Autowired
    private CredentialsInterceptor credentialsInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        MappedInterceptor interceptor = new MappedInterceptor(new String[] {"/open/**"}, credentialsInterceptor);
        registry.addInterceptor(interceptor);
    }
    
    @Override
    public void addFormatters(FormatterRegistry registry)
    {
        registry.addFormatter(new DateFormatter());
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        converters.add(converter);
    }
}
