package com.codergeshu.jd.auth.db;

import java.util.HashMap;
import java.util.Map;

import static com.codergeshu.jd.auth.common.SystemConstant.*;

/**
 * 模拟数据库
 *
 * @Date: 2022/3/5 22:25
 * @Author: Eric
 */
public class DataBase {
    /**
     * 模拟第三方软件在开放授权平台上的注册信息
     */
    public static Map<String, Map<String, String>> registerAppInfos = new HashMap<>();

    static {
        // Rabbit软件的相关注册信息
        Map<String, String> rabbitAppInfo = new HashMap<>();
        rabbitAppInfo.put(APP_ID, "rabbitId1828");
        rabbitAppInfo.put(APP_SECRET, "rabbitSecret9876");
        rabbitAppInfo.put(REDIRECT_URI, "http://localhost:8081/rabbit/index");
        rabbitAppInfo.put(SCOPE, "query_add_del");
        registerAppInfos.put("rabbitId1828", rabbitAppInfo);
    }

    /**
     * 模拟存储授权页面的会话 Id
     */
    public static Map<String, String> reqIdInfos = new HashMap<>();

    /**
     * 模拟登录开放授权平台的当前用户信息
     */
    public static final Map<String, String> currentUser = new HashMap<>();

    static {
        currentUser.put("userId", "CoderGeshu1828"); // 模拟用户已经登录
    }

    /**
     * 模拟存储授权码信息及其权限范围信息
     */
    public static Map<String, String> codeInfos = new HashMap<>();
    public static Map<String, String[]> codeScopeInfos = new HashMap<>();

}
