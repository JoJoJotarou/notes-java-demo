package com.study.jdbc.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import com.study.jdbc.util.ConnPoolUtil;

public class GenericDAO<T> {

    Class<T> clazz = null;

    {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        clazz = (Class<T>) type.getActualTypeArguments()[0];
    }

    protected Connection connection = null;
    protected PreparedStatement ps = null;
    protected ResultSet rs = null;

    /**
     * 增删改
     * 
     * @param sql
     * @param params
     */
    public void execute(String sql, Object... params) throws Exception {
        try {
            ConnPoolUtil.showActiveCount();
            connection = ConnPoolUtil.getConnection();
            ConnPoolUtil.showActiveCount();
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.execute();
        } finally {
            ConnPoolUtil.close(connection, ps);
            ConnPoolUtil.showActiveCount();
        }
    }

    /**
     * 查询
     * 
     * @param sql
     * @param params
     * @return {@code List<T>}
     */
    public List<T> executeQuery(String sql, Object... params) throws Exception {

        List<T> tList = new ArrayList<>();

        try {
            connection = ConnPoolUtil.getConnection();
            ps = connection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();

            while (rs.next()) {
                // 若存在数据，创建对象实例
                T t = clazz.getConstructor().newInstance();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String fieldName = metaData.getColumnLabel(i + 1);
                    Field field = t.getClass().getDeclaredField(fieldName);
                    Method method = t.getClass().getDeclaredMethod(
                            "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), field.getType());
                    method.invoke(t, rs.getObject(fieldName));
                }
                tList.add(t);
            }

            return tList;
        } finally {
            ConnPoolUtil.close(connection, ps);
        }
    }
}
