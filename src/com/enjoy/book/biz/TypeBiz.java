package com.enjoy.book.biz;

import com.enjoy.book.bean.Book;
import com.enjoy.book.bean.Type;
import com.enjoy.book.dao.BookDao;
import com.enjoy.book.dao.TypeDao;
import com.enjoy.book.util.DBHelper;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author Mr.Lu
 * @Date 2022/9/21 20:17
 * @ClassName TypeBiz
 * @Version 1.0
 */
// 注意： BIz层的异常只能用try—catch， 不能再交给上一次进行抛出了
public class TypeBiz {
    private TypeDao typeDao = new TypeDao();

    /**
     * 添加图书类型
     * @param name
     * @param parentId
     * @return
     */
    public int add(String name, long parentId){
        int count = 0;
        try {
            count = typeDao.add(name, parentId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;

    }

    /**
     * 查询图书的所有类型
     * @return
     * @throws SQLException
     */
    public List<Type> getAll() {
        List<Type> types = null;
        try {
            types = typeDao.getAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return types;
    }

    /**
     * 根据类型编号获取类型对象
     * @param typeId
     * @return
     */
    public Type getById(long typeId){
       Type type = null;
        try {
            type = typeDao.getById(typeId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return type;
    }

    /**
     * 修改图书类型
     * @param id 需要修改的类型编号
     * @param name
     * @param parentId
     * @return
     */
    public int modify(long id, String name, long parentId){
       int count = 0;
        try {
            count = typeDao.modify(id, name, parentId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }

    /**
     * 根据id删除图书类型
     * @param id
     * @return
     */
    public int remove(long id) throws Exception{
        // 如果有子项， 是不能删除
        BookDao bookDao = new BookDao();
       int count = 0;
        try {
            List<Book> books = bookDao.getBooksByTypeId(id);
            if(books.size() > 0){
                // 不能删除
                throw new Exception("删除的类型有子信息，删除失败");
            }
            count = typeDao.remove(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return count;
    }
}
