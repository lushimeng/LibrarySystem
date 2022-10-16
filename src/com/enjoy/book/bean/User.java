package com.enjoy.book.bean;

/**
 * @Author Mr.Lu
 * @Date 2022/9/9 8:31
 * @ClassName User
 * @Version 1.0
 */

/**
 *  注意事项：
 *  1. serializable,序列化接口，可以使用IO完成对象的读写；
 *  2. 私有属性；
 *  3. getXXX/setXXX；
 *  4. 存在默认的构造方法；
 */
public class User {

    private long id;
    private String name;
    private String pwd;
    private long state;

    public User() {
    }

    public User(long id, String name, String pwd, long state) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
        this.state = state;
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

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", state=" + state +
                '}';
    }
}
