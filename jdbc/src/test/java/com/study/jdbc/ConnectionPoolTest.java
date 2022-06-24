package com.study.jdbc;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 连接池 C3P0、DBCP、Druid 测试用例
 */
public class ConnectionPoolTest {

    /**
     * c3p0 硬编码创建连接池
     * 
     * @throws PropertyVetoException
     * @throws SQLException
     */
    @Test
    public void testC3P0() throws PropertyVetoException, SQLException {

        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.cj.jdbc.Driver"); // loads the jdbc driver
        cpds.setJdbcUrl("jdbc:mysql:///test?rewriteBatchedStatements=true");
        cpds.setUser("test");
        cpds.setPassword("test");

        // the settings below are optional -- c3p0 can work with defaults
        cpds.setMinPoolSize(5);
        cpds.setAcquireIncrement(5);
        cpds.setMaxPoolSize(20);

        Connection connection = cpds.getConnection();
        System.out.println(connection);
        connection.close();
        cpds.close();
    }

    /**
     * 配置文件 c3p0-config.xml
     * 
     * @throws PropertyVetoException
     * @throws SQLException
     */
    @Test
    public void testC3P0WithConfig() throws PropertyVetoException, SQLException {
        ComboPooledDataSource cpds = new ComboPooledDataSource("intergalactoApp");
        Connection connection = cpds.getConnection();
        System.out.println(connection);
        connection.close();
        cpds.close();
    }

    /**
     * DBCP 硬编码创建连接池
     * 
     * @throws SQLException
     */
    @Test
    public void testDBCP() throws SQLException {
        // Construct BasicDataSource
        BasicDataSource bds = new BasicDataSource();
        bds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        bds.setUrl("jdbc:mysql:///test?rewriteBatchedStatements=true");
        bds.setUsername("test");
        bds.setPassword("test");

        Connection connection = bds.getConnection();
        System.out.println(connection);
        connection.close();
        bds.close();
    }

    /**
     * DBCP 配置文件创建连接池 dbcp.properties
     * 
     * @throws Exception
     */
    @Test
    public void testDBCPWithConfig() throws Exception {
        // Construct BasicDataSource
        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("dbcp.properties"));
        BasicDataSource bds = BasicDataSourceFactory.createDataSource(properties);

        Connection connection = bds.getConnection();
        System.out.println(connection);
        connection.close();
        bds.close();
    }

    /**
     * Druid 硬编码连接
     * 
     * @throws SQLException
     */
    @Test
    public void testDruid() throws SQLException {
        DruidDataSource dds = new DruidDataSource();
        dds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dds.setUrl("jdbc:mysql:///test?rewriteBatchedStatements=true");
        dds.setUsername("test");
        dds.setPassword("test");

        DruidPooledConnection dpc = dds.getConnection();
        Connection connection = dpc.getConnection();
        System.out.println(connection);
        connection.close();
        dpc.close();
        dds.close();
    }

    /**
     * Druid 配置文件连接
     * 
     * @throws Exception
     */
    @Test
    public void testDruidWithConfig() throws Exception {

        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("druid.properties"));
        DruidDataSource dds = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        DruidPooledConnection dpc = dds.getConnection();
        Connection connection = dpc.getConnection();
        System.out.println(connection);
        connection.close();
        dpc.close();
        dds.close();
    }
}
