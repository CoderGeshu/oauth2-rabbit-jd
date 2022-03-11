package com.codergeshu.rabbit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.codergeshu.rabbit.common.SystemConstant.*;

/**
 * @Date: 2022/3/6 12:05
 * @Author: Eric
 */
@WebServlet("/rabbit/index")
public class RabbitIndexController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("【开放授权平台回调至Rabbit，并赋上授权码】");
        response.sendRedirect("/index.jsp?code=" + request.getParameter(CODE));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("APP Index doPost...");
    }
}
