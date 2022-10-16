package com.enjoy.book.biz;

import com.enjoy.book.bean.Member;
import com.enjoy.book.bean.MemberType;
import com.enjoy.book.dao.MemberDao;
import com.enjoy.book.dao.MemberTypeDao;
import com.enjoy.book.util.DBHelper;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author Mr.Lu
 * @Date 2022/10/2 12:59
 * @ClassName MemberBiz
 * @Version 1.0
 */
public class MemberBiz {
    // Member类中含有MemberType的外键
    MemberDao memberDao = new MemberDao();
    MemberTypeDao memberTypeDao = new MemberTypeDao();

    // 添加会员信息
    public int add(String name, String pwd, long typeId, double balance, String tel, String idNumber){
        try {
            return memberDao.add(name, pwd, typeId, balance, tel, idNumber);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    // 重写会员添加发给发
    public int add(Member member){
        return add(member.getName(), member.getPwd(), member.getTypeId(), member.getBalance(), member.getTel(), member.getIdNumber());
    }

    // 修改会员信息
    public int modify(long id,String name,String pwd,long typeId,double balance,String tel,String idNumber){
        try {
            return memberDao.modify(id, name, pwd, typeId, balance, tel, idNumber);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    // 重写修改会员信息方法
    public int modify(Member member){
        return this.modify(member.getId(), member.getName(), member.getPwd(), member.getTypeId(), member.getBalance(), member.getTel(), member.getIdNumber());
    }

    // 根据id删除会员信息
    public int remove(long id) throws Exception {
        // 1. 判断会员账号余额 > 0 : 提示不能删除
        Member member = memberDao.getById(id);
        if(member.getBalance() > 0){
//            throw new Exception("此会员消费金额大于0， 删除失败");
            return 0;
        }

        // 2. 存在外键不能删除
        if(memberDao.exits(id)){
//            throw new Exception("此会员有子信息， 删除失败");
            return 0;
        }

        // 3. 删除
        int count = 0;
        count = memberDao.remove(id);
        return count;
    }

    // 根据会员的身份证号码修改押金
    public int modifyBalance(String idNumber,double amount){
        try {
            return memberDao.modifyBalance(idNumber, amount);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    // 根据id修改押金
    public int modifyBalance(long id,double amount){
        try {
            return memberDao.modifyBalance(id, amount);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
    }

    // 会员列表
    public List<Member> getAll(){
        try {
            List<Member> members = memberDao.getAll();
            if(members.size() == 0) return null;
            for(Member member : members){
                // 根据类型编号获取类型对象
                long typeId = member.getTypeId();
                MemberType memberType = memberTypeDao.getById((int) typeId);
                member.setMemberType(memberType);
            }
            return members;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    // 根据会员编号查会员信息
    public Member getById(long id) {
        try {
            Member member = memberDao.getById(id);
            if(member == null) return null;
            long typeId = member.getTypeId();  // 得到外键表的主键id
            MemberType memberType = memberTypeDao.getById((int) typeId);
            member.setMemberType(memberType);  // 设置Member类的外键
            return member;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    // 根据会员身份证查会员信息
    public Member getByIdNumber(String idNumber){
        try {
            Member member = memberDao.getByIdNumber(idNumber);
            if(member == null) return null;
            long typeId = member.getTypeId();  // 得到外键表的主键id
            MemberType memberType = memberTypeDao.getById((int) typeId);
            member.setMemberType(memberType);  // 设置Member类的外键
            return member;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    // 判断会员编号是否存在Reocrd中
    public boolean exits(long id){
        try {
            return memberDao.exits(id);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
}
