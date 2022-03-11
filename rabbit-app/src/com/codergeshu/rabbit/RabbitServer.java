package com.codergeshu.rabbit;

import com.codergeshu.rabbit.common.RabbitAppInfo;
import com.codergeshu.rabbit.util.HttpURLClient;
import com.codergeshu.rabbit.util.URLParamsUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.codergeshu.rabbit.common.SystemConstant.*;

/**
 * 模拟第三方平台后端，利用授权码获取访问令牌
 *
 * @Date: 2022/3/5 23:57
 * @Author: Eric
 */
@WebServlet("/rabbit/server")
public class RabbitServer extends HttpServlet {
    final String AUTH_SERVER_URL = "http://localhost:8082/jd_auth/server";
    final String RESOURCES_SERVER_URL = "http://localhost:8082/jd_resources/server";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, RuntimeException {
        // 根据从开放平台获取的授权码去请求token=access_token|refresh_token
        String code = request.getParameter(CODE);
        Map<String, String> params = new HashMap<>();
        params.put(CODE, code);
        params.put(APP_ID, RabbitAppInfo.APP_ID);
        params.put(APP_SECRET, RabbitAppInfo.APP_SECRET);
        params.put(GRANT_TYPE, AUTHORIZATION_CODE);
        System.out.println("【利用授权码获取访问令牌】");
        try {
            String[] tokens = HttpURLClient.doPost(AUTH_SERVER_URL, URLParamsUtil.mapToStr(params)).split("\\|");
            String accessToken = tokens[0];
            if (tokens.length >= 2) {
                String refreshToken = tokens[1];
            }
            System.out.println("【成功获得访问令牌】");
            // 使用访问令牌请求受保护资源服务
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put(APP_ID, RabbitAppInfo.APP_ID);
            paramsMap.put(APP_SECRET, RabbitAppInfo.APP_SECRET);
            paramsMap.put(ACCESS_TOKEN, accessToken);
            String scope = HttpURLClient.doPost(RESOURCES_SERVER_URL, URLParamsUtil.mapToStr(paramsMap));
            String[] rsScope = scope != null ? scope.split(SEPARATOR) : new String[]{};
            HttpSession session = request.getSession();
            session.setAttribute(RS_SCOPE, rsScope);
            response.sendRedirect("/home.jsp");
        } catch (RuntimeException e) {
            System.out.println("【获取访问令牌异常】");
            throw new RuntimeException("获取访问令牌异常");
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
}
