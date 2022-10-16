package com.enjoy.book.action;

import com.alibaba.fastjson.JSON;
import com.enjoy.book.bean.Member;
import com.enjoy.book.bean.MemberType;
import com.enjoy.book.bean.Record;
import com.enjoy.book.biz.MemberBiz;
import com.enjoy.book.biz.MemberTypeBiz;
import com.enjoy.book.biz.RecordBiz;

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
 * @Date 2022/10/2 14:26
 * @ClassName MemberServlet
 * @Version 1.0
 */
@WebServlet("/member.let")
public class MemberServlet extends HttpServlet {
    MemberBiz memberBiz = new MemberBiz();
    MemberTypeBiz memberTypeBiz = new MemberTypeBiz();
    RecordBiz recordBiz = new RecordBiz();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
    * /member.let?type=addpre 添加准备(MemberTypes)
    * /member.let?type=add  添加会员
    * /member.let?type=modifypre&id=xx 修改准备(MemberTypes ,Member)
    * /member.let?type=modify 修改
    * /member.let?type=remove&id=xx 删除
    * /member.let?type=query  查询
    * /member.let?type=modifyrecharge 充值
     * /member.let?type=ajax&idNumber=XXX 通过ajax进行请求
    * @param req
    * @param resp
    * @throws ServletException
    * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置编码格式
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();

        // 判断用户是否登录
        HttpSession session = req.getSession();
        if(session.getAttribute("user") == null){
            out.println("<script>alert('请登录！');parent.window.location.href='login.html';</script>");
            return;
        }


