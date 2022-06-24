package com.study.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import com.study.jdbc.util.JDBCUtil;

/**
 * Statement操作 SQL 测试用例
 */
public class StatementTest {
    public void query(String name) throws ClassNotFoundException, SQLException {
        Connection conn = JDBCUtil.connection();
        // sql 拼接
        String sql = "select * from user where name = '" + name + "'";
        // 执行SQL
        Statement state = conn.createStatement();
        ResultSet rs = state.executeQuery(sql);

        // 记录条数
        int i = 0;
        while (rs.next()) {
            i++;
        }
        System.out.println(i);
        JDBCUtil.close(conn, state, rs);
    }

    @Test
    public void testQuery1() throws ClassNotFoundException, SQLException {
        String name = "test";
        query(name);
    }

    /**
     * SQL 注入测试
     * 
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Test
    public void testQuery2() throws ClassNotFoundException, SQLException {
        String name = "test' or 1=1 or 1='1";
        query(name);
    }
}
