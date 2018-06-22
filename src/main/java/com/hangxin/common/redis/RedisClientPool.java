package com.hangxin.common.redis;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 * Redis缓存客户端连接池
 * @author wang
 * @since 2018-05-30 18:11
 */
@Service
public class RedisClientPool {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private JedisPoolConfig jedisPoolConfig; // 连接池配置

	@Value("${redis.server}")
	private String redisServer;

	@Value("${spring.redis.timeout}")
	private int timeout;

	private ShardedJedisPool jedisPool; // 客户端连接池

	@PostConstruct
	private void initialPool() throws Exception{
		logger.info("****初始化Redis客户端****");
		List<JedisShardInfo> list = new ArrayList<>();
		String[] arr = redisServer.split(";");
		for (String ipAndPort : arr) {
			arr = ipAndPort.split(":");
			String ip = arr[0];
			String port = arr[1];
			logger.info("Redis服务器地址：{}:{}", ip, port);
			list.add(new JedisShardInfo(ip, Integer.parseInt(port), timeout));
		}
		jedisPool = new ShardedJedisPool(jedisPoolConfig, list);
	}

	// @PostConstruct 项目启动时候未启动redis时候设置启动失败，这里是否应该放入初始化执行来达到此目的
	ShardedJedis getClientForListener() {
		return jedisPool.getResource();
	}

	public ShardedJedis getClient() {
		try {
			return jedisPool.getResource();
		} catch (JedisException e) {
			Throwable throwable = e.getCause();
			if (throwable instanceof java.util.NoSuchElementException) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e1) {
				}
				return getClient();
			}
			throw e;
		}
	}

}