        String type = req.getParameter("type");
        switch (type){
            case "addpre":
                addPre(req, resp, out);
                break;
            case "add":
                add(req, resp, out);
                break;
            case "modifypre":
                modifyPre(req, resp, out);
                break;
            case "modify":
                modify(req, resp, out);
                break;
            case "remove":
                try {
                    remove(req, resp, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "query":
                query(req, resp, out);
                break;
            case "modifyrecharge":
                modifyRecharge(req, resp, out);
                break;
            case "ajax":
                ajax(req, resp, out);
                break;
            default:
                resp.sendError(404, "请求资源不存在");
        }
    }

    // member.let?type=ajax&idNumber=XXX 通过ajax进行请求
    private void ajax(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
        // 1. 获取身份证号
        String idNumber = req.getParameter("idNumber");
        // 2. 获取member对象
        Member member = memberBiz.getByIdNumber(idNumber);
        // 2.1 修改member结束的数量
        List<Record> records = recordBiz.getRecordsByMemberId(member.getId());
        if(records.size() > 0){
            long size = member.getMemberType().getAmount() - records.size();
            member.getMemberType().setAmount(size);
        }
        // 3. 将member对象转化为JSON对象
        String memberJson = JSON.toJSONString(member);
        // 4. 响应客户端 ， 特别注意out.打印不能进行换行
        out.print(memberJson);


    }

    // member.let?type=addpre 添加准备(MemberTypes)
    private void addPre(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws ServletException, IOException {
        // 1. 获取所有的会员类型
        List<MemberType> memberTypes = memberTypeBiz.getAll();
        // 2. 存入request
        req.setAttribute("memberTypes", memberTypes);
        // 3. 转发
        req.getRequestDispatcher("mem_add.jsp").forward(req,resp);
    }

    // member.let?type=add
    private void add(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
        // 1. 创建一个member对象，添加到数据库中
        Member member = new Member();
        // 2. 获取请求的参数信息
        String name = req.getParameter("name");
        String pwd = req.getParameter("pwd");
        String memberTypeId = req.getParameter("memberType");
        String balance = req.getParameter("balance");
        String tel = req.getParameter("tel");
        String idNumber = req.getParameter("idNumber");

        // 封装成member对象
        member.setName(name);
        member.setPwd(pwd);
        member.setTypeId(Long.parseLong(memberTypeId));
        member.setBalance(Double.parseDouble(balance));
        member.setTel(tel);
        member.setIdNumber(idNumber);

        // 3. 调用biz成执行sql语句
        int count = memberBiz.add(member);

        if(count > 0){
            // 注意： location.href 不能直接转到mem_list.jsp，因为该jsp还没有内容， 要跳转到member.let?type=query进行查询（查询后具有数据了跳转到了mem_list.jsp）
            out.println("<script>alert('会员开卡成功');location.href='member.let?type=query';</script>");
        }else {
            out.println("<script>alert('会员开卡失败');location.href='member.let?type=query';</script>");
        }
    }

    // member.let?type=modifypre&id=xx 修改准备(MemberTypes ,Member)
    private void modifyPre(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws ServletException, IOException {
        // 1. 根据id，获取member对象 ,
        long id = Long.parseLong(req.getParameter("id"));
        Member member = memberBiz.getById(id);  // 得到member对象

        // 2. 获取所有的memberTypes对象
        List<MemberType> memberTypes = memberTypeBiz.getAll();

        // 3. 存
        req.setAttribute("member", member);
        req.setAttribute("memberTypes", memberTypes);

        // 4. 转发
        req.getRequestDispatcher("mem_modify.jsp").forward(req, resp);
    }

    // member.let?type=modify 修改
    private void modify(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
        // 1. 获取请求参数信息
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String pwd = req.getParameter("pwd");
        String memberTypeId = req.getParameter("memberType");
        String balance = req.getParameter("balance");
        String tel = req.getParameter("tel");
        String idNumber = req.getParameter("idNumber");


        // 2. 声明一个Member对象，并将请求参数信息放入到member对象中
        Member member = new Member();
        member.setId(Long.parseLong(id));
        member.setName(name);
        member.setPwd(pwd);
        member.setTypeId(Long.parseLong(memberTypeId));
        member.setBalance(Double.parseDouble(balance));
        member.setTel(tel);
        member.setIdNumber(idNumber);

        // 3. 调用biz成执行sql语句
        int count = memberBiz.modify(member);

        if(count > 0){
            // 注意： location.href 不能直接转到mem_list.jsp，因为该jsp还没有内容， 要跳转到member.let?type=query进行查询（查询后具有数据了跳转到了mem_list.jsp）
            out.println("<script>alert('会员信息修改成功');location.href='member.let?type=query';</script>");
        }else {
            out.println("<script>alert('会员信息修改失败');location.href='member.let?type=query';</script>");
        }
    }

    // member.let?type=remove&id=xx 删除
    private void remove(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws Exception {
        // 1. 获取要删除会员的id
        Long id = Long.parseLong(req.getParameter("id"));
        // 2. 调用对应biz层的方法进行删除
        int count = memberBiz.remove(id);
        // 3. 删除成功跳转到展示界面
        if(count > 0){
            out.println("<script>alert('删除成功');location.href='member.let?type=query';</script>");
        }else {
            out.println("<script>alert('删除失败, 请检查该会员是否有余额或有子信息');location.href='member.let?type=query';</script>");
        }
    }

    // member.let?type=query  查询
    private void query(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws ServletException, IOException {
        // 1. 获取会员列表信息
        List<Member> memberList = memberBiz.getAll();
        req.setAttribute("memberList", memberList);

        // 2. 转发到展示界面
        req.getRequestDispatcher("mem_list.jsp").forward(req, resp);
    }

    // member.let?type=modifyrecharge 充值
    private void modifyRecharge(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
        // 1. 获取表信息
        String idNumber = req.getParameter("idNumber");
        double amount = Double.parseDouble(req.getParameter("amount"));

        // 2. 调用biz层进行添加
        int count = memberBiz.modifyBalance(idNumber, amount);

        if(count > 0){
            out.println("<script>alert('充值成功');location.href='member.let?type=query';</script>");
        }else {
            out.println("<script>alert('充值成功');location.href='member.let?type=query';</script>");
        }
    }
}







































