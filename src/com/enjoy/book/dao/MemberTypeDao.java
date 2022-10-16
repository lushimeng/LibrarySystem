package com.enjoy.book.dao;

import com.enjoy.book.bean.Member;
import com.enjoy.book.bean.MemberType;
import com.enjoy.book.bean.Record;
import com.enjoy.book.util.DBHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author Mr.Lu
 * @Date 2022/10/2 11:15
 * @ClassName MemberTypeDao
 * @Version 1.0
 */
public class MemberTypeDao {
    // 创建QueryRunner对象
    QueryRunner runner = new QueryRunner();

    /**
     * 得到会员的全部类型
     */
    public List<MemberType> getAll() throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from membertype;";
        List<MemberType> memberTypes = runner.query(conn, sql, new BeanListHandler<MemberType>(MemberType.class));
        DBHelper.close(conn);
        return memberTypes;
    }

    /**
     * 根据 会员id查询对象
     */
    public MemberType getById(int id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from membertype where id = ?";
        MemberType memberType = runner.query(conn, sql, new BeanHandler<>(MemberType.class), id);
        DBHelper.close(conn);
        return memberType;
    }

}
