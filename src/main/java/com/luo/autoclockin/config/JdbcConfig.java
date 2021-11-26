package com.luo.autoclockin.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@PropertySource("classpath:jdbc.properties")
@MapperScan("com.luo.autoclockin.Dao")
public class JdbcConfig {
    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        var dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    /*
         需要导入spring-jdbc的包，mybatis底层要用到这个事务管理器，然后这个可写可不写

         @Bean
         public TransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource);
         }
     */

    /**
     * 整合mybatis<br>
     * 把mapper放进容器中
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        /*
            手动添加mapper的xml多个文件（stuMapper.xml位于resources目录下）

            sqlSessionFactoryBean.setMapperLocations(new ClassPathResource("stuMapper.xml"),
                    new ClassPathResource("xxx.xml"));

        */
        return new SqlSessionFactoryBean() {{
            setDataSource(dataSource);
        }}.getObject();
    }
}
