<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html >
<html >
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="keywords"  content = "图书 java jsp"/>
    <meta http-equiv="author" content="phenix"/>
    <link rel="stylesheet" type="text/css" href="./Style/skin.css" />
    <script src="Js/jquery-3.3.1.min.js"></script>
    <script>

        /**
         * 获取系统的当前时间
         * 2022-10-22
         * 参考：https://zhuanlan.zhihu.com/p/450208567
         */
        function getCurrentDate(){
            let objDate = new Date();
            let fullYear = objDate.getFullYear();
            let month = objDate.getMonth() + 1; // 0 ~ 11
            let date = objDate.getDate();
            let dateStr = fullYear + "-" + (month >= 10 ? month : "0" + month) + "-" + (date >= 10 ? date : "0" + date);
            return dateStr
        }

        /**
         * 获取返回书籍的时间
         * 具体参考：https://zhuanlan.zhihu.com/p/450208567
         */
        function getBackDate(count){
           let objDate = new Date();
           let mills = objDate.getMilliseconds();  // 得到毫秒值 1s = 1000ms
            mills += count * 24 * 60 * 60 * 1000;
            objDate.setMilliseconds(mills);
            let fullYear = objDate.getFullYear();
            let month = objDate.getMonth() + 1; // 0 ~ 11
            let date = objDate.getDate();
            let backDateStr = fullYear + "-" + (month >= 10 ? month : "0" + month) + "-" + (date >= 10 ? date : "0" + date);
            return backDateStr
        }


        $(function (){
            $("#btnQueryBook").prop("disabled", "disabled");
            $("#btnSubmit").prop("disabled", "disabled");


            var member = null;
            // 查询会员部分 ajax实现
           $("#btnQuery").click(function (){
               // 1. 获取用户输入的身份证号
               var content = $("#memberId").val();
               if(!content){
                   alert("请输入用户号");
                   return;
               }

               // 2. 调用js-ajax()/post/get
               var url = "member.let?type=ajax&idNumber=" + content;
               $.get(url, function (data, status){
                  // json字符串：{"balance":187.0,"id":1,"idNumber":"300312199506150011","memberType":{"amount":5,"discount":100.0,"id":1,"keepDay":30,"name":"普通会员","recharge":100},"name":"andy","pwd":"andyliu","regdate":1627747200000,"tel":"13374645654","typeId":1}
                  console.log(data)
                   // 特殊情况 {}
                   if(data==="{}"){
                       alert("无此会员信息，请重新输入");
                       $("#memberId").val("");
                       $("#btnQuery").removeAttr("disabled");
                       return;
                   }

                   // 2.1 将json字符串转化为json对象
                   member = JSON.parse(data);

                   console.log(member.name +", " + member.memberType.name + ", " + member.memberType.amount + ", " + member.balance);
                    // 给组件赋值
                   $("#name").val(member.name);
                   $("#type").val(member.memberType.name);
                   $("#amount").val(member.memberType.amount);
                   $("#balance").val(member.balance);
               });
               // 查询用户的功能关闭
               $("#btnQuery").prop("disabled", "disabled");
               // 开启查询按钮的功能
               $("#btnQueryBook").removeAttr("disabled");

           });// of btnQuery


           // 查询图书部分 ajax实现
            var bookNameList = new Array();
           $("#btnQueryBook").click(function (){
               // 1. 获取用户输入的书名
               let bookName = $("#bookContent").val();
               // 2. 调用js-ajax()/post/get
               let url = "book.let?type=doajax&bookName="+bookName;
               $.get(url, function (data, status){
                  // 一本书的json字符串
                   console.log(data);
                   // 特殊情况 {}
                  if(data==="{}"){
                      alert("当前书籍不存在，查找失败");
                      $("#bookContent").val("");
                      return;
                  }
                  // 书本是否存在过
                   if(bookNameList.indexOf(bookName) >= 0){
                       alert("当前书籍已添加， 添加失败")
                       return;
                   }

                   bookNameList.push(bookName);
                  let book = JSON.parse(data); // 将json字符串转换为book对象

                   //1.创建行
                   let tr = $("<tr align=\"center\" class=\"d\">");
                   //2.创建多个列
                   let tdCheck = $("<td><input type=\"checkbox\" value=\""+book.id+"\" class=\"ck\" checked /></td>");
                   let tdName = $("<td>"+book.name+"</td>");
                   let tdRentDate = $("<td>"+ getCurrentDate() +"</td>");   // 借书时间
                   let tdBackDate = $("<td>"+ getBackDate(member.memberType.keepDay) +"</td>");  // 还书时间
                   let tdPublish = $("<td>"+book.publish+"</td>");
                   let tdAddress = $("<td>"+book.address+"</td>");
                   let tdPrice = $("<td>"+book.price+"</td>");

                   //3.行加列
                   tr.append(tdCheck);
                   tr.append(tdName);
                   tr.append(tdRentDate);
                   tr.append(tdBackDate);
                   tr.append(tdPublish);
                   tr.append(tdAddress);
                   tr.append(tdPrice);
                   //4.表加行
                   $("#tdBook").append(tr);
                   $("#bookContent").val("");
                   $("#btnSubmit").removeAttr("disabled");
               });  // of get
           });  // of btnQueryBook


            // 全选和全不选功能实现
            $("#ckAll").click(function (){
                $(".ck").prop("checked", $(this).prop("checked"))
            });


            // 提交功能实现
            $("#btnSubmit").click(function (){
                // 1. 获取用户选择的书籍编号(1-3-6-7)
                var ids = new Array();
                var count = 0;
                $(".ck").each(function (){
                   if($(this).prop("checked")){
                       ids.push($(this).val());
                       count++;
                   }
                });

                if(count === 0){
                    alert("请选择借阅书籍");
                    return;
                }

                if(count > member.memberType.amount){
                    alert("借阅数量超出范围");
                    return;
                }

                console.log(ids)
                console.log(ids.join("_"));

                // 请求servlet: http://localhost/mybook_war_exploded/record.let?add&memberId=1&ids=5_25
                location.href="record.let?type=add&memberId="+ member.id + "&ids=" + ids.join("_");

            });
        });  // of function

    </script>
