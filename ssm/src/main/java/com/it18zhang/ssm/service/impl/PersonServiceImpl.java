package com.it18zhang.ssm.service.impl;

import com.it18zhang.ssm.dao.BaseDao;
import com.it18zhang.ssm.dao.impl.PersonDaoImpl;
import com.it18zhang.ssm.domain.Item;
import com.it18zhang.ssm.domain.Order;
import com.it18zhang.ssm.domain.Person;
import com.it18zhang.ssm.domain.User;
import com.it18zhang.ssm.service.PersonService;
import com.it18zhang.ssm.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 */
@Service("personService")
public class PersonServiceImpl extends BaseServiceImpl<Person> implements PersonService {

    @Resource(name = "personDao")
    public void setDao(BaseDao<Person> dao) {
        super.setDao(dao);
    }

    public String selectNameByPhone(String phone) {
        return ((PersonDaoImpl) getDao()).selectNameByPhone(phone);
    }
}