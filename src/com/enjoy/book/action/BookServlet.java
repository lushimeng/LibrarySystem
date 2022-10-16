package com.enjoy.book.action;
/**
 * @Author Mr.Lu
 * @Date 2022/9/28 11:18
 * @ClassName BookServlet
 * @Version 1.0
 */
import com.alibaba.fastjson.JSON;
import com.enjoy.book.bean.Book;
import com.enjoy.book.bean.Record;
import com.enjoy.book.biz.BookBiz;
import com.enjoy.book.biz.RecordBiz;
import com.enjoy.book.util.DateHelper;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;


@WebServlet("/book.let")
public class BookServlet extends HttpServlet {
    BookBiz bookBiz = new BookBiz();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

     /**
     * /book.let?type=add 添加图书
     * /book.let?type=modifypre&id=xx 修改前准备
     * /book.let?type=modify        修改
     * /book.let?type=remove&id=xx    删除
     * /book.let?type=query&pageIndex=1 :分页查询(request:转发)
     * /book.let?type=details&id=xx   展示书籍详细信息
     * /book.let?type=doajax&bookName=xx  :使用ajax查询图书名对应的图书信息
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();

        // 验证用户是否登录
        HttpSession session = req.getSession();
        if(session.getAttribute("user") == null){
            out.println("<script>alert('请登录');parent.window.location.href='login.html';</script>");
            return;
        }

        String type = req.getParameter("type");
        switch (type){
            case "add":
                try {
                    add(req, resp, out, session);
                } catch (FileUploadException e) {
                    e.printStackTrace();
                    resp.sendError(500, "文件上传失败");
                } catch (Exception e) {
                    e.printStackTrace();
                    resp.sendError(500, e.getMessage());
                }
                break;
            case "modifypre":
                modifyPre(req, resp, out, session);
                break;
            case "modify":
                try {
                    modify(req, resp, out, session);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "remove":
                remove(req, resp, out, session);
                break;
            case "query":
                query(req, resp, out, session);
                break;
            case "details":
                details(req, resp, out, session);
                break;
            case "doajax":  // 忘记书写了，笑哭
                doajax(req, resp, out, session);
                break;
            default:
                resp.sendError(404);
        }
    }

    /**
     * 根据书本的名称查询书籍对象
     * book.let?type=doajax&bookName=xx  :使用ajax查询图书名对应的图书信息
     */
    private void doajax(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) {
        // 1. 获得传入的书籍参数信息
        String bookName = req.getParameter("bookName");
        // 2. 获得对应的书籍对象
        Book book = bookBiz.getByName(bookName);
        // 3. 响应客户端
        if(book == null){
            out.print("{}");  // 响应一个 null json字符串
        }else{
            // 将book对象转化为json对象
            String bookJson = JSON.toJSONString(book);
            out.print(bookJson);  // print不能加上ln，要不然客户端接收解析会报错。
        }
    }

    /**
     * 查看书本的信息信息
     * book.let?type=details&id=xx
     */
    private void details(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) throws ServletException, IOException {
        // 1. 要查看书本的id
        int bookId = Integer.parseInt(req.getParameter("id"));
        // 2. 根据id查找书本对象
        Book book = bookBiz.getById(bookId);
        // 3. 存
        req.setAttribute("book", book);
        // 4. 转发到book_details.jsp中
        req.getRequestDispatcher("book_details.jsp").forward(req, resp);
    }

