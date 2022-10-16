package com.enjoy.book.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author Mr.Lu
 * @Date 2022/10/16 13:18
 * @ClassName UserLoginFilter
 * @Version 1.0
 */

@WebFilter("*.jsp")  // 过来所有的jsp
public class UserLoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse)servletResponse;
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        // 判断用户是否登录： 已经登录，放行   没有登录拦截下来
        HttpSession session = req.getSession();
        if(session.getAttribute("user") != null){
            filterChain.doFilter(req, resp);
        }else {
            out.println("<script>alert('请登录！');location.href='login.html';</script>");
        }
    }

    @Override
    public void destroy() {

    }
}
