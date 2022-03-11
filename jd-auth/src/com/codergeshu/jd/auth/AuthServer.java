package com.codergeshu.jd.auth;

import com.codergeshu.jd.auth.common.AuthException;
import com.codergeshu.jd.auth.common.AuthInfo;
import com.codergeshu.jd.auth.db.DataBase;
import com.codergeshu.jd.auth.util.JwtUtil;
import com.codergeshu.jd.auth.util.URLParamsUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.codergeshu.jd.auth.common.SystemConstant.*;

/**
 * 京东开放授权服务
 *
 * @Date: 2022/3/5 22:49
 * @Author: Eric
 */
@WebServlet("/jd_auth/server")
public class AuthServer extends HttpServlet {

    private final Long ONE_DAY = 3600000L * 24; // 一天（毫秒）

    // 开放授权平台显示授权页面前的准备工作，处理从第三方软件的请求，验证成功后才会显示授权页面
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, AuthException {
        Map<String, String> loginUser = DataBase.currentUser;
        // 如果用户不处于登录状态，此处需要引导用户登录开发授权平台
        if (loginUser.isEmpty()) {
            // redirect to login auth server
            return;
        }
        System.out.println("【验证第三方软件的合法性】");
        String appId = request.getParameter(APP_ID);
        String redirectUri = request.getParameter(REDIRECT_URI);
        String[] reqScope = request.getParameter(SCOPE).split(SEPARATOR);
        String responseType = request.getParameter(RESPONSE_TYPE);
        // 开放平台利用注册信息库，验证第三方软件的请求是否合法
        Map<String, String> registerAppInfo = DataBase.registerAppInfos.get(appId);
        if (registerAppInfo == null)
            throw new AuthException("未找到当前请求的注册信息");
        if (!registerAppInfo.get(REDIRECT_URI).equals(redirectUri))
            throw new AuthException("请求的回调地址未通过验证");
        if (!checkScope(appId, reqScope))
            throw new AuthException("请求的权限范围未通过验证");
        // 如果是授权码许可类型
        if (CODE.equals(responseType)) {
            // 生成授权页面reqId
            String reqId = String.valueOf(System.currentTimeMillis());
            DataBase.reqIdInfos.put(reqId, reqId);
            // 跳转到授权页面，至此颁发授权码的准备工作完毕
            System.out.println("【验证通过，显示授权页面】");
            request.setAttribute(REQ_ID, reqId);
            request.setAttribute(APP_ID, appId);
            request.setAttribute(REDIRECT_URI, redirectUri);
            request.setAttribute(RESPONSE_TYPE, responseType);
            request.setAttribute(REQ_SCOPE, reqScope);
            // 开放授权平台当前登录的用户信息，
            request.setAttribute("loginUser", loginUser);
            request.getRequestDispatcher("/approve.jsp").forward(request, response);
        } else if (TOKEN.equals(responseType)) {
            // 如果是隐式许可流程，该流程全是在前端通信中完成的
            List<String> scope = Collections.singletonList("query");
            String accessToken = generateAccessToken(appId, loginUser.get("userId"), scope);
            Map<String, String> params = new HashMap<>();
            params.put(REDIRECT_URI, redirectUri);
            params.put(ACCESS_TOKEN, accessToken);
            String toAppUrl = URLParamsUtil.appendParams(redirectUri, params); // 重定向至回调地址
            System.out.println("【隐式许可流程，直接将access_token返回给前端】");
            response.sendRedirect(toAppUrl);
        }

    }

