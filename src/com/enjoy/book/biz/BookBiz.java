package com.enjoy.book.biz;

import com.enjoy.book.bean.Book;
import com.enjoy.book.bean.Record;
import com.enjoy.book.bean.Type;
import com.enjoy.book.dao.BookDao;
import com.enjoy.book.dao.TypeDao;
import com.enjoy.book.util.DBHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author Mr.Lu
 * @Date 2022/9/27 15:07
 * @ClassName BookBiz
 * @Version 1.0
 */
public class BookBiz {
    private BookDao bookDao = new BookDao();
    TypeDao typeDao = new TypeDao();

    public List<Book> getBooksByTypeId(long typeId){
        List<Book> books = null;
        try {
            books = bookDao.getBooksByTypeId(typeId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return books;
    }


    public int add(long typeId, String name, double price, String desc, String pic, String publish, String author, long stock, String address){
        int count = 0;
        try {
            count = bookDao.add(typeId, name, price, desc, pic, publish, author, stock, address);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }

    // 重载原因，参数较多，直接封装成一个对象即可
    public int add(Book book){
        return this.add(book.getTypeId(), book.getName(), book.getPrice(), book.getDesc(), book.getPic(), book.getPublish(), book.getAuthor(), book.getStock(), book.getAddress());
    }


    public int modify(long id, long typeId, String name, double price, String desc, String pic, String publish, String author, long stock, String address){
        int count = 0;
        try {
            count = bookDao.modify(id, typeId, name, price, desc, pic, publish, author, stock, address);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }

    // 重载原因，参数较多，直接封装成一个对象即可
    public  int modify(Book book){
        return this.modify(book.getId(), book.getTypeId(), book.getName(), book.getPrice(), book.getDesc(), book.getPic(), book.getPublish(), book.getAuthor(), book.getStock(), book.getAddress());
    }

    /**
     * 更新书本数量
     * @param id
     * @param amount : +1表示书本个数+1， -1: 表示书本个数-1
     * @return
     * @throws SQLException
     */
    public int modify(long id, long amount) {
       int count = 0;
        try {
            count = bookDao.modify(id, amount);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return count;
    }


    public int remove(long id){
        RecordBiz recordBiz = new RecordBiz();
        int count = 0;
        try {
            //  1. 判断id是否存在外键
            List<Record> records = recordBiz.getRecordByBookId(id);
            if(records.size() > 0){
                throw new Exception("删除的书籍有子信息， 删除失败");
            }
            // 2. 删除
            count = bookDao.remove(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }


    // 这里要特别注意，里面包含一个外键Type，所以要特殊处理
    public Book getById(long id){
        Book book = null;
        try {
            book = bookDao.getById(id);
            if(book == null) return null;  // 如果book == null 在调用getTypeId()的时候会报错
            long typeId = book.getTypeId();  // 得到外键的id信息
            Type type = typeDao.getById(typeId);  // 根据外键信息获取对应的type对象
            book.setType(type);  // 对book对象中的type属性进行设置
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return book;
    }


    // 这里要特别注意，里面包含一个外键Type，所以要特殊处理
    public Book getByName(String bookName){
        try {
            Book book = bookDao.getByName(bookName);
            if(book == null) return null;
            long typeId = book.getTypeId();  // 得到外键的id信息
            Type type = typeDao.getById(typeId);  // 根据外键信息获取对应的type对象
            book.setType(type);  // 对book对象中的type属性进行设置
            return book;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }


    // 获取当前页的信息
    public List<Book> getByPage(int pageIndex, int pageSize){
        List<Book> books = null;
        try {
            books = bookDao.getByPage(pageIndex, pageSize);
            if(books.size() == 0) return null;
            // 处理type对象的数据问题
            for(Book book : books){
                long typeId = book.getTypeId();
                Type type = typeDao.getById(typeId);  // 根据typeId找到对应的type对象
                book.setType(type);  // 设置给book.setType()
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return books;
    }


    /**
     * 根据行数计算页数
     * @return
     */
    public int getPageCount(int pageSize){
        int pageCount = 0;
        try {
            // 1. 获取行数
            int rowCount = bookDao.getCount();
            // 2. 根据行数得到页数， 每一页多少条
            pageCount = (rowCount - 1) / pageSize + 1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pageCount;
    }

    public static void main(String[] args) {
        BookBiz bookBiz = new BookBiz();
        Book book = bookBiz.getByName("234234");
        System.out.println(book);
    }

}
