package com.it18zhang.ssm.service;

import com.it18zhang.ssm.domain.Person;
import com.it18zhang.ssm.domain.User;

import java.util.List;

/**
 *
 */
public interface PersonService extends BaseService<Person> {
    public String selectNameByPhone(String phone);
}
