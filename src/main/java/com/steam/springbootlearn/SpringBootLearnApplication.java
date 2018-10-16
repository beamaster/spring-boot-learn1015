package com.steam.springbootlearn;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.steam.springbootlearn.util.FTPUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Map;

@EnableScheduling
@EnableCaching
@SpringBootApplication
public class SpringBootLearnApplication extends SpringBootServletInitializer
    implements SchedulingConfigurer {

    private static Logger logger = LoggerFactory.getLogger(SpringBootLearnApplication.class);

    private static String fromPath;
    private static String toPath;
    private static String suffix;

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

    /**
     * 创建启动传值参数
     * @return
     */
    private static Options createOption(){
        Options options = new Options();
        options.addOption("help",false,"帮助");
        options.addOption(OptionBuilder.create("fromPath"));
//        options.addOption(OptionBuilder.withArgName("FromPath").hasArg().withDescription("原始路径").create("fromPath"));
        options.addOption(OptionBuilder.hasArg(true).withDescription("原始路径").create("fromPath"));
        options.addOption(OptionBuilder.hasArg(true).withDescription("目标路径").create("toPath"));
        options.addOption(OptionBuilder.hasArg(true).withDescription("后缀格式").create("suffix"));
        return options;
    }

    public static void main(String[] args) {
        Options options = createOption();
        CommandLineParser parser = new PosixParser();
        try {
            CommandLine command = parser.parse(options,args);
            if (command.hasOption("help")){
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("ant",options);
                return;
            }else if((fromPath = command.getOptionValue("fromPath"))==null ||
                    (toPath = command.getOptionValue("toPath"))==null ||
                    (suffix=command.getOptionValue("suffix"))==null){
                logger.info("———缺少必要参数—————");
                return;
            }
        } catch (ParseException pe) {
            logger.info("main...ParseException..." + pe);
        }
        SpringApplication.run(SpringBootLearnApplication.class, args);
        logger.info("it's time:" + System.currentTimeMillis());
    }


    /**
     * 每天执行6次
     * 7:15,7:30,7:45
     * 8:15,8:30,8:45
     */
//    @Scheduled(cron = "0 15,30,45 7,8 6/1 * ?")
    @Scheduled(fixedRate =60000)
    public void execTransmitTask(){
        logger.info("开始传输...");
        Map<String,Object> map = FTPUtil.uploadSpecifiedSuffixFile(fromPath,toPath,suffix);
        logger.info("传输成功...");
        logger.info("传输失败...");
    }
}
