package com.enjoy.book.dao;

import com.enjoy.book.bean.Type;
import com.enjoy.book.util.DBHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author Mr.Lu
 * @Date 2022/9/21 19:46
 * @ClassName TypeDao
 * @Version 1.0
 */
// 注意，Dao层可以把异常进行抛出，交给上一层Biz层进行处理
public class TypeDao {
    // 创建QueryRunner对象
    QueryRunner runner = new QueryRunner();

    /**
     * 添加图书类型
     * @param name
     * @param parentId
     * @return
     */
    public int add(String name, long parentId) throws SQLException {
        // 1. 调用DBHelper获取连接对象
        Connection conn = DBHelper.getConnection();
        // 2. 准备执行的sql语句
        String sql = "insert into type values(null, ?, ?)";
        // 3. 调用update方法对图书类型进行添加
        int count = runner.update(conn, sql, name, parentId);
        // 4. 关闭连接对象
        DBHelper.close(conn);
        // 5. 返回count
        return count;
    }

    /**
     * 查询图书的所有类型
     * @return
     * @throws SQLException
     */
    public List<Type> getAll() throws SQLException {
        // 1. 调用DBHelper获取连接对象
        Connection conn = DBHelper.getConnection();
        // 2. 准备执行的sql语句
        String sql = "select id, name, parentId from type";
        // 3. 调用query根据id对图书类型进行查找
        List<Type> types = runner.query(conn, sql,new BeanListHandler<Type>(Type.class));
        // 4. 关闭连接对象
        DBHelper.close(conn);
        // 5. 返回count
        return types;
    }

    /**
     * 根据类型编号获取类型对象
     * @param typeId
     * @return
     */
    public Type getById(long typeId) throws SQLException {
        // 1. 调用DBHelper获取连接对象
        Connection conn = DBHelper.getConnection();
        // 2. 准备执行的sql语句
        String sql = "select id, name, parentId from type where id = ?";
        // 3. 调用query根据id对图书类型进行查找
        Type type = runner.query(conn, sql, new BeanHandler<Type>(Type.class), typeId);
        // 4. 关闭连接对象
        DBHelper.close(conn);
        // 5. 返回count
        return type;
    }

    /**
     * 修改图书类型
     * @param id 需要修改的类型编号
     * @param name
     * @param parentId
     * @return
     */
    public int modify(long id, String name, long parentId) throws SQLException {
        // 1. 调用DBHelper获取连接对象
        Connection conn = DBHelper.getConnection();
        // 2. 准备执行的sql语句
        String sql = "update type set name = ?, parentId = ? where id = ?";
        // 3. 调用update方法对图书类型进行添加
        int count = runner.update(conn, sql, name, parentId, id);
        // 4. 关闭连接对象
        DBHelper.close(conn);
        // 5. 返回count
        return count;
    }

    /**
     * 根据id删除图书类型
     * @param id
     * @return
     */
    public int remove(long id) throws SQLException {
        // 1. 调用DBHelper获取连接对象
        Connection conn = DBHelper.getConnection();
        // 2. 准备执行的sql语句
        String sql = "delete from type where id=?";
        // 3. 调用update方法对图书类型进行添加
        int count = runner.update(conn, sql, id);
        // 4. 关闭连接对象
        DBHelper.close(conn);
        // 5. 返回count
        return count;
    }
}
