package com.enjoy.book.biz;

import com.enjoy.book.bean.MemberType;
import com.enjoy.book.dao.MemberTypeDao;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author Mr.Lu
 * @Date 2022/10/2 12:54
 * @ClassName MemberTypeBiz
 * @Version 1.0
 */
public class MemberTypeBiz {
    MemberTypeDao memberTypeDao = new MemberTypeDao();

    // 得到会员的全部类型
    public List<MemberType> getAll(){
        List<MemberType> memberTypes = null;
        try {
            memberTypes = memberTypeDao.getAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return memberTypes;
    }

    // 根据id查询会员类型
    public MemberType getById(int id){
        MemberType memberType = null;
        try {
            memberType = memberTypeDao.getById(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return memberType;
    }
}
