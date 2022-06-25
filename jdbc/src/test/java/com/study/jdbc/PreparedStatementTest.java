package com.study.jdbc;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import com.study.jdbc.domain.User;
import com.study.jdbc.domain.UserPhoto;
import com.study.jdbc.util.JDBCUtil;

/**
 * PreparedStatement 操作 SQL 测试用例
 */
public class PreparedStatementTest {
    private Connection connection;
    private PreparedStatement ps;
    private ResultSet rs;

    @Test
    public void testAdd() throws SQLException, ClassNotFoundException {
        String sql = "insert into user(name, password) value (?, ?)";
        try {
            connection = JDBCUtil.connection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, "t1");
            ps.setString(2, "password");
            ps.execute();
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    @Test
    public void testQuery() throws ClassNotFoundException, SQLException {
        String sql = "select id, name, password from user";
        try {

            connection = JDBCUtil.connection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));

                System.out.println(user);
            }
        } finally {
            JDBCUtil.close(connection, ps, rs);
        }
    }

    @Test
    public void testUpdate() throws ClassNotFoundException, SQLException {
        String sql = "update user set password = ? where name = ?";
        try {
            connection = JDBCUtil.connection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, "password1");
            ps.setString(2, "t1");
            ps.execute();
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    @Test
    public void testDelete() throws ClassNotFoundException, SQLException {
        String sql = "delete from user where name = ?";
        try {
            connection = JDBCUtil.connection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, "t1");
            ps.execute();
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    @Test
    public void testAddBlob() throws ClassNotFoundException, SQLException {
        String sql = "insert into user_photo(name, photo) value (?, ?)";
        try {
            connection = JDBCUtil.connection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, "t1");

            // 获取 photo 的 InputStream
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("me.jpg");
            // 可以传 Blob 类型也可以传 InputStream
            ps.setBlob(2, inputStream);
            ps.execute();
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    @Test
    public void testDownloadblob() throws ClassNotFoundException, SQLException, IOException {
        String sql = "select id, name, photo from user_photo";
        InputStream is = null;
        FileOutputStream fos = null;
        try {

            connection = JDBCUtil.connection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                UserPhoto user = new UserPhoto();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPhoto(rs.getBytes("photo"));
                System.out.println(user);

                // 下载
                is = new ByteArrayInputStream(user.getPhoto());
                // 下载到项目根目录
                fos = new FileOutputStream("download.jpg");
                // 下载到 resources 目录
                // fos = new FileOutputStream(ClassLoader.getSystemResource("").getPath() +
                // "download.jpg");
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    fos.write(bytes, 0, len);
                }
            }
        } finally {
            JDBCUtil.close(connection, ps, rs);
            if (is != null)
                is.close();
            if (fos != null)
                fos.close();
        }
    }

    @Test
    public void testBatchAdd() throws ClassNotFoundException, SQLException {
        String sql = "insert into user(name, password) value (?, ?)";
        try {
            long start = System.currentTimeMillis();
            connection = JDBCUtil.connection();
            ps = connection.prepareStatement(sql);
            // 关闭自动提交，减少提示事物耗时
            connection.setAutoCommit(false);
            for (int i = 0; i < 2000; i++) {
                ps.setString(1, "t" + i);
                ps.setString(2, "password" + i);
                ps.addBatch();
                if (((i + 1) % 500) == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            connection.commit();
            long end = System.currentTimeMillis();
            System.out.println("批量插入耗时：" + (end - start));
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    @Test
    public void testTransaction() throws SQLException, ClassNotFoundException {
        String sql = "insert into user(name, password) value (?, ?)";
        try {
            connection = JDBCUtil.connection();
            connection.setAutoCommit(false); // 手动提交
            ps = connection.prepareStatement(sql);
            ps.setString(1, "t1");
            ps.setString(2, "password");
            ps.execute();
            // 模拟异常
            System.out.println(10 / 0);
            ps.setString(1, "t2");
            ps.setString(2, "password");
            ps.execute();
            connection.commit(); // 提交事物
        } catch (Exception e) {
            connection.rollback(); // 回滚事物
            e.printStackTrace();
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    /**
     * 重现脏读（先执行 testTransactionIsolation1Update 再执行 testTransactionIsolation1 ）
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void testTransactionIsolation1() throws SQLException, ClassNotFoundException {
        String sql = "select id, name, password from user";
        try {
            connection = JDBCUtil.connection();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            // 使用 TRANSACTION_READ_COMMITTED 解决脏读问题
            // connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            System.out.println(connection.getTransactionIsolation());
            // connection.setAutoCommit(false); // 手动提交
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));

                System.out.println(user);
            }
            // connection.commit();
        } catch (Exception e) {
            // connection.rollback(); // 回滚事物
            e.printStackTrace();
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    /**
     * 重现脏读（先执行 testTransactionIsolation1Update 再执行 testTransactionIsolation1 ）
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    @Test
    public void testTransactionIsolation1Update() throws SQLException, ClassNotFoundException, InterruptedException {
        String sql = "update user set password = ? where name = ?";
        try {
            connection = JDBCUtil.connection();
            connection.setAutoCommit(false);
            ps = connection.prepareStatement(sql);
            ps.setString(1, "password1");
            ps.setString(2, "tx");
            ps.execute();
            Thread.sleep(15000);
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    /**
     * 重现不可重复度（先执行 testTransactionIsolation2 再执行 testTransactionIsolation2Update）
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void testTransactionIsolation2() throws SQLException, ClassNotFoundException {
        String sql = "select id, name, password from user";
        try {
            connection = JDBCUtil.connection();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // 使用 TRANSACTION_REPEATABLE_READ 解决不可重复读
            // connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            System.out.println(connection.getTransactionIsolation());
            connection.setAutoCommit(false); // 手动提交
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));

                System.out.println(user);
            }

            Thread.sleep(5000);

            rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));

                System.out.println(user);
            }

            // connection.commit();
        } catch (Exception e) {
            connection.rollback(); // 回滚事物
            e.printStackTrace();
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    /**
     * 重现不可重复读（先执行 testTransactionIsolation2 再执行 testTransactionIsolation2Update）
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void testTransactionIsolation2Update() throws SQLException, ClassNotFoundException {
        String sql = "update user set password = ? where name = ?";
        try {
            connection = JDBCUtil.connection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, "password1");
            ps.setString(2, "tx");
            ps.execute();
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    /**
     * 重现幻读（先执行 testTransactionIsolation3 再执行 testTransactionIsolation3Add）
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void testTransactionIsolation3() throws SQLException, ClassNotFoundException {
        String sql = "select id, name, password from user";
        try {
            connection = JDBCUtil.connection();
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // 使用 TRANSACTION_SERIALIZABLE 解决幻读
            // connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            System.out.println(connection.getTransactionIsolation());
            connection.setAutoCommit(false); // 手动提交
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));

                System.out.println(user);
            }

            Thread.sleep(5000);

            rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));

                System.out.println(user);
            }

            // connection.commit();
        } catch (Exception e) {
            connection.rollback(); // 回滚事物
            e.printStackTrace();
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }

    /**
     * 重现幻读（先执行 testTransactionIsolation3 再执行 testTransactionIsolation3Add）
     * 
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Test
    public void testTransactionIsolation3Add() throws SQLException, ClassNotFoundException {
        String sql = "insert into user(name, password) value (?, ?)";
        try {
            connection = JDBCUtil.connection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, "tx1");
            ps.setString(2, "password");
            ps.execute();
        } finally {
            JDBCUtil.close(connection, ps);
        }
    }
}
