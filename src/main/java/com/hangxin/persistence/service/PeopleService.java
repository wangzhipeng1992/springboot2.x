package com.hangxin.persistence.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.hangxin.basic.Result;
import com.hangxin.common.redis.RedisClient;
import com.hangxin.persistence.mapper.PeopleMapper;
import com.hangxin.persistence.model.People;
import com.hangxin.util.JsonHelper;

@Service
public class PeopleService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private PeopleMapper peopleMapper;

    public static final String CACHE_NAME = "people";

    // @Cacheable、@CachePut、@CacheEvict 注释介绍
    // @Cacheable 的作用 主要针对方法配置，能够根据方法的请求参数对其结果进行缓存
    // @CachePut 的作用 主要针对方法配置，能够根据方法的请求参数对其结果进行缓存，和 @Cacheable
    // 不同的是，它每次都会触发真实方法的调用
    // @CachEvict 的作用 主要针对方法配置，能够根据一定的条件对缓存进行清空

    @Transactional(rollbackFor = Exception.class)
    @CachePut(value = CACHE_NAME, key = "'people_2'")
    public Result peopleInsert() throws Exception {
        Result result = new Result();
        People arg0 = new People();
        arg0.setName("徐慧娟");
        arg0.setSex(0);
        arg0.setAge(25);
        arg0.setHeight(168.3);
        arg0.setWeight(55.2);
        try {
            logger.debug("存储信息参数 peopleInfo={}", JsonHelper.parseToJson(arg0));
            int insert = peopleMapper.insert(arg0);
            if (insert > 0) {
                result.setResultData(arg0);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return Result.result("0101", "保存人员信息出现异常");
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Result peopleInsertXml(Map<String, Object> peopleInfo) throws Exception {
        Result result = new Result();
        try {
            int num = peopleMapper.peopleInsertXml(peopleInfo);
            if (num > 0) {
                result.setResultData(peopleInfo);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            logger.error("XML保存人员信息有误 e={}", e);
            return Result.result("0101", "保存人员信息出现异常");
        }
        return result;
    }

    public Result getPeopleInfo() throws Exception {
        Result result = new Result();
        if (redisClient.get("Redis_Key_ALL_PEOPLE_INFO_Key") != null) {
            List<People> allPeopleInfo = JSON.parseArray((String) redisClient.get("Redis_Key_ALL_PEOPLE_INFO_Key"),
                    People.class);
            result.setResultData(allPeopleInfo);
            return result;
        }
        List<People> allPeopleInfo = peopleMapper.selectAll();
        result.setResultData(allPeopleInfo);
        redisClient.set("Redis_Key_ALL_PEOPLE_INFO_Key", JsonHelper.parseToJson(allPeopleInfo), 1 * 60 * 60);
        return result;
    }

    @Cacheable(value = CACHE_NAME, key = "'people_2'")
    public Result getPeopleInfoFromCache() throws Exception{
        Result result = new Result();
        List<People> allPeopleInfo = peopleMapper.selectAll();
        result.setResultData(allPeopleInfo);
        return result;
     }
}
