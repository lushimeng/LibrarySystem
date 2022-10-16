package com.enjoy.book.bean;

/**
 * @Author Mr.Lu
 * @Date 2022/9/21 19:35
 * @ClassName Type
 * @Version 1.0
 */

import java.io.Serializable;

/**
 * 图书类型信息
 *  注意事项：
 *  1. Serializable,序列化接口，可以使用IO完成对象的读写；
 *  2. 私有属性；
 *  3. getXXX/setXXX；
 *  4. 存在默认的构造方法；
 *
 */
public class Type implements Serializable {
    private long id;
    private String name;
    private long parentId;

    public Type() {
    }

    public Type(long id, String name, long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
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

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "Type{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}
