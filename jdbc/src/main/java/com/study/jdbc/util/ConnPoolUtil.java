package com.study.jdbc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

public class ConnPoolUtil {

    private static DruidDataSource druidDataSource;

    private static void createPool() throws Exception {
        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("druid.properties"));
        ConnPoolUtil.druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
    }

    public static Connection getConnection() throws Exception {
        if (ConnPoolUtil.druidDataSource == null) {
            createPool();
        }
        return ConnPoolUtil.druidDataSource.getConnection();
    }

    public static void showActiveCount() throws Exception {
        if (ConnPoolUtil.druidDataSource == null) {
            createPool();
        }
        System.out.println("connection: " + ConnPoolUtil.druidDataSource.getConnectCount());
        System.out.println("count: " + ConnPoolUtil.druidDataSource.getActiveCount());
        System.out.println("peak: " + ConnPoolUtil.druidDataSource.getActivePeak());
    }

    /**
     * 关闭流
     *
     * @param c
     * @param s
     * @throws SQLException
     */
    public static void close(Connection c, Statement s) throws SQLException {
        if (c != null)
            c.close();
        if (s != null)
            s.close();
    }

    /**
     * 关闭流
     *
     * @param c
     * @param s
     * @param rs
     * @throws SQLException
     */
    public static void close(Connection c, Statement s, ResultSet rs) throws SQLException {
        if (c != null)
            c.close();
        if (s != null)
            s.close();
        if (rs != null)
            rs.close();
    }

}
