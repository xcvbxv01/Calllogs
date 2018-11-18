package com.it18zhang.ssm.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.it18zhang.ssm.domain.Person;
import com.it18zhang.ssm.service.PersonService;
import com.it18zhang.ssm.service.impl.PersonServiceImpl;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

public class TestPerson {

    //电话簿
    public static Map<String, String> callers = new HashMap<String, String>();

    static{
        callers.put("15811111111", "史让");
        callers.put("18022222222", "赵嗄");
        callers.put("15133333333", "张锕 ");
        callers.put("13269364444", "王以");
        callers.put("15032295555", "张噢");
        callers.put("17731086666", "张类");
        callers.put("15338597777", "李平");
        callers.put("15733218888", "杜跑");
        callers.put("15614209999", "任阳");
        callers.put("15778421111", "梁鹏");
        callers.put("18641241111", "郭彤");
        callers.put("15732641111", "刘飞");
        callers.put("13341101111", "段星");
        callers.put("13560191111", "唐华");
        callers.put("18301581111", "杨谋");
        callers.put("13520401111", "温英");
        callers.put("18332561111", "朱宽");
        callers.put("18620191111", "刘宗");
    }

    @Test
    public void testInsertPerson()
    {
        ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
        PersonService ps = (PersonService)ac.getBean("personService");
        Set<String> keySet = callers.keySet();
        for(String key : keySet)
        {
            String num = key;
            String name = callers.get(key);
            Person p = new Person();
            p.setName(name);
            p.setPhone(num);
            ps.insert(p);
        }
    }

    @Test
    public void testFindAllPerson()
    {
        ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
        PersonService ps = (PersonService)ac.getBean("personService");

        List<Person> list = ps.selectAll();

        String s = JSONArray.toJSONString(list);
        System.out.println(s);
    }

    @Test
    public void testFindPersonNameByPhone()
    {
        ApplicationContext ac = new ClassPathXmlApplicationContext("beans.xml");
        PersonService ps = (PersonService)ac.getBean("personService");

        String name = ps.selectNameByPhone("15338597777");

       System.out.println(name);
    }
}
