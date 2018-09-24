package com.ethan.datasource.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.ethan.datasource.common.DataSourceKey;
import com.ethan.datasource.common.DynamicRoutingDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DynamicDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "multiple.datasource.master")
    public DataSource dbMaster(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "multiple.datasource.slave1")
    public DataSource dbSlave1(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "multiple.datasource.slave2")
    public DataSource dbSlave2(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "multiple.datasource.other")
    public DataSource dbOther(){
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 动态数据源核心
     * @return 数据源实例
     */
    @Bean
    public DataSource dynamicDataSource(){
        DynamicRoutingDataSource ds = new DynamicRoutingDataSource();
        ds.setDefaultTargetDataSource(dbMaster());

        Map<Object, Object> dsMap = new HashMap<>(4);
        dsMap.put(DataSourceKey.DB_MASTER, dbMaster());
        dsMap.put(DataSourceKey.DB_SLAVE1, dbSlave1());
        dsMap.put(DataSourceKey.DB_SLAVE2, dbSlave2());
        dsMap.put(DataSourceKey.DB_OTHER, dbOther());
        ds.setTargetDataSources(dsMap);

        return ds;
    }

    /**
     * sqlSession会话工厂
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        //此处设置为了解决找不到mapper文件的问题
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/com/ethan/datasource/mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory());
    }

    /**
     * 事务管理
     * @return 事务管理实例
     */
    @Bean
    public PlatformTransactionManager platformTransactionManager(){
        return new DataSourceTransactionManager(dynamicDataSource());
    }
}
