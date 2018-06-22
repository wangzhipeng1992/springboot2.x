package com.hangxin.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.hangxin.basic.Result;
import com.hangxin.persistence.service.PeopleService;
import com.hangxin.util.JsonHelper;

/**
 * @description : 1. class can't make to abstract
 *                2. method only at here only for simple test
 * @author Lenovo
 *
 */
public class PeopleServiceTest extends SpringBootAdminApplicationTests {

    @Autowired
    private PeopleService peopleService;

    @Test
    public void getPeopleInfo() {
        /*try {
            Result pResult = peopleService.getPeopleInfoFromCache();
            System.out.println(JsonHelper.parseToJson(pResult));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        System.out.println("-----------------由于虚拟机未安装相应数据库,这里仅输出代表数据返回-----------------");
    }

}
