package com.codergeshu.rabbit;

import com.codergeshu.rabbit.common.RabbitAppInfo;
import com.codergeshu.rabbit.util.URLParamsUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.codergeshu.rabbit.common.SystemConstant.*;

/**
 * 模拟第三方软件前端，引导跳转至开放授权平台
 *
 * @Date: 2022/3/5 21:50
 * @Author: Eric
 */
@WebServlet("/rabbit/redirect_to_auth")
public class RabbitRedirectController extends HttpServlet {

    // 开放授权平台url
    String oauthUrl = "http://localhost:8082/jd_auth/server";

    // 第三方软件重定向到开放授权平台，引导用户授权
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> params = new HashMap<>();
        params.put(APP_ID, RabbitAppInfo.APP_ID);
        params.put(REDIRECT_URI, RabbitAppInfo.REDIRECT_URI);
        params.put(SCOPE, RabbitAppInfo.SCOPE);
        params.put(RESPONSE_TYPE, CODE);     // 如果是授权码许可类型，请求授权码code
        // params.put(RESPONSE_TYPE, TOKEN); // 如果是隐式许可类型，告诉授权服务直接返回access_token
        String toOauthUrl = URLParamsUtil.appendParams(oauthUrl, params);
        System.out.println("【重定向到开放授权平台引导用户授权】");
        response.sendRedirect(toOauthUrl); // 第一次重定向
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("APP Index doPost...");
    }

}
