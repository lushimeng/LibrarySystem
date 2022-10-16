package com.enjoy.book.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    /**
     * 获取上传图片名
     * @return
     */
    public static String getImageName(){
        SimpleDateFormat  sdf= new SimpleDateFormat("yyyyMMddHHmmssS");
        return sdf.format(new Date());
    }

    /**
     * 对date日期进行加减操作
     * @param rentDate
     * @param amount
     * @return
     */
    public static java.sql.Date getNewDate(java.sql.Date rentDate, long amount){
        long mills = rentDate.getTime();
        mills += amount * 24 * 60 * 60 * 1000;
        java.sql.Date backDate = new java.sql.Date(mills);
        return backDate;
    }


    /**
     * 计算两个时间差
     * @param date01
     * @param date02
     * @return
     */
    public static int getSpan(Date date01, Date date02){
        long mills = date01.getTime() - date02.getTime();
        int day = (int) (mills / 1000 / 60 / 60 / 24);
        return Math.abs(day);
    }


    public static void main(String[] args) {
        java.sql.Date  date01 =  java.sql.Date.valueOf("2021-10-01");
        java.sql.Date  date02 = getNewDate(date01,31);
        System.out.println(date01+","+date02);
    }

}
