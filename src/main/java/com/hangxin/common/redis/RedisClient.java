package com.hangxin.common.redis;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.hangxin.util.JsonHelper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.util.SafeEncoder;

/**
 * Redis内存数据库客户端操作封装
 * @author wang
 * @since 2018-05-30 18:11
 */
@Service
@SuppressWarnings("unchecked")
public class RedisClient implements ApplicationContextAware {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private ApplicationContext spring;

	// 序列化为json序列的字节数组
	private final GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer(
			JsonHelper.getObjectMapper());

	private Timer timer = new Timer();

	@Resource
	private RedisClientPool pool;

	/**
	 * redis中写入值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> boolean set(String key, T value) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			jedis.set(rawKey(key), rawValue(value));
			return true;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * redis中写入值,并指定有效时间
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 * @return
	 */
	public <T> boolean set(String key, T value, int seconds) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			jedis.setex(rawKey(key), seconds, rawValue(value));
			return true;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 从redis中取值
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return get(key, Object.class);
	}

	/**
	 * 从redis中取值，返回指定类型
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> T get(String key, Class<T> clazz) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			return readValue(jedis.get(rawKey(key)), clazz);
		} catch (SerializationException e) {
			// 发生SerializationException时删除原来的值，并返回null
			logger.error(String.format("redis.get方法SerializationException,key=%s,class=%s", key, clazz.getName()), e);
			jedis.del(rawKey(key));
			return null;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 往redis中写入Set值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> boolean setAsSet(String key, T value) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			jedis.sadd(rawKey(key), rawValue(value));
			return true;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 往redis中写入Set值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> boolean setAsSet(String key, Set<T> value) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			byte[] keyByte = rawKey(key);
			for (T t : value) {
				jedis.sadd(keyByte, rawValue(t));
			}
			return true;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 判断redis的set中是否包含某值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> boolean existsInSet(String key, T value) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			return jedis.sismember(rawKey(key), rawValue(value));
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 移除redis的set中的值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> boolean removeFromSet(String key, T... value) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			jedis.srem(rawKey(key), rawValue(value));
			return true;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 取出redis中key对应的Set的所有值
	 * 
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> getAsSet(String key, Class<T> clazz) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			Set<byte[]> set = jedis.smembers(rawKey(key));
			if (CollectionUtils.isEmpty(set)) {
				return null;
			}
			Set<T> result = new HashSet<>();
			for (byte[] t : set) {
				result.add(readValue(t, clazz));
			}
			return result;
		} catch (SerializationException e) {
			// 发生SerializationException删除原来的值，并返回null
			logger.error(String.format("redis.getAsSet方法SerializationException,key=%s,class=%s", key, clazz.getName()),
					e);
			jedis.del(rawKey(key));
			return null;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 判断key是否存在
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			return jedis.exists(rawKey(key));
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 设置有效时间
	 * 
	 * @param key
	 * @param seconds
	 *            时间参数：单位-秒
	 * @return
	 */
	public boolean expire(String key, int seconds) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			jedis.expire(rawKey(key), seconds);
			return true;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * redis根据key值自动递增,若发生JedisDataException则删除key重新递增
	 * 
	 * @param key
	 * @return
	 * 
	 */
	public long incr(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			return jedis.incr(rawKey(key));
		} catch (JedisDataException e) {
			jedis.del(key);
			return jedis.incr(key);
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * set if not exists
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public <T> long setnx(String key, T value) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			return jedis.setnx(rawKey(key), rawValue(value));
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 取出redis中的老值同时设置新值
	 * 
	 * @param key
	 * @param value
	 * @param clazz
	 * @return
	 */
	public <T> T getSet(String key, T value, Class<T> clazz) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			byte[] result = jedis.getSet(rawKey(key), rawValue(value));
			return readValue(result, clazz);
		} catch (SerializationException e) {
			// 发生SerializationException时返回null
			logger.error(String.format("redis.get方法SerializationException,key=%s,class=%s", key, clazz.getName()), e);
			return null;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 发布渠道消息
	 * 
	 * @param channel
	 *            渠道名称
	 * @param message
	 *            消息内容
	 */
	public <T> Long publish(String channel, T message) {
		ShardedJedis shardedJedis = null;
		try {
			shardedJedis = pool.getClient();
			byte[] channel_byte = rawKey(channel);
			Jedis jedis = shardedJedis.getShard(channel_byte);
			return jedis.publish(channel_byte, rawValue(message));
		} finally {
			if (null != shardedJedis) {
				shardedJedis.close();
			}
		}
	}

	/**
	 * 延迟发布渠道消息
	 * 
	 * @param channel
	 *            渠道名称
	 * @param message
	 *            消息内容
	 * @param seconds
	 *            延迟秒数
	 */
	public <T> void publishDelay(String channel, T message, long seconds) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				publish(channel, message);
			}
		}, TimeUnit.SECONDS.toMillis(seconds));
	}

	/**
	 * 查看key的剩余有效时间
	 * 
	 * @param key
	 * @return
	 */
	public Long ttl(String key) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			return jedis.ttl(rawKey(key));
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 删除一组key以及key对应的数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(String... keys) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			for (String key : keys) {
				jedis.del(rawKey(key));
			}
			return true;
		} finally {
			if (null != jedis) {
				jedis.close();
			}
		}
	}

	/**
	 * 分布式锁，单次锁，用于执行定时任务
	 * 
	 * @param lockKey
	 * @param lockMilliseconds
	 */
	public boolean lockOnce(String lockKey, long lockMilliseconds) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			String lockValue;
			long lock = 0, timestamp;
			if (lock != 1) {
				timestamp = System.currentTimeMillis();
				lockValue = String.valueOf(timestamp + lockMilliseconds);
				lock = jedis.setnx(lockKey, lockValue);
				if (lock == 1 || (timestamp > Long
						.parseLong(Objects.toString(jedis.get(lockKey), "-1").replaceAll("\"", ""))
						&& timestamp > Long.parseLong(
								Objects.toString(jedis.getSet(lockKey, lockValue), "-1").replaceAll("\"", "")))) {
					return true;
				}
			}
			return false;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 分布式锁，循环锁，用于频繁调用的方法体中，考虑配合unLock使用
	 * 
	 * @param lockKey
	 * @param lockMilliseconds
	 */
	public void lock(String lockKey, long lockMilliseconds) {
		ShardedJedis jedis = null;
		try {
			jedis = pool.getClient();
			String lockValue;
			long lock = 0, timestamp;
			while (lock != 1) {
				timestamp = System.currentTimeMillis();
				lockValue = String.valueOf(timestamp + lockMilliseconds);
				lock = jedis.setnx(lockKey, lockValue);
				if (lock == 1 || (timestamp > Long
						.parseLong(Objects.toString(jedis.get(lockKey), "-1").replaceAll("\"", ""))
						&& timestamp > Long.parseLong(
								Objects.toString(jedis.getSet(lockKey, lockValue), "-1").replaceAll("\"", "")))) {
					break;
				}
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
				}
			}
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	/**
	 * 分布式锁主动释放
	 * 
	 * @param lockKey
	 */
	public void unLock(String lockKey) {
		remove(lockKey);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.spring = applicationContext;
	}

	public byte[] rawKey(String key) {
		return SafeEncoder.encode(key);
	}

	public byte[][] rawKeys(String... key) {
		return SafeEncoder.encodeMany(key);
	}

	public <T> byte[] rawValue(T value) throws SerializationException {
		return redisSerializer.serialize(value);
	}

	public <T> byte[][] rawValue(T... value) throws SerializationException {
		List<byte[]> list = Lists.newArrayList();
		for (T t : value) {
			list.add(redisSerializer.serialize(t));
		}
		return list.toArray(new byte[0][0]);
	}

	public String readKey(byte[] b) {
		return SafeEncoder.encode(b);
	}

	public <T> T readValue(byte[] b, Class<T> clazz) throws SerializationException {
		return redisSerializer.deserialize(b, clazz);
	}

}
