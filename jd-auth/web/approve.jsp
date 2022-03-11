<%--
  Created by IntelliJ IDEA.
  User: Eric
  Date: 2022/3/6
  Time: 12:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>京东开放授权平台</title>
    <style>
        .authForm {
            display: flex;
            justify-content: center;
            align-items: center
        }
    </style>
</head>
<body>
    <br>
    <div style="text-align: center">
        <h1>京东开放授权平台</h1>
        当前用户：${requestScope.loginUser.get("userId")} <br><br>
        【Rabbit】需要获得您在京东平台的以下权限：<br><br>
    </div>
    <div class="authForm">
        <form action="/jd_auth/server" method="post">
            <input type="hidden" name="req_id" value="<%=request.getAttribute("req_id")%>"/>
            <input type="hidden" name="app_id" value="<%=request.getAttribute("app_id")%>"/>
            <input type="hidden" name="redirect_uri" value="<%=request.getAttribute("redirect_uri")%>"/>
            <input type="hidden" name="response_type" value="<%=request.getAttribute("response_type")%>"/>
            <c:forEach var="scope" items="${requestScope.req_scope}">
                <label>
                    <input type="checkbox" value="${scope}" name="rs_scope" checked/>${scope}
                </label><br>
            </c:forEach>
            <br>
            <label>
                <input type="radio" checked name="req_type" value="approve"/>授权
            </label>&ensp;&ensp;
            <label>
                <input type="radio" name="req_type" value="refuse"/>拒绝
            </label>
            <br><br>
            <input type="submit" value="提交"/>
        </form>
    </div>
</body>
</html>

