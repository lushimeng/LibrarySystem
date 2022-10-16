package com.enjoy.book.bean;

import java.io.Serializable;

/**
 * @Author Mr.Lu
 * @Date 2022/9/21 20:37
 * @ClassName Book
 * @Version 1.0
 */
public class Book implements Serializable {
    private long id;
    private long typeId;  // 外键，与type表主键相连接
    private String name;
    private double price;
    private String desc;
    private String pic;
    private String publish;
    private String author;
    private long stock;
    private String address;

    // 外键对应的实体对象
    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Book() {
    }

    public Book(long id, long typeId, String name, double price, String desc, String pic, String publish, String author, long stock, String address) {
        this.id = id;
        this.typeId = typeId;
        this.name = name;
        this.price = price;
        this.desc = desc;
        this.pic = pic;
        this.publish = publish;
        this.author = author;
        this.stock = stock;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getStock() {
        return stock;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", desc='" + desc + '\'' +
                ", pic='" + pic + '\'' +
                ", publish='" + publish + '\'' +
                ", author='" + author + '\'' +
                ", stock=" + stock +
                ", address='" + address + '\'' +
                '}';
    }
}
