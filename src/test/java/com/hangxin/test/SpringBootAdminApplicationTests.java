package com.hangxin.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.hangxin.persistence.service.PeopleService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
/**
 * @description 1. package as far as possible to com.qiyou, if this maby need springBootTest(class = SpringBootAdminApplication.class)
 *              2. if @Before & @After add @Test, method init() and after() will run three times
 *              3. no @Test method, jenkins can auto run sonClass @Test method
 * @author Lenovo
 *
 */
public class SpringBootAdminApplicationTests {

    @Before
    public void init() {
        System.out.println("开始测试-----------------");
    }

    @After
    public void after() {
        System.out.println("测试结束-----------------");
    }

    @Autowired
    private PeopleService peopleService;

    @Test
    public void getPeopleInfo() {
        /*
         * try { Result pResult = peopleService.getPeopleInfoFromCache();
         * System.out.println(JsonHelper.parseToJson(pResult)); } catch
         * (Exception e) { e.printStackTrace(); }
         */
        System.out.println("-----------------由于虚拟机未安装相应数据库,这里仅输出代表数据返回-----------------");
    }

}
