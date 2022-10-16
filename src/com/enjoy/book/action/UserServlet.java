package com.enjoy.book.action;

import com.enjoy.book.bean.User;
import com.enjoy.book.biz.UserBiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author Mr.Lu
 * @Date 2022/9/9 9:18
 * @ClassName UserServlet
 * @Version 1.0
 */

@WebServlet("/user.let")
public class UserServlet extends HttpServlet {
    UserBiz userBiz = new UserBiz();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * 一个Servlet下面对应一个地址 /user.let，但是我们可以通过配置参数?type=xxx让一个Servlet可以完成多种不同的请求但是都需要和用户有关
     * /user.let?type=login  登录
     * /user.let?type=exit   安全退出
     * /user.let?modifyPwd   修改密码
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        // 设置编码格式可以预防请求和响应中的中文乱码
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();

        // 1. 判断用户请求的类型为login
        String method = req.getParameter("type");
        switch (method){
            case "login":
                login(req, resp, out, session);
                break;
            case "exit":
                exit(out, session);
                break;
            case "modifyPwd":
                modifyPwd(req, resp, out, session);
                break;
        }
    }

    // user.let?modifyPwd   修改密码
    private void modifyPwd(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) {
        // 修改密码
        // 1. 获取用户输入的新的密码
        String newPwd = req.getParameter("newpwd");
        // 2. 获取用户的编号--user对象在session中存储
        User sessionUser = (User) session.getAttribute("user");
        long id = sessionUser.getId();
        // 3. 调用biz层方法
        int count = userBiz.modifyPwd(id, newPwd);  // 改动的行数
        // 4. 响应-参考exit
        if(count > 0){
            out.println("<script>alert('密码修改成功~');parent.window.location.href='login.html';</script>");
        }else {
            out.println("<script>alert('修改失败！');parent.window.location.href='login.html';</script>");
        }
    }

    // user.let?type=exit   安全退出
    private void exit(PrintWriter out, HttpSession session) {
        // 0. 判断用户是否登录
        if(session.getAttribute("user") == null){
            out.println("<script>alert('请登录！');parent.window.location.href='login.html';</script>");
            return;
        }
        // 1. 清除session
        session.invalidate();
        // 2. 跳转到login.html（框架中需要回去） top.jsp -> index.jsp(parent)
        out.println("<script>parent.window.location.href='login.html'</script>");
        System.out.println("exit");
    }

    // user.let?type=login  登录
    private void login(HttpServletRequest req, HttpServletResponse resp,PrintWriter out,  HttpSession session) {
        // 2. 从login.html中获取用户名和密码，验证码
        String name = req.getParameter("name");
        String pwd = req.getParameter("pwd");
        String userCode = req.getParameter("valcode");

        // 获取session中的code
        String code = session.getAttribute("code").toString();
        if(!code.equalsIgnoreCase(userCode)){
            out.println("<script>alert('验证码错误');location.href='login.html';</script>");
        }

        // 3. 调用UserBiz的getUser方法，根据用户名和密码获取对应的用户对象
        User user = userBiz.getUser(name, pwd);

        // 4. 判断用户对象是否为null
        if(user == null){
            //  4.1 如果是null表示用户名或密码不正确 ，提示错误，回到登录页面
            out.println("<script>alert('用户名或密码不存在');location.href='login.html';</script>");
        }else {
            //  4.2 非空：表示登录成功, 将用户对象保存到session中,提示登录成功后,将页面跳转到index.jsp
            session.setAttribute("user",user);
            out.println("<script>alert('登录成功');location.href='index.jsp';</script>");
        }
    }
}