    /**
     * 查询
     * book.let?type=query&pageIndex=1
     * 这里要特别注意对上一页和下一页的处理
     * if(pageIndex <  0) pageIndex = 1;
     * if(pageIndex > pageCount) pageIndex = pageCount;
     */
    private void query(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) throws ServletException, IOException {
        // 1. 获取信息（页码， 页数，信息）
        int pageSize = 3; // 每一页展示的信息条数
        int pageCount = bookBiz.getPageCount(pageSize); // 多少页
        int pageIndex = Integer.parseInt(req.getParameter("pageIndex"));  // 从那一条数据进行展示

        // 对上一页下一页的特殊处理, 位置一定要方好，要不然会出现问题
        if(pageIndex <  1) pageIndex = 1;
        if(pageIndex > pageCount) pageIndex = pageCount;
//        System.out.println("我被运行了， 我的pageIndex = " + pageIndex + ", 我的pageCount=" +pageCount);
        List<Book> books = bookBiz.getByPage(pageIndex, pageSize);  // 获得当前页面信息

        // 2. 存
        req.setAttribute("pageCount", pageCount);
        req.setAttribute("books", books);

        // 3. 转发到jsp页面
        req.getRequestDispatcher("book_list.jsp?pageIndex=" + pageIndex).forward(req,resp);
    }

    /**
     * 删除书本信息
     * book.let?type=remove&id=xx
     */
    private void remove(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) {
        long id = Long.parseLong(req.getParameter("id"));
        int count = bookBiz.remove(id);
        if(count > 0){
            out.println("<script>alert('删除成功');location.href='book.let?type=query&pageIndex=1';</script>");
        }else {
            out.println("<script>alert('删除失败');location.href='book.let?type=query&pageIndex=1';</script>");
        }
    }

    /**
     * 修改图书类型
     * book.let?type=modify
     */
    private void modify(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) throws Exception {
        //  1. 得到文件和表单元素集合
        List<FileItem> fileItems = factoryGetItems(req, resp, out);

        // 2. 遍历FileItem
        Book book = new Book();
        for(FileItem fileItem : fileItems){
            if(fileItem.isFormField()){  // 普通的表单域: name-value形式
                // 4.1 得到普通表单域的键name + 值value
                String name = fileItem.getFieldName();
                String value = fileItem.getString("utf-8");  // 防止乱码
                switch (name){
                    case "id":
                        book.setId(Long.parseLong(value));
                        break;
                    case "pic":
                        book.setPic(value);
                        break;
                    case "typeId":
                        book.setTypeId(Long.parseLong(value));
                        break;
                    case "name":
                        book.setName(value);
                        break;
                    case "price":
                        book.setPrice(Double.parseDouble(value));
                        break;
                    case "desc":
                        book.setDesc(value);
                        break;
                    case "publish":
                        book.setPublish(value);
                        break;
                    case "author":
                        book.setAuthor(value);
                        break;
                    case "stock":
                        book.setStock(Long.parseLong(value));
                        break;
                    case "address":
                        book.setAddress(value);
                        break;
                }

            }else {
                // 4.2 文件： 图片的文件名, 文件上传表单域，一个文件
                String picName = fileItem.getName();
                if(picName.trim().length() > 0){  // 如果不修改图片，那么picName为一个空的字符串，不进行下面的操作
                    int index = picName.indexOf(".");
                    String filterName = picName.substring(index);  // 获取 --> .png
                    picName = DateHelper.getImageName() + filterName;   // 修改文件名：20220928144509116.png
                    String path = req.getServletContext().getRealPath("/Images/cover");    // 文件读写：实际路径 --> 虚拟路径：Images/cover对应的实际路径
                    String picPath = path + "/" + picName;       // d:/xxx/xx/2022121223213.png
                    String dbPath = "Images/cover/" + picName;   // 数据库表中的路径
                    book.setPic(dbPath);  // 设置pic信息
                    fileItem.write(new File(picPath));  // 4.3 保存文件
                }
            }
        }

        // 2. 将信息保存到数据库中
//        int count = bookBiz.modify(book.getId(), book.getTypeId(), book.getName(), book.getPrice(), book.getDesc(), book.getPic(), book.getPublish(), book.getAuthor(), book.getStock(), book.getAddress());
        int count = bookBiz.modify(book);
        if(count > 0){
            out.println("<script>alert('修改成功');location.href='book.let?type=query&pageIndex=1';</script>");
        }else {
            out.println("<script>alert('修改失败');location.href='book.let?type=query&pageIndex=1';</script>");
        }
    }

