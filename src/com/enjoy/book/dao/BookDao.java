package com.enjoy.book.dao;

import com.enjoy.book.bean.Book;
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
 * @Date 2022/9/21 20:47
 * @ClassName BookDao
 * @Version 1.0
 */
public class BookDao {
    // 创建QueryRunner对象(JDBC->DBUtils)
    QueryRunner runner = new QueryRunner();

    public List<Book> getBooksByTypeId(long typeId) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from book where typeId = ?";
        List<Book> books = runner.query(conn, sql, new BeanListHandler<Book>(Book.class), typeId);  // 返回一个集合是： new BeanListHandler<>()
        DBHelper.close(conn);  // 打开一个数据库连接一定要记得关闭，不然会造成资源的浪费
        return books;
    }

    /**
     * 添加图书信息
     * @return
     * @throws SQLException
     */
    public int add(long typeId, String name, double price, String desc, String pic, String publish, String author, long stock, String address) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "insert into book(typeId, `name`, price, `desc`, pic, publish, author, stock, address) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int count = runner.update(conn, sql, typeId, name, price, desc, pic, publish, author, stock, address);
        DBHelper.close(conn);
        return count;
    }


    public int modify(long id, long typeId, String name, double price, String desc, String pic, String publish, String author, long stock, String address) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "update book set typeId = ?, `name` = ?, price = ?, `desc` = ?, pic = ?, publish = ?, author = ?, stock = ?, address = ? where id = ?";
        int count = runner.update(conn, sql, typeId, name, price, desc, pic, publish, author, stock, address, id);
        DBHelper.close(conn);
        return count;
    }


    /**
     * 更新书本数量
     * @param id
     * @param amount : +1表示书本个数+1， -1: 表示书本个数-1
     * @return
     * @throws SQLException
     */
    public int modify(long id, long amount) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "update book set stock = stock + ? where id = ?";
        int count = runner.update(conn, sql, amount , id);
        DBHelper.close(conn);
        return count;
    }

    public int remove(long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "delete from book where id = ?";
        int count = runner.update(conn, sql, id);
        DBHelper.close(conn);
        return count;
    }

    public Book getById(long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from book where id = ?";
        Book book = runner.query(conn, sql, new BeanHandler<Book>(Book.class), id);
        DBHelper.close(conn);
        return book;
    }

    public Book getByName(String bookName) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from book where name = ?";
        Book book = runner.query(conn, sql, new BeanHandler<>(Book.class), bookName);
        DBHelper.close(conn);
        return book;
    }


    /**
     * 页面查询
     * @param pageIndex 第几页， 从1开始
     * @param pageSize  每一页多少行
     * @return  当前页的信息
     * @throws SQLException
     */
    public List<Book> getByPage(int pageIndex, int pageSize) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from book limit ?, ?";
        int offset = (pageIndex - 1) * pageSize;
        List<Book> books = runner.query(conn, sql, new BeanListHandler<Book>(Book.class), offset, pageSize);
        DBHelper.close(conn);
        return books;
    }

    public int getCount() throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select count(*) from book";
        Object data = runner.query(conn, sql, new ScalarHandler<>());
        int count = (int)((long)data);
        DBHelper.close(conn);
        return count;
    }

    public static void main(String[] args) {
        BookDao bookDao = new BookDao();
        try {
            bookDao.modify(2, -100);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}


























