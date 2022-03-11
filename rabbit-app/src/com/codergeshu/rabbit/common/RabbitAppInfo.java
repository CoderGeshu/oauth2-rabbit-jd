package com.codergeshu.rabbit.common;

import static com.codergeshu.rabbit.common.SystemConstant.SEPARATOR;

/**
 * 第三方软件的注册信息
 *
 * @Date: 2022/3/5 21:41
 * @Author: Eric
 */
public class RabbitAppInfo {

    public static final String APP_ID = "rabbitId1828";
    public static final String APP_SECRET = "rabbitSecret9876";
    public static final String REDIRECT_URI = "http://localhost:8081/rabbit/index";
    public static final String SCOPE = "query" + SEPARATOR + "add" + SEPARATOR + "del";

}