    /**
     * 修改图书信息前的准备
     * book.let?type=modifypre&id=xx
     */
    private void modifyPre(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) throws ServletException, IOException {
        // 1. 获取要修改图书的id信息
        int id = Integer.parseInt(req.getParameter("id"));
        // 2. 得到书本信息
        Book book = bookBiz.getById(id);
        // 3. 存
        req.setAttribute("book", book);
        // 4. 转发
        req.getRequestDispatcher("book_modify.jsp").forward(req, resp);
    }

    /**
     * 添加书籍
     *  1.enctype="multipart/form-data":和以前不同
     *  获取表单元素不能再使用req.getParameter("name") ，因为用了enctype新的上传方式。
     *  2.文件上传 ：图片文件从浏览器端保存到服务器端 (第三方 FileUpload+io)
     *  3.路径
     *    图片:
     *    D:\图书管理系统-JavaWeb\01-数据库脚本\cover\文城.png  :实际路径
     *    http://localhost:8888/mybook_explored_war/Images/cover/文城.png:虚拟路径(服务器)
     */
    private void add(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session) throws Exception {
        // 1. 得到文件和表单元素集合
        List<FileItem> fileItems = factoryGetItems(req, resp, out);

        // 2. 遍历FileItem
        Book book = new Book();
        for(FileItem fileItem : fileItems){
            if(fileItem.isFormField()){  // 普通的表单域: name-value形式
                // 4.1 得到普通表单域的键name + 值value
                String name = fileItem.getFieldName();
                String value = fileItem.getString("utf-8");  // 防止乱码
                switch (name){
                    case "typeId":
                        book.setTypeId(Long.parseLong(value));
                        break;
                    case "name":
                        book.setName(value);
                        break;
                    case "price":
                        book.setPrice(Double.parseDouble(value));
                        break;
                    case "desc":
                        book.setDesc(value);
                        break;
                    case "publish":
                        book.setPublish(value);
                        break;
                    case "author":
                        book.setAuthor(value);
                        break;
                    case "stock":
                        book.setStock(Long.parseLong(value));
                        break;
                    case "address":
                        book.setAddress(value);
                        break;
                }

            }else {  // 文件上传表单域，一个文件
                // 4.2 文件： 图片的文件名   xxx.png
                String picName = fileItem.getName();

                // 这里为了防止文件替换， 把xxx.png ----> 当前系统时间.png
                int index = picName.indexOf(".");
                String filterName = picName.substring(index);  // 获取--> .png

                // 修改文件名：20220928144509116.png
                picName = DateHelper.getImageName() + filterName;

                // 文件读写：实际路径 --> 虚拟路径：Images/cover对应的实际路径
                String path = req.getServletContext().getRealPath("/Images/cover");

                // d:/xxx/xx/2022121223213.png
                String picPath = path + "/" + picName;
                // 数据库表中的路径： Images/cover/101-1.png: 相对项目的根目录位置
                String dbPath = "Images/cover/" + picName;
                book.setPic(dbPath);

                // 4.3 保存文件
                fileItem.write(new File(picPath));
            }
        }

        // 2. 将信息保存到数据库中
        int count = bookBiz.add(book);
        if(count > 0){
            out.println("<script>alert('添加书籍成功');location.href='book.let?type=query&pageIndex=1';</script>");
        }else {
            out.println("<script>alert('添加书籍失败');location.href='book_add.jsp';</script>");
        }
    }

    // 得到文本文档和文件的集合
    private List<FileItem> factoryGetItems(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) throws FileUploadException {
        // 1. 构建一个磁盘工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 1.1 设置大小
        factory.setSizeThreshold(1024*9);
        // 1.2 临时仓库
        File file = new File("e:\\temp");
        if(!file.exists()){
            file.mkdir();
        }
        factory.setRepository(file);

        // 2. 上传文件 + 表单数据
        ServletFileUpload fileUpload = new ServletFileUpload(factory);

        // 3. 将请求解析成一个个FileItem（文件 + 表单元素）
        List<FileItem> fileItems = fileUpload.parseRequest(req);

        return fileItems;
    }
}



