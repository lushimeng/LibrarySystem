package com.enjoy.book.listener;

import com.enjoy.book.bean.Type;
import com.enjoy.book.biz.TypeBiz;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.List;

/**
 * @Author Mr.Lu
 * @Date 2022/9/22 15:49
 * @ClassName TypeServletContextListener
 * @Version 1.0
 */
@WebListener
public class TypeServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // 1. 获取当前数据库中所有的类型信息
        TypeBiz typeBiz = new TypeBiz();
        List<Type> types = typeBiz.getAll();

        // 2. 获取application对象
        ServletContext application = servletContextEvent.getServletContext();

        // 3. 将信息存在application对象中
        application.setAttribute("types", types);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
