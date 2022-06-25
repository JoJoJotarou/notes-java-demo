package com.study.jdbc.dao;

import java.util.List;

import org.junit.Test;

import com.study.jdbc.domain.User;

public class UserDAOTest extends GenericDAO<User> {
    @Test
    public void testAdd() throws Exception {
        String sql = "insert into user(name, password) value (?, ?)";
        execute(sql, "t1", "password");
    }

    @Test
    public void testQuery() throws Exception {
        String sql = "select id, name, password from user";
        List<User> list = executeQuery(sql);
        for (User user : list) {
            System.out.println(user);
        }
    }

    @Test
    public void testUpdate() throws Exception {
        String sql = "update user set password = ? where name = ?";
        execute(sql, "password1", "t1");
    }

    @Test
    public void testDelete() throws Exception {
        String sql = "delete from user where name = ?";
        execute(sql, "t1");
    }

}
