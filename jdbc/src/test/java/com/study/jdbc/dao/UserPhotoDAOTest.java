package com.study.jdbc.dao;

import java.util.List;

import org.junit.Test;

import com.study.jdbc.domain.UserPhoto;

public class UserPhotoDAOTest extends GenericDAO<UserPhoto> {

    @Test
    public void testQuery() throws Exception {
        String sql = "select id, name, photo from user_photo";
        List<UserPhoto> list = executeQuery(sql);
        for (UserPhoto user : list) {
            System.out.println(user);
        }
    }

}
