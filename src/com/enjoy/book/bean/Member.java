package com.enjoy.book.bean;

import javax.swing.*;
import java.io.Serializable;
import java.sql.Date;

/**
 * @Author Mr.Lu
 * @Date 2022/9/30 15:04
 * @ClassName Member
 * @Version 1.0
 */
public class Member implements Serializable {
    private long id;
    private String name;
    private String pwd;
    private long typeId;
    private double balance;
    private java.sql.Date regdate;
    private String tel;
    private String idNumber;

    // 外键
    MemberType memberType;

    public Member() {
    }

    public Member(long id, String name, String pwd, long typeId, double balance, Date regdate, String tel, String idNumber, MemberType memberType) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.typeId = typeId;
        this.balance = balance;
        this.regdate = regdate;
        this.tel = tel;
        this.idNumber = idNumber;
        this.memberType = memberType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", typeId=" + typeId +
                ", balance=" + balance +
                ", regdate=" + regdate +
                ", tel='" + tel + '\'' +
                ", idNumber='" + idNumber + '\'' +
                '}';
    }
}
