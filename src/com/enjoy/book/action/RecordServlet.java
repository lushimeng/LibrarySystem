package com.enjoy.book.action;

import com.alibaba.fastjson.JSON;
import com.enjoy.book.bean.Member;
import com.enjoy.book.bean.Record;
import com.enjoy.book.bean.User;
import com.enjoy.book.biz.MemberBiz;
import com.enjoy.book.biz.RecordBiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author Mr.Lu
 * @Date 2022/10/9 10:54
 * @ClassName RecordServlet
 * @Version 1.0
 */
@WebServlet("/record.let")
public class RecordServlet extends HttpServlet {
    RecordBiz recordBiz  = new RecordBiz();
    MemberBiz memberBiz = new MemberBiz();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * /record.let?type=add&memberId=1&ids=5_25_7 图书借阅
     * /record.let?type=queryback&idNumber=xx:根据会员的身份证号查询会员信息及借阅信息
     * /record.let?type=back&memberId=1&ids=1_5_4 图书归还
     * /record.let?type=keep&id=x 书籍续借
     * /record.let?type=doajax&typeId=x&keyword=xx ajax查询
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置编码
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession();

        // 判断用户是否登录
        if(session.getAttribute("user") == null){
            out.println("<script>alert('请登录！');parent.window.location.href='login.html';</script>");
            return;
        }


        String type = req.getParameter("type");
        switch (type){
            case "add":
                add(req, resp, out, session);
                break;
            case "queryback":
                queryBack(req, resp, out, session);
                break;
            case "back":
                back(req, resp,out, session);
                break;
            case "keep":
                keep(req, resp, out, session);
                break;
            case "doajax":
                doAjax(req, resp,out, session);
                break;
            default:
                resp.sendError(404, "请求的地址不存在");
        }

    }

    // record.let?type=doajax&typeId=x&keyword=xx ajax查询
    private void doAjax(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) throws ServletException, IOException {
        // 1. 获取typeId信息, 这里的typeId指的为查询类型有(0, 1, 2, 3)四种选择
       int typeId = Integer.parseInt(req.getParameter("typeId"));

        // 2. 获取keyword信息,空串："";   null: 表示没有找到keyword;
        String keyword = req.getParameter("keyword");
        keyword = keyword.isEmpty() ? null : keyword;

        // 3. 收获数据
        List<Map<String, Object>> query = recordBiz.query(typeId, keyword);

        // 4. 转成json
        String jsonQuery = JSON.toJSONString(query);
        out.print(jsonQuery);  // 输出到前端界面中去
    }

    // record.let?type=keep&id=x 书籍续借
    private void keep(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) {
        // 1. 得到续借信息
        Long recordId = Long.parseLong(req.getParameter("id"));
        // 2. 调用对应的biz层信息
        int count = recordBiz.modify(recordId);
        if(count > 0){
            out.print("<script>alert('续借成功');location.href='main.jsp';</script>");
        } else {
            out.print("<script>alert('续借失败');location.href='main.jsp';</script>");
        }
    }

    // record.let?type=back&memberId=1&ids=1_5_4
    private void back(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) {
        // 1. 获取会员的编号
        Long memberId = Long.parseLong(req.getParameter("memberId"));

        // 2. 获取记录编号
        String idStr = req.getParameter("ids");
        String[] ids = idStr.split("_");
        ArrayList<Long> recordIds = new ArrayList<>();
        for(String s : ids){
            recordIds.add(Long.parseLong(s));
        }

        // 3. 获取操作的用户编号
        User user = (User)session.getAttribute("user");
        long userId = user.getId();

        // 4. 调用record对应的biz层
        int count = recordBiz.modify(memberId, recordIds, userId);

        // 5. 归还图书
        if(count > 0){
            out.print("<script>alert('归还成功');location.href='main.jsp';</script>");
        }else {
            out.print("<script>alert('归还失败');location.href='main.jsp';</script>");
        }
    }

    // record.let?type=queryback&idNumber=xx:根据会员的身份证号查询会员信息及借阅信息
    private void queryBack(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) throws ServletException, IOException {
        // 1. 获取会员身份证号码
        String idNumber = req.getParameter("idNumber");
        // 2. 得到会员对象
        Member member = memberBiz.getByIdNumber(idNumber);
        if(member == null){  // 根本就不存在该会员身份证号
            out.print("<script>alert('身份证输入不正确');location.href='return_list.jsp';</script>");
            return;
        }

        // 3. 得到member对象的record记录
        List<Record> records = recordBiz.getRecordsByMemberId(member.getId());
        // 4. 存request
        req.setAttribute("member", member);
        req.setAttribute("records", records);
        // 5. 转发
        req.getRequestDispatcher("return_list.jsp").forward(req, resp);
    }

    // 图书借阅
    private void add(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) {
        // 1. 得到会员编号
        Long memberId = Long.parseLong(req.getParameter("memberId"));
        // 2. 借阅的书籍编号
        String idStr = req.getParameter("ids");
        String[] ids = idStr.split("_");
        ArrayList<Long> bookIds = new ArrayList<>();
        for(String s : ids){
            bookIds.add(Long.parseLong(s));
        }

        // 3. 当前负责处理这个借阅的管理员编号
        User user = (User)session.getAttribute("user");
        long userId = user.getId();

        // 4. 调用record对应的biz层
        int count = recordBiz.add(memberId, bookIds, userId);

        if(count > 0){
            out.print("<script>alert('借阅成功');location.href='main.jsp';</script>");
        }else {
            out.print("<script>alert('借阅失败');location.href='main.jsp';</script>");
        }


    }
}
