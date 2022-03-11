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
    <title>Rabbit 首页</title>
</head>
<body>
    <br>
    <div style="text-align: center">
        <h1>Rabbit</h1>
        一个帮助整理【京东】平台订单信息的 APP
        <br><br>
        <c:if test="${param.code == null}">
            <a href="http://localhost:8081/rabbit/redirect_to_auth">
                <button>
                    去京东授权
                </button>
            </a>
        </c:if>
        <c:if test="${param.code != null}">
            京东授权码：${param.code}&ensp;
            <a href="http://localhost:8081/rabbit/server?code=${param.code}">
                <button>
                    获取访问令牌
                </button>
            </a>
        </c:if>
    </div>
</body>
</html>
