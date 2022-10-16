package com.enjoy.book.bean;

import java.io.Serializable;

/**
 * @Author Mr.Lu
 * @Date 2022/9/30 15:05
 * @ClassName MemberType
 * @Version 1.0
 */
public class MemberType implements Serializable {
    private long id;
    private String name;
    private long amount;
    private double discount;
    private long recharge;
    private long keepDay;

    public MemberType() {
    }

    public MemberType(long id, String name, long amount, double discount, long recharge, long keepDay) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.discount = discount;
        this.recharge = recharge;
        this.keepDay = keepDay;
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

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public long getRecharge() {
        return recharge;
    }

    public void setRecharge(long recharge) {
        this.recharge = recharge;
    }

    public long getKeepDay() {
        return keepDay;
    }

    public void setKeepDay(long keepDay) {
        this.keepDay = keepDay;
    }

    @Override
    public String toString() {
        return "MemberType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", discount=" + discount +
                ", recharge=" + recharge +
                ", keepDay=" + keepDay +
                '}';
    }
}
