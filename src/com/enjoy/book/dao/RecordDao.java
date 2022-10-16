package com.enjoy.book.dao;

import com.enjoy.book.bean.Record;
import com.enjoy.book.util.DBHelper;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Lu
 * @Date 2022/9/30 15:28
 * @ClassName RecordDao
 * @Version 1.0
 */
public class RecordDao {
    QueryRunner runner = new QueryRunner();

    /**
     * 根据recordId获取借阅对象
     */
    public Record getById(long recordId) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from record where id = ?";
        Record record = runner.query(conn, sql, new BeanHandler<>(Record.class), recordId);
        DBHelper.close(conn);
        return  record;
    }


    /**
     * 根据用户的会员编号查询用户借阅信息
     */
    public List<Record> getRecordsByMemberId(long memberId) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from record where memberId = ? and backDate is null";  // backDate为null表示图书还没有归还，不为null表示图书已经归还
        List<Record> records = runner.query(conn, sql, new BeanListHandler<Record>(Record.class), memberId);
        DBHelper.close(conn);
        return records;
    }


    /**
     * 根据书本的id获取对应的records对象
     */
    public List<Record> getRecordByBookId(long bookId) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from record where bookId = ?";
        List<Record> records = runner.query(conn, sql, new BeanListHandler<Record>(Record.class), bookId);
        DBHelper.close(conn);
        return records;
    }



    /**
     * 根据用户的身份证查询用户的借阅信息
     * 日期存在问题
     */
    public List<Record> getRecordsByIdNum(String idNumber) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from record where memberId = (select id from member where idNumber = ?)";
        List<Record> records = runner.query(conn, sql, new BeanListHandler<Record>(Record.class), idNumber);
        DBHelper.close(conn);
        return records;
    }


    /**
     * 添加借阅记录
     */
    public int add(long memberId, long bookId, double deposit, long userId) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "insert into record(memberId, bookId, rentDate, backDate, deposit, userId, isbn) values(?, ?, CURRENT_DATE, null, ?, ?, '978-7-302-12260-9')";
        int count = runner.update(conn, sql, memberId, bookId, deposit, userId);
        DBHelper.close(conn);
        return count;
    }


    /**
     * @param deposit 押金： 过期归还 > 0  准时归还： 清零
     * @param userId  管理员编号
     * @param id  record表记录编号
     * @return
     */
    public int modify(double deposit, long userId, long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "update record set backDate=CURRENT_DATE, deposit=?, userId=? where id=?";
        int count = runner.update(conn, sql, deposit, userId, id);
        DBHelper.close(conn);
        return count;
    }

    /**
     * 图书续借
     * @param id
     * @return
     * @throws SQLException
     */
    public int modify(long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "update record set rentDate=CURRENT_DATE where id=?";
        int count = runner.update(conn, sql, id);
        DBHelper.close(conn);
        return count;
    }


    /**
     * 根据记录表的id删除数据
     * @param id
     */
    public int remove(long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "delete from record where id = ?";
        int count = runner.update(sql, conn, id);
        DBHelper.close(conn);
        return count;
    }


    /**
     * 历史查询阶段
     * @param typeId
     *              0：查询所有
     *              1：查询归还的书籍
     *              2：查询未归还的书籍
     *              3：查询7天之内要归还的书籍
     * @param keyWork： 关键字查询
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> query(int typeId, String keyWork) throws SQLException{
        Connection conn = DBHelper.getConnection();
        StringBuilder sbSql = new StringBuilder("select * from recordView where 1 = 1 ");
        switch (typeId){
            case 0:
                break;
            case 1:
                sbSql.append("and backDate is not null ");
                break;
            case 2:
                sbSql.append("and backDate is null ");
                break;
            case 3:
                sbSql.append("and backDate is null and returnDate < date_add(CURRENT_DATE, INTERVAL 7 DAY) ");
                break;
        }
        if(keyWork != null){
            sbSql.append("and (bookName like '%"+ keyWork +"%' or memberName like '%"+ keyWork +"%' or rentDate like binary '%"+ keyWork +"%') ");  // 加上()的原因是当做一个条件， 要不然后面会出现查询不准确的情况
        }
        System.out.println(sbSql);

        // 使用MapListHandler的原因: 查出来的对象没有对应的javaBean,所以每一个对象都放在一个map集合中，最后都装入到list集合中。
        List<Map<String, Object>> data = runner.query(conn, sbSql.toString(), new MapListHandler());
        DBHelper.close(conn);
        return data;
    }



    public static void main(String[] args) throws SQLException {
        RecordDao recordDao = new RecordDao();
//        List<Map<String, Object>> datas = recordDao.query(0, null);
        List<Map<String, Object>> datas = recordDao.query(0, "lsm");
        System.out.println(datas.isEmpty());  // true
        System.out.println(datas);
        for(Map<String, Object> data : datas){
            System.out.println(data);
            System.out.println("data.id: " + data.get("id") + "  " +data.get("id").getClass().equals(Number.class));
            System.out.println("data.memberName: " + data.get("memberName"));
            System.out.println("data.bookName: " + data.get("bookName"));
            System.out.println("data.rentDate: " + data.get("rentDate"));
            System.out.println("data.backDate: " + data.get("backDate"));
            System.out.println("data.returnDate: " + data.get("returnDate"));
            System.out.println("data.deposit: " + data.get("deposit"));
            System.out.println("--------------------------------------------------------------------------------------------------------------------");
        }
    }
}















