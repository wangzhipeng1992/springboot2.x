package com.hangxin.common.db;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.github.pagehelper.PageInterceptor;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * MyBatis基础配置
 *
 * @author wang
 * @since 2018-05-29 10:11
 */
@Configuration
@EnableTransactionManagement	// 配置支持事务
public class MyBatisConfig implements TransactionManagementConfigurer {
	@Value("${databaseRW.name}")
	private String rwName;

	@Value("${databaseRW.driverClassName}")
	private String rwClassName;

	@Value("${databaseRW.url}")
	private String rwUrl;

	@Value("${databaseRW.username}")
	private String rwUsername;

	@Value("${databaseRW.password}")
	private String rwPassword;

	@Value("${databaseRW.maxPoolSize}")
	private int rwMaxPoolSize;

	@Value("${databaseRW.minPoolSize}")
	private int rwMinPoolSize;

	@Value("${databaseRW.initialPoolSize}")
	private int rwInitialPoolSize;

	@Value("${databaseRW.maxIdleTime}")
	private int rwMaxIdleTime;

	/**
	 * 配置dataSource，使用C3P0连接池
	 * 
	 * @throws PropertyVetoException
	 */
	@Bean(destroyMethod = "close")	// 当数据库连接不使用的时候,就把该连接重新放到数据池中,方便下次使用调用
	@Primary
	public DataSource dataSource1() throws PropertyVetoException {

		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		// 数据源名称
		dataSource.setDataSourceName(rwName);
		dataSource.setDriverClass(rwClassName);
		dataSource.setJdbcUrl(rwUrl);
		dataSource.setUser(rwUsername);
		dataSource.setPassword(rwPassword);
		System.err.println(rwMaxPoolSize);
		dataSource.setMaxPoolSize(Integer.valueOf(rwMaxPoolSize));
		dataSource.setMinPoolSize(Integer.valueOf(rwMinPoolSize));
		dataSource.setInitialPoolSize(Integer.valueOf(rwInitialPoolSize));
		dataSource.setMaxIdleTime(Integer.valueOf(rwMaxIdleTime));
		return dataSource;
	}

	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() {
		try {
			SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
			bean.setDataSource(dataSource1());
			// 分页插件

			Properties properties = new Properties();
			properties.setProperty("supportMethodsArguments", "true");
			properties.setProperty("returnPageInfo", "check");
			// 添加插件
			Interceptor i = new PageInterceptor();
			i.setProperties(properties);
			bean.setPlugins(new Interceptor[] { i });

			// 指定mapper xml目录
			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			bean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
	
			return bean.getObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@Bean
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		try {
			return new DataSourceTransactionManager(dataSource1());
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}