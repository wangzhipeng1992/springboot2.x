package com.hangxin.common.redis;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisPoolConfig extends JedisPoolConfig {

	@Value("${spring.redis.pool.max-idle}")
	private int maxIdle;
	@Value("${spring.redis.pool.min-idle}")
	private int minIdle;
	@Value("${spring.redis.pool.max-wait}")
	private int maxWaitMillis;
	@Value("${spring.redis.pool.max-active}")
	private int maxTotal;
	@Value("${redis.testOnBorrow}")
	private boolean testOnBorrow;

	public RedisPoolConfig() {
		super();
	}

	@PostConstruct // 依赖关系注入完成之后需要执行的方法
	private void init() {
		setMaxIdle(maxIdle);
		setMinIdle(minIdle);
		setMaxWaitMillis(maxWaitMillis);
		setMaxTotal(maxTotal);
		setTestOnBorrow(testOnBorrow);
	}

}