</head>
    <body>
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
            <!-- 头部开始 -->
            <tr>
                <td width="17" valign="top" background="./Images/mail_left_bg.gif">
                    <img src="./Images/left_top_right.gif" width="17" height="29" />
                </td>
                <td valign="top" background="./Images/content_bg.gif">

                </td>
                <td width="16" valign="top" background="./Images/mail_right_bg.gif"><img src="./Images/nav_right_bg.gif" width="16" height="29" /></td>
            </tr>
            <!-- 中间部分开始 -->
            <tr>
                <!--第一行左边框-->
                <td valign="middle" background="./Images/mail_left_bg.gif">&nbsp;</td>
                <!--第一行中间内容-->
                <td valign="top" bgcolor="#F7F8F9">
                    <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
                        <!-- 空白行-->
                        <tr><td colspan="2" valign="top">&nbsp;</td><td>&nbsp;</td><td valign="top">&nbsp;</td></tr>
                        <tr>
                            <td colspan="4">
                                <table>
                                    <tr>
                                        <td width="100" align="center"><img src="./Images/mime.gif" /></td>
                                        <td valign="bottom"><h3 style="letter-spacing:1px;">常用功能 > 图书借阅 </h3></td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <!-- 一条线 -->
                        <tr>
                            <td height="20" colspan="4">
                                <table width="100%" height="1" border="0" cellpadding="0" cellspacing="0" bgcolor="#CCCCCC">
                                    <tr><td></td></tr>
                                </table>
                            </td>
                        </tr>
                        <!-- 会员信息开始 -->
                        <tr>
                            <td width="2%">&nbsp;</td>
                            <td width="96%">
                                <fieldset>
                                    <legend>查询会员</legend>
                                    <table width="100%" border="0" class="cont"  >
                                        <tr>
                                            <td width="8%" class="run-right"> 会员编号</td>
                                            <td colspan="7"><input class="text" type="text" id="memberId" name="memberId"/>
                                                 <input type="button" id="btnQuery" value="确定" style="width: 80px;"/></td>

                                            </td>

                                        </tr>
                                        <tr>
                                            <td width="8%" class="run-right">会员名称</td>
                                            <td width="17%"><input class="text" type="text" id="name" disabled/></td>
                                            <td width="8%" class="run-right">会员类型:</td>
                                            <td width="17%"><input class="text" type="text" id="type" disabled/></td>
                                            <td width="8%" class="run-right">可借数量</td>
                                            <td width="17%"><input class="text" type="text" id="amount"  disabled/></td>
                                            <td width="8%" class="run-right">账户余额</td>
                                            <td width="17%"><input class="text" type="text" id="balance" disabled/></td>
                                        </tr>
                                    </table>
                                </fieldset>
                            </td>
                            <td width="2%">&nbsp;</td>
                        </tr>

                        <!--空行-->
                        <tr>
                            <td height="40" colspan="3">
                            </td>
                        </tr>



                        <!--图书搜索条-->
                        <tr>
                            <td width="2%">&nbsp;</td>
                            <td width="96%">
                                <fieldset>
                                    <legend>查询图书</legend>
                                    <table width="100%" border="1" class="cont"  >
                                        <tr>
                                            <td colspan="8">

                                                请输入:&nbsp;&nbsp;<input class="text" type="text" id="bookContent" name="bookContent" placeholder="输入条形码/书名"/>
                                                <input type="button" id="btnQueryBook" value="确定" style="width: 80px;"/>
                                                <input type="button" id="btnSubmit" value="完成借阅" style="width: 80px;"/>
                                            </td>

                                        </tr>

                                    </table>
                                </fieldset>
                            </td>
                            <td width="2%">&nbsp;</td>
                        </tr>
                        <tr><td height="20" colspan="3"></td></tr>
                        <tr>
                            <td width="2%">&nbsp;</td>
                            <td width="96%">
                                <table width="100%">
                                    <tr>
                                        <td colspan="2">
                                            <form action="" method="">
                                                <table width="100%"  class="cont tr_color" id="tdBook">
                                                    <tr>
                                                        <th><input  type="checkbox" id="ckAll" checked/>全选/全不选</th>
                                                        <th>书籍名</th>
                                                        <th>借阅时间</th>
                                                        <th>应还时间</th>
                                                        <th>出版社</th>
                                                        <th>书架</th>
                                                        <th>定价(元)</th>
                                                    </tr>

                                                </table>
                                            </form>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                            <td width="2%">&nbsp;</td>
                        </tr>
                        <!-- 产品列表结束 -->
                        <tr>
                            <td height="40" colspan="4">
                                <table width="100%" height="1" border="0" cellpadding="0" cellspacing="0" bgcolor="#CCCCCC">
                                    <tr><td></td></tr>
                                </table>
                            </td>
                        </tr>
                        <tr>
                            <td width="2%">&nbsp;</td>
                            <td width="51%" class="left_txt">
                                <img src="./Images/icon_mail.gif" width="16" height="11"> 客户服务邮箱：2087924818@qq.com<br />
                            </td>
                            <td>&nbsp;</td><td>&nbsp;</td>
                        </tr>
                    </table>
                </td>
                <td background="./Images/mail_right_bg.gif">&nbsp;</td>
            </tr>
            <!-- 底部部分 -->
            <tr>
                <td valign="bottom" background="./Images/mail_left_bg.gif">
                    <img src="./Images/buttom_left.gif" width="17" height="17" />
                </td>
                <td background="./Images/buttom_bgs.gif">
                    <img src="./Images/buttom_bgs.gif" width="17" height="17">
                </td>
                <td valign="bottom" background="./Images/mail_right_bg.gif">
                    <img src="./Images/buttom_right.gif" width="16" height="17" />
                </td>
            </tr>
        </table>
    </body>
</html>