    // 开放平台根据请求类型 生成授权码 或 生成访问令牌
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, AuthException {
        String currentUserId = DataBase.currentUser.get("userId");
        String appId = request.getParameter(APP_ID);
        // 行为一：处理授权页面提交，根据用户的授权行为生成授权码，并重定向到第三方软件
        String reqType = request.getParameter(REQ_TYPE);
        if (reqType != null) {
            // 处理用户确认授权的动作
            if ("approve".equals(reqType)) {
                System.out.println("【用户确认了授权】");
                // 校验授权页面reqId
                if (!DataBase.reqIdInfos.containsKey(request.getParameter(REQ_ID)))
                    throw new AuthException("授权页面异常");
                // 如果第三方软件在请求授权码，则生成授权码
                if (CODE.equals(request.getParameter(RESPONSE_TYPE))) {
                    // 验证用户选择的权限范围，未授权任何权限则scope设为{}
                    String[] rsScope = request.getParameterValues(RS_SCOPE);
                    if (rsScope == null) rsScope = new String[]{};
                    if (!checkScope(appId, rsScope))
                        throw new AuthException("用户选择权限超出了第三方软件注册的权限");
                    // 生成授权码，并绑定授权范围
                    String code = generateCode(appId, currentUserId);
                    DataBase.codeScopeInfos.put(code, rsScope);
                    // 重定向到第三方的回调地址
                    System.out.println("【生成授权码并重定向到第三方平台】");
                    Map<String, String> params = new HashMap<>();
                    params.put(CODE, code);
                    String toAppUrl = URLParamsUtil.appendParams(request.getParameter(REDIRECT_URI), params);
                    response.sendRedirect(toAppUrl); // 第二次重定向
                }
            } else {
                System.out.println("【用户拒绝了授权】");
                response.sendRedirect(request.getParameter(REDIRECT_URI));
            }
        }

        // 行为二：生成访问令牌，并返回给第三方软件
        String grantType = request.getParameter(GRANT_TYPE);
        if (grantType != null) {
            // 再次校验第三方软件的合法性，此时会用的第三方软件的密钥
            String appSecret = request.getParameter(APP_SECRET);
            Map<String, String> registerAppInfo = DataBase.registerAppInfos.get(appId);
            if (!registerAppInfo.get(APP_ID).equals(appId))
                throw new AuthException("未找到当前请求的注册信息");
            if (!registerAppInfo.get(APP_SECRET).equals(appSecret))
                throw new AuthException("第三方平台密钥信息错误");
            switch (grantType) {
                // 如果是授权码凭据许可类型
                case AUTHORIZATION_CODE: {
                    String code = request.getParameter(CODE);
                    if (!existCode(code)) // 验证授权码
                        throw new AuthException("授权码错误");
                    DataBase.codeInfos.remove(code); // 授权码一旦被使用，要立即作废
                    // 生成访问令牌与刷新令牌，返回给第三方平台
                    String[] scope = DataBase.codeScopeInfos.get(code);
                    String accessToken = generateAccessToken(appId, currentUserId, Arrays.asList(scope));
                    String refreshToken = generateRefreshToken(appId, currentUserId, Arrays.asList(scope));
                    DataBase.codeScopeInfos.remove(code);
                    System.out.println("【根据授权码生成访问令牌：" + accessToken + "】");
                    response.getWriter().write(accessToken + "|" + refreshToken);
                    break;
                }
                // 如果是刷新令牌
                case REFRESH_TOKEN: {
                    String refreshToken = request.getParameter(REFRESH_TOKEN);
                    try {
                        AuthInfo authInfo = JwtUtil.getAuthInfoFromToken(refreshToken);
                        // 生成新的访问令牌与刷新令牌
                        String newAccessToken = generateAccessToken(appId, currentUserId, authInfo.getScope());
                        String newRefreshToken = generateRefreshToken(appId, currentUserId, authInfo.getScope());
                        // 一个刷新令牌被使用以后需要立即废弃，并重新颁发
                        response.getWriter().write(newAccessToken + "|" + newRefreshToken);
                    } catch (Exception e) {
                        throw new AuthException("refresh_token错误");
                    }
                    break;
                }
                // 如果是客户端资源凭据许可类型
                case CLIENT_CREDENTIALS: {
                    System.out.println("【客户端资源凭据许可类型】");
                    break;
                }
                // 如果是资源拥有者凭据许可类型
                case PASSWORD: {
                    System.out.println("【资源拥有者凭据许可类型】");
                    break;
                }
                default: {
                    System.out.println("【不是任何类型】");
                    break;
                }
            }
        }
    }

    /**
     * 生成授权码
     */
    private String generateCode(String appId, String userId) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(r.nextInt(10));
        }
        String code = sb.toString();
        DataBase.codeInfos.put(code, appId + "|" + userId + "|" + System.currentTimeMillis());
        return code;
    }

    /**
     * 生成访问令牌
     */
    private String generateAccessToken(String appId, String userId, List<String> scope) {
        try {
            AuthInfo authInfo = new AuthInfo();
            authInfo.setAppId(appId);
            authInfo.setUserId(userId);
            authInfo.setScope(scope);
            return JwtUtil.generateToken(authInfo, ONE_DAY * 15); // 15天
        } catch (Exception e) {
            System.out.println("【生成access_token异常】");
        }
        return "";
    }

    /**
     * 生成刷新令牌
     */
    private String generateRefreshToken(String appId, String userId, List<String> scope) {
        try {
            AuthInfo authInfo = new AuthInfo();
            authInfo.setAppId(appId);
            authInfo.setUserId(userId);
            authInfo.setScope(scope);
            return JwtUtil.generateToken(authInfo, ONE_DAY * 30); // 30天
        } catch (Exception e) {
            System.out.println("【生成refresh_token异常】");
        }
        return "";
    }


    /**
     * 验证授权码
     */
    private boolean existCode(String code) {
        return DataBase.codeInfos.containsKey(code);
    }

    /**
     * 验证用户授权的权限
     */
    private boolean checkScope(String appId, String[] scopes) {
        Map<String, String> registerAppInfo = DataBase.registerAppInfos.get(appId);
        if (registerAppInfo == null) return false;
        String registerScope = registerAppInfo.get(SCOPE);
        for (String scope : scopes) {
            if (!registerScope.contains(scope)) {
                return false;
            }
        }
        return true;
    }

}
