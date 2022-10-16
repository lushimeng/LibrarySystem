package com.enjoy.book.dao;

import com.enjoy.book.bean.Member;
import com.enjoy.book.bean.MemberType;
import com.enjoy.book.bean.Record;
import com.enjoy.book.util.DBHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author Mr.Lu
 * @Date 2022/10/2 11:15
 * @ClassName MemberDao
 * @Version 1.0
 */
public class MemberDao {
    QueryRunner runner = new QueryRunner();

    /**
     * 添加会员
     */
    public int add(String name,String pwd,long typeId,double balance,String tel,String idNumber) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "insert into member(`name`, pwd, typeId, balance, regdate, tel, idNumber) values(?, ?, ?, ?, CURRENT_DATE, ?, ?)";
        int count = runner.update(conn, sql, name, pwd, typeId, balance, tel, idNumber);
        DBHelper.close(conn);
        return count;
    }

    /**
     * 修改会员信息
     */
    public int modify(long id,String name,String pwd,long typeId,double balance,String tel,String idNumber) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "update member set `name` = ?, pwd = ?, typeId = ?, balance = ?, tel = ?, idNumber = ? where id = ?";
        int count = runner.update(conn, sql, name, pwd, typeId, balance, tel, idNumber, id);
        DBHelper.close(conn);
        return count;
    }

    /**
     * 根据id删除会员信息
     */
    public int remove(long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "delete from member where id = ?";
        int count = runner.update(conn, sql, id);
        DBHelper.close(conn);
        return count;
    }

    /**
     * 根据会员的身份证号码修改押金
     */
    public int modifyBalance(String idNumber,double amount) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "update member set balance = balance + ? where idNumber = ?";
        int count = runner.update(conn, sql, amount, idNumber);
        DBHelper.close(conn);
        return count;
    }

    /**
     * 会员id修改押金
     * @param id
     * @param amount >0: 归还+   <0:借书-押金
     */
    public int modifyBalance(long id,double amount) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "update member set balance = balance + ? where id = ?";
        int count = runner.update(conn, sql, amount, id);
        DBHelper.close(conn);
        return count;
    }

    /**
     * 会员列表
     */
    public List<Member> getAll() throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select id, `name`, pwd, typeId, balance, regdate, tel, idNumber from member";
        List<Member> members = runner.query(conn, sql, new BeanListHandler<Member>(Member.class));
        DBHelper.close(conn);
        return members;
    }

    /**
     * 根据会员编号查会员信息
     */
    public Member getById(long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select id, `name`, pwd, typeId, balance, regdate, tel, idNumber from member where id = ?";
        Member member = runner.query(conn, sql, new BeanHandler<Member>(Member.class), id);
        DBHelper.close(conn);
        return member;
    }

    /**
     * 根据会员身份证查会员信息
     */
    public Member getByIdNumber(String idNumber) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select id, `name`, pwd, typeId, balance, regdate, tel, idNumber from member where idNumber = ?";
        Member member = runner.query(conn, sql, new BeanHandler<Member>(Member.class), idNumber);
        DBHelper.close(conn);
        return  member;
    }


    /**
     * 判断会员编号是否存在Record中(作为外键 ）
     */
    public boolean exits(long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from record where memberId = ?;";
        List<Record> records = runner.query(conn, sql, new BeanListHandler<Record>(Record.class), id);
        DBHelper.close(conn);
        if(records.size() > 0){
            return true;
        }else {
            return false;
        }
    }

    public static void main(String[] args) throws SQLException {
        MemberTypeDao memberTypeDao = new MemberTypeDao();
        System.out.println(memberTypeDao.getAll());
    }

}
