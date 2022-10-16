<%--
  Created by IntelliJ IDEA.
  User: lenovo
  Date: 2022/9/19
  Time: 11:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html >
<head>
    <title>享学科技 - 网站后台管理中心</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="description" content="享学科技专业的网站后台管理系统" />
    <meta name="keywords" content="享学,网站管理系统,企业网站" />
    <meta name="Author" content="phenix" />
    <meta name="CopyRight" content="享学科技" />
</head>
<frameset rows="64,*"  frameborder="no" border="0" framespacing="0">
    <!--头部-->
    <frame src="./top.jsp" name="top" noresize="noresize" frameborder="0"  scrolling="no" marginwidth="0" marginheight="0" />
    <!--主体部分-->
    <frameset cols="185,*">
        <!--主体左部分-->
        <frame src="./left.jsp" name="left" noresize="noresize" frameborder="0" scrolling="no" marginwidth="0" marginheight="0" />
        <!--主体右部分-->
        <frame src="./main.jsp" name="main" frameborder="0" scrolling="auto" marginwidth="0" marginheight="0" />
    </frameset>
</html>>
