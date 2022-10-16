package com.enjoy.book.biz;

import com.enjoy.book.bean.User;
import com.enjoy.book.dao.UserDao;

import java.sql.SQLException;

/**
 * @Author Mr.Lu
 * @Date 2022/9/9 8:58
 * @ClassName UserBiz
 * @Version 1.0
 */
public class UserBiz {
    // 构建UserDao的对象
    UserDao userDao = new UserDao();
    public User getUser(String name, String pwd){
        User user = null;
        try {
            user = userDao.getUser(name, pwd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }


    public  int modifyPwd(long id, String pwd){
        int count = 0;
        try {
            count = userDao.modifyPwd(id, pwd);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(count);
        return count;
    }

    public User getById(long id){
        try {
            return userDao.getById(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}
