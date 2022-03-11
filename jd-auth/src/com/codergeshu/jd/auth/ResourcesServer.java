package com.codergeshu.jd.auth;

import com.codergeshu.jd.auth.common.AuthInfo;
import com.codergeshu.jd.auth.util.JwtUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.codergeshu.jd.auth.common.SystemConstant.*;

/**
 * 京东资源保护服务
 *
 * @Date: 2022/3/6 0:00
 * @Author: Eric
 */
@WebServlet("/jd_resources/server")
public class ResourcesServer extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accessToken = request.getParameter(ACCESS_TOKEN);
        try {
            AuthInfo authInfo = JwtUtil.getAuthInfoFromToken(accessToken);
            System.out.println("【第三方正在访问受保护资源，APP：" + authInfo.getAppId() + "，授权用户：" + authInfo.getUserId() + "】");
            // 根据当时授权的token对应的权限范围，做相应的处理动作，不同权限对应不同的操作
            List<String> scope = authInfo.getScope();
            System.out.println("【第三方被授予的权限：" + scope + "】");
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(String.valueOf(scope));
        } catch (Exception e) {
            System.out.println("【access_token错误】");
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Protected GET...");
    }

    // 查询
    private void queryGoods() {
    }

    // 增加
    private void addGoods() {
    }

    // 删除
    private void delGoods() {
    }
}
