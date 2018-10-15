package com.steam.springbootlearn;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.sql.DataSource;

@EnableScheduling
@EnableCaching
@SpringBootApplication
public class SpringBootLearnApplication extends SpringBootServletInitializer
    implements SchedulingConfigurer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder applicationBuilder){
        return applicationBuilder.sources(SpringBootLearnApplication.class);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        TaskScheduler taskScheduler = getTaskScheduler();
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler getTaskScheduler(){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }


    @Bean(name = "mysql127Source")
    @ConfigurationProperties(prefix = "mysql127.datasource")
    public DataSource mysql127Source(){
        return DataSourceBuilder.create().type(ComboPooledDataSource.class).build();
    }

    @Bean(name = "mysql235Source")
    @ConfigurationProperties(prefix = "mysql235.datasource")
    public DataSource mysql235Source(){
        return DataSourceBuilder.create().type(ComboPooledDataSource.class).build();
    }

    @Bean(name = "oracle235Source")
    @ConfigurationProperties(prefix = "oracle235.datasource")
    public DataSource oracle235Source(){
        return DataSourceBuilder.create().type(ComboPooledDataSource.class).build();
    }

    @Bean(name = "mysql127Template")
    public JdbcTemplate mysql127Template(@Qualifier("mysql127Source") DataSource source){
        return new JdbcTemplate(source);
    }
    @Bean(name = "mysql235Template")
    public JdbcTemplate mysql235Template(@Qualifier("mysql235Source") DataSource source){
        return new JdbcTemplate(source);
    }
    @Bean(name = "oracle235Template")
    public JdbcTemplate oracle235Template(@Qualifier("oracle235Source") DataSource source){
        return new JdbcTemplate(source);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootLearnApplication.class, args);
        System.out.println("hello,it's time:" + System.currentTimeMillis());
    }

    /**
     * 每天执行6次
     * 7:15,7:30,7:45
     * 8:15,8:30,8:45
     */
    @Scheduled(cron = "0 15,30,45 7,8 6/1 * ?")
    public void execTask1(){
        //do something
    }
}
