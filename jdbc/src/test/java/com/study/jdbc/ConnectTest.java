package com.study.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Test;

/**
 * 数据库 Connection 创建测试用例
 */
public class ConnectTest {

    @Test
    public void getConnectionTest1() throws SQLException {
        // 注册驱动
        Driver driver = new com.mysql.cj.jdbc.Driver();

        // 连接参数
        String url = "jdbc:mysql://localhost/test";
        Properties info = new Properties();
        info.setProperty("user", "test");
        info.setProperty("password", "test");

        // 获取数据库连接
        Connection connection = driver.connect(url, info);
        System.out.println(connection);
    }

    /**
     * 通过反射获取驱动就可以加载不同数据库驱动
     * 
     * @throws Exception
     */
    @Test
    public void getConnectionTest2() throws Exception {
        // 注册驱动
        Driver driver = (Driver) Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();

        // 连接参数
        String url = "jdbc:mysql://localhost/test";
        Properties info = new Properties();
        info.setProperty("user", "test");
        info.setProperty("password", "test");

        // 获取数据库连接
        Connection connection = driver.connect(url, info);
        System.out.println(connection);
    }

    /**
     * 使用 DriverManager
     * 
     * @throws Exception
     */
    @Test
    public void getConnectionTest3() throws Exception {
        // 注册驱动
        Driver driver = (Driver) Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
        DriverManager.registerDriver(driver);

        // 连接参数
        String url = "jdbc:mysql://localhost/test";
        String user = "test";
        String password = "test";

        // 获取数据库连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }

    /**
     * 自动注册驱动
     *
     * - com.mysql.cj.jdbc.Driver 的静态代码块实现了 `DriverManager.registerDriver(driver)`
     * - 所以我们只需通过 Class.forName 加载类即可
     *
     *
     * @throws Exception
     */
    @Test
    public void getConnectionTest4() throws Exception {
        // 注册驱动
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 连接参数
        String url = "jdbc:mysql://localhost/test";
        String user = "test";
        String password = "test";

        // 获取数据库连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }

    /**
     * 配置文件（final）
     *
     * 优点：
     * - 数据与与代码解耦
     * - 修改数据集连接信息不需要重新打包
     *
     * @throws Exception
     */
    @Test
    public void getConnectionTest5() throws Exception {
        // 读取配置文件
        InputStream resourceAsStream = ClassLoader.getSystemResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(resourceAsStream);

        // 注册驱动
        Class.forName(properties.getProperty("driverClass"));

        // 连接参数
        String url = properties.getProperty("url");
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        // 获取数据库连接
        Connection connection = DriverManager.getConnection(url, user, password);
        System.out.println(connection);
    }
}
