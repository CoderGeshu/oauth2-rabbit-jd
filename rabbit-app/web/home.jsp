<%--
  Created by IntelliJ IDEA.
  User: Eric
  Date: 2022/3/6
  Time: 0:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Rabbit 授权结果</title>
</head>
<body>
    <br>
    <div style="text-align: center">
        <h1>Rabbit</h1>
        一个帮助整理【京东】平台订单信息的 APP
        <br><br>
        被用户授予的权限：
        <c:forEach var="scope" items="${sessionScope.rs_scope}">
            ${scope}&ensp;
        </c:forEach>
    </div>
</body>
</html>
