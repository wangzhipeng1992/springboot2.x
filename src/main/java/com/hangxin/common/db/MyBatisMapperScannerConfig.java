package com.hangxin.common.db;

import java.util.Properties;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tk.mybatis.spring.mapper.MapperScannerConfigurer;

/***
 * MyBatis扫描接口，使用的tk.mybatis.spring.mapper.MapperScannerConfigurer，如果你不使用通用Mapper，可以改为org.xxx...**
 * 
 * @author wang
 * @since 2018-05-29 10:11
 */
@Configuration
// TODO 注意，由于MapperScannerConfigurer执行的比较早，所以必须有下面的注解（因为这个对象的扫描，需要在MyBatisConfig的后面注入）
@AutoConfigureAfter(MyBatisConfig.class)
public class MyBatisMapperScannerConfig {

	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer() {
		MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
		mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");	// 获取之前注入的beanName为sqlSessionFactory对象
		mapperScannerConfigurer.setBasePackage("com.hangxin.persistence.mapper");	// 指定路径
		Properties properties = new Properties();
		properties.setProperty("mappers", "com.hangxin.util.MyMapper");   // 不去扫描此接口
		properties.setProperty("notEmpty", "false");
		properties.setProperty("IDENTITY", "MYSQL");
		mapperScannerConfigurer.setProperties(properties);
		return mapperScannerConfigurer;
	}

}
