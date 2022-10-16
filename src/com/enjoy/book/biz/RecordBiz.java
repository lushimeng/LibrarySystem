package com.enjoy.book.biz;


import com.enjoy.book.bean.Book;
import com.enjoy.book.bean.Member;
import com.enjoy.book.bean.Record;
import com.enjoy.book.bean.User;
import com.enjoy.book.dao.BookDao;
import com.enjoy.book.dao.MemberDao;
import com.enjoy.book.dao.RecordDao;
import com.enjoy.book.dao.UserDao;
import com.enjoy.book.util.DBHelper;
import com.enjoy.book.util.DateHelper;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Lu
 * @Date 2022/9/30 15:34
 * @ClassName RecordBiz
 * @Version 1.0
 */
public class RecordBiz {
    RecordDao recordDao = new RecordDao();
    BookDao bookDao = new BookDao();
    BookBiz bookBiz = new BookBiz();
    MemberDao memberDao = new MemberDao();
    MemberBiz memberBiz = new MemberBiz();
    UserBiz userBiz = new UserBiz();

    /**
     * 归还图书功能
     * book --> 归还的图书对应要进行 +1
     * member --> 押金退还
     * record --> 根据recordIds进行删除数据
     * @param memberId： 会员id信息
     * @param recordIds：记录表中要归还的id信息
     * @param userId
     * @return
     */
    public int modify(Long memberId, ArrayList<Long> recordIds, long userId) {
        try {
            DBHelper.beginTransaction();  // 启动事务
            Member member = memberBiz.getById(memberId);  // 退还图书会员对象
            // 1. 遍历会员退还图书的记录表id
            double total = 0;
            for(Long recordId : recordIds){
                Record record = recordDao.getById(recordId);
                // 2. 修改book表信息
                long bookId = record.getBookId();
                bookBiz.modify(bookId, 1); // 对应书本 +1

                // 2.1 计算押金（逾期：超出1天扣1块）
                java.sql.Date curDate = new java.sql.Date(System.currentTimeMillis());
                java.sql.Date backDate = DateHelper.getNewDate(record.getRentDate(),member.getMemberType().getKeepDay());
                int day = 0;
                double deposit = 0;
                if(backDate.before(curDate)){  // 逾期
                    day = DateHelper.getSpan(curDate, backDate);
                    deposit =  record.getDeposit();
                    if(day <= deposit){
                        total += deposit - day;
                    }else {
                        total += 0;  // 逾期天数大于其押金，则不进行退还
                    }
                }else {  // 不逾期
                    total += record.getDeposit();
                }

                // 2.2 修改record表的信息
                System.out.println("day : " + day + ", userId : " + userId + ", recordId : " + record);
                recordDao.modify(day, userId, recordId); // 把押金修改为0
                System.out.println("测试点1");
            }
            // 2.3 修改member表信息
            memberBiz.modifyBalance(memberId, total);
            DBHelper.commitTransaction();  // 提交事务，要么全部成功要么全部失败，是整体的。
        } catch (SQLException throwables) {
            try {
                DBHelper.rollbackTransaction();  // 事务回滚
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
            return 0; // 操作失败
        }
        return 1; // 操作成功
    }

    /**
     * 根据借阅表的recodeId实现图书续借
     * @param id
     * @return
     */
    public int modify(long id){
        int count = 0;
        try {
            count = recordDao.modify(id);
            return count;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    /**
     * 借阅:
     * 1.借一本数： record表添加一行信息(recordDao,insert) 1
     * 2.这本书的数量-1 (bookDao,update) 1
     * 3.会员信息表中 ,更改余额( memberDao,update) 1
     * 要么全部成功，要么全部失败(一个业务(事务处理))
     * 前提:用同一个connection对象(如何?)
     * @param memberId
     * @param bookIdList
     * @param userId
     * @return 0:操作失败  1:操作成功
     */
    public int add(long memberId, List<Long> bookIdList, long userId){
        try {
            // 1. 启动事务
            DBHelper.beginTransaction();
            double total = 0;
            // 拿到借阅的书籍编号
            for(Long bookId : bookIdList){
                // 书籍对象
                Book book = bookDao.getById(bookId);

                // 调用价格
                double deposit = book.getPrice();
                double regPrice = deposit * 0.3f;
                total += regPrice;

                // 调用recordDao --> insert
                recordDao.add(memberId, bookId, regPrice, userId);
                // 调用bookDao --> 书本数量
                bookDao.modify(bookId, -1);
            }
            // 调用memberDao --> update 更新用户余额
            memberDao.modifyBalance(memberId, 0-total);

            DBHelper.commitTransaction();  // 事务提交：成功
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                DBHelper.rollbackTransaction();  // 事务回滚：有异常
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;  // 操作失败
        }
        return 1;  // 操作成功
    }

    /**
     * 根据bookId获得records集合
     * @param bookId
     * @return
     */
    public List<Record> getRecordByBookId(long bookId){
        try {
            List<Record> records = recordDao.getRecordByBookId(bookId);
            if(records == null) return null;
            Book book = bookBiz.getById(bookId);
            for(Record record : records){
                // 设置book外键
                record.setBook(book);
                // 设置member外键
                long memberId = record.getMemberId();
                Member member = memberBiz.getById(memberId);
                record.setMember(member);
                // 设置user外键
                long userId = record.getUserId();
                User user = userBiz.getById(userId);
                record.setUser(user);
            }
            return records;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }

    }

    /**
     * 根据会员身份证信息获得record记录信息
     * @param idNumber
     * @return
     */
    public List<Record> getRecordsByIdNum(String idNumber) {
        try {
            List<Record> records = recordDao.getRecordsByIdNum(idNumber);
            if(records == null) return null;
            for(Record record : records){
                // 设置book外键
                long bookId = record.getBookId();
                Book book = bookBiz.getById(bookId);
                record.setBook(book);
                // 设置member外键
                long memberId = record.getMemberId();
                Member member = memberBiz.getById(memberId);
                record.setMember(member);
                // 设置user外键
                long userId = record.getUserId();
                User user = userBiz.getById(userId);
                record.setUser(user);
            }
            return records;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    /**
     * 根据用户的会员编号查询用户借阅信息
     */
    public List<Record> getRecordsByMemberId(long memberId) {
        try {
            List<Record> records = recordDao.getRecordsByMemberId(memberId);
            if(records == null) return null;

            // 1. 获取外键信息
            Member member = memberBiz.getById(memberId);  // 使用biz层的原因在于获取外键
            for(Record record : records){
                // 设置member外键
                record.setMember(member);
                // 设置book外键
                long bookId = record.getBookId();
                Book book = bookBiz.getById(bookId);
                record.setBook(book);

                // 2. 应还时间 ,借阅时间+keeyDay
                long day = member.getMemberType().getKeepDay();
                // 做时间的计算
                java.sql.Date rentDate = record.getRentDate();
                java.sql.Date backDate = DateHelper.getNewDate(rentDate, day);
                record.setBackDate(backDate);
            }
            return records;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }


    /**
     * 历史查询
     * @param typeId
     * @param keyWork
     * @return
     */
    public List<Map<String, Object>> query(int typeId, String keyWork){
        List<Map<String, Object>> query = null;
        try {
            query = recordDao.query(typeId, keyWork);
            return query;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        RecordBiz recordBiz = new RecordBiz();
        List<Record> records = recordBiz.getRecordsByMemberId(10);
    }
}
