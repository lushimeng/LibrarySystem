package com.enjoy.book.dao;

import com.enjoy.book.bean.User;
import com.enjoy.book.util.DBHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author Mr.Lu
 * @Date 2022/9/9 8:47
 * @ClassName UserDao
 * @Version 1.0
 */
public class UserDao {
    // 创建QueryRunner对象(JDBC->DBUtils)
    QueryRunner runner = new QueryRunner();

    /**
     * 查找用户
     * @param name
     * @param pwd
     * @return
     * @throws SQLException
     */
    public User getUser(String name, String pwd) throws SQLException {
        // 1. 调用DBHelper获取连接对象
        Connection conn = DBHelper.getConnection();
        // 2. 准备执行sql语句
        String sql = "select * from user where name = ? and pwd = ? and state = '1'";
        // 3. 调用查询方法，将查询的数据封装成User对象
        User user = runner.query(conn, sql, new BeanHandler<User>(User.class), name, pwd);
        // 4. 关闭连接对象
        DBHelper.close(conn);
        // 5. 返回user
        
        return user;
    }

    /**
     * 修改密码
     * @param id  需要修改密码的用户编号
     * @param pwd  新的密码
     * @return  修改的数据行
     */
    public  int modifyPwd(long id, String pwd) throws SQLException {

        // 1. 调用DBHelper获取连接对象
        Connection conn = DBHelper.getConnection();
        // 2. 准备执行sql语句
        String sql = "update user set pwd = ? where id = ?";
        // 3. 调用更新方法，将id的新的密码放入到数据库中, 返回影响的行号
        int count = runner.update(conn, sql, pwd, id);
        // 4. 关闭连接对象
        DBHelper.close(conn);
        // 5. 返回count
        return count;
    }

    /**
     * 根据id查找管理员信息
     * @param id
     * @return
     */
    public User getById(long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from user where id = ?";
        User user = runner.query(conn, sql, new BeanHandler<User>(User.class), id);
        DBHelper.close(conn);
        return user;
    }
}
