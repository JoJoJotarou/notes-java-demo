package com.study.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCUtil {

    private static String password;
    private static String username;
    private static String url;
    private static String driverClass;

    static {
        try {
            getProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取配置文件 jdbc.properties
     */
    private static void getProperties() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = ClassLoader.getSystemResourceAsStream("jdbc.properties");
            properties.load(inputStream);
            driverClass = properties.getProperty("driverClass");
            url = properties.getProperty("url");
            username = properties.getProperty("user");
            password = properties.getProperty("password");
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    /**
     * 获取数据库连接
     * 
     * <p>
     * {@code com.mysql.cj.jdbc.Driver} 的静态代码中已经有注册驱动类的代码，静态代码在类加载的时候执行，
     * 故 {@code Class.forName(DRIVER)} 方法可以创建驱动类的实例，并将其加载到内存中
     * 
     * @throws ClassNotFoundException
     * @throws SQLException
     * @see com.mysql.cj.jdbc.Driver
     */
    public static Connection connection() throws ClassNotFoundException, SQLException {
        // 加载 MySQL 驱动类。
        Class.forName(driverClass);
        // 获取数据库连接。
        return DriverManager.getConnection(url, username, password);
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
