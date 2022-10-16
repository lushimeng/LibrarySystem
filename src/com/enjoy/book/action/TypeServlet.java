package com.enjoy.book.action;

import com.enjoy.book.bean.Type;
import com.enjoy.book.biz.TypeBiz;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @Author Mr.Lu
 * @Date 2022/9/22 15:32
 * @ClassName TypeServlet
 * @Version 1.0
 */
@WebServlet("/type.let")
public class TypeServlet extends HttpServlet {
    TypeBiz typeBiz = new TypeBiz();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }


    /**
     * /type.let?type=add:添加(表单)
     * /type.let?type=modifypre&id=xx 修改预备(req->转发->type_modify.jsp)
     * /type.let?type=modify 修改(表单)
     * /type.let?type=remove&id=xx删除(获取删除的类型编号)
     * /type_list.jsp 查询(所有的类型数据:当项目运行时(监听器)，数据读放到application对象)
     * servlet:
     *  request:同一个请求中传输信息
     *  session:同一个会话(用户)
     *  application:同一个运行项目
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 设置编码方式
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        // 2. 获取各种对象
        PrintWriter out = resp.getWriter();
        ServletContext application = req.getServletContext();

        // 判断用户是否登录
        HttpSession session = req.getSession();
        if(session.getAttribute("user") == null){
            out.println("<script>alert('请登录！');parent.window.location.href='login.html';</script>");
            return;
        }

        // 3. 根据用户的需求完成业务
        String type = req.getParameter("type");
        switch (type){
            case "add":
                add(req, resp, out, application);
                break;
            case "modifypre":
                modifyPre(req, resp, out, application);
                break;
            case "modify":
                modify(req, resp, out, application);
                break;
            case "remove":
                remove(req, resp, out, application);
                break;
        }


    }

    /**
     * 删除书本类型
     */
    private void remove(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) {
        // 1. 获取需要删除的id
        long id = Long.parseLong(req.getParameter("id"));

        // 2. 调用Biz层方法进行删除
        try {
            int count = typeBiz.remove(id);
            if(count > 0){
                List<Type> types = typeBiz.getAll();
                // 更新application
                application.setAttribute("types", types);
                out.println("<script>alert('删除成功');location.href='type_list.jsp';</script>");
            }else {
                out.println("<script>alert('删除失败');location.href='type_list.jsp';</script>");
            }
        } catch (Exception e) {
            out.println("<script>alert('" + e.getMessage() +"');location.href='type_list.jsp';</script>");
        }
    }

    /**
     * modifyPre --> type_modify.jsp --> /type.let&type=modify --> type_lsit.jsp
     */
    private void modify(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) {
        // 1. 获取修改类型信息(id, name, parentId)
        Long id = Long.parseLong(req.getParameter("typeId"));
        String name = req.getParameter("typeName");
        Long parentId = Long.parseLong(req.getParameter("parentType"));

        // 2. 调用biz的修改方法
        int count = typeBiz.modify(id, name, parentId);

        // 3. 更新application
        if(count > 0){
            List<Type> types = typeBiz.getAll();
            application.setAttribute("types",types);
            out.println("<script>alert('修改成功');location.href='type_list.jsp'</script>");
        }else {
            out.println("<script>alert('修改失败');location.href='type_list.jsp'</script>");
        }

    }

    private void modifyPre(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) throws ServletException, IOException {
        // 1. 获取需要修改的type对象的id
        long id = Long.parseLong(req.getParameter("id"));
        // 2. 根据id获取type对象
        Type type = typeBiz.getById(id);
        // 3. 把type存到req中，同一个功能
        req.setAttribute("type", type);
        req.getRequestDispatcher("type_modify.jsp").forward(req, resp);
    }

    /**
     * 添加类型
     * type_add.jsp ——> type.let?type=add --> ok --> type_list.jsp
     *                                        no --> type_add.jsp
     */
    private void add(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) {
        // 1. 从表单中获取名字和父类型
        String typeName = req.getParameter("typeName");
        Long parentTypeId = Long.parseLong(req.getParameter("parentType"));

        // 2. 判断新添加的typeName是否已经存在
        boolean isExist = false;
        List<Type> types = typeBiz.getAll();
        for(Type type : types){
            if(type.getName().equals(typeName)){
                isExist = true;
                break;
            }
        }

        // 3. 如果存在进行下一步的逻辑，不存在提示添加失败，跳转到type_add.jsp
        if(!isExist){
            // 3.1 存在，则直接插入到数据库中
            typeBiz.add(typeName, parentTypeId);
            // 3.2 更新application中的types
            application.setAttribute("types", typeBiz.getAll());
            out.println("<script>alert('添加成功');location.href='type_list.jsp'</script>");
        }else {
            // 3.3 添加失败
            out.println("<script>alert('添加失败');location.href='type_add.jsp'</script>");
        }
    }
}








































