package com.codergeshu.jd.auth.util;

import java.util.Map;
import java.util.Set;

public class URLParamsUtil {
    // 为 URL 追加 query 参数
    public static String appendParams(String url, Map<String, String> params) {
        if (null == url) {
            return "";
        } else if (params.isEmpty()) {
            return url.trim();
        } else {
            StringBuilder sb = new StringBuilder();
            Set<String> keys = params.keySet();
            for (String key : keys) {
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
            url = url.trim();
            int length = url.length();
            int index = url.indexOf("?");
            if (index > -1) { // url最后一个符号为'?'，如：http://wwww.baidu.com?
                if ((length - 1) == index) {
                    url += sb.toString();
                } else { // 情况为：http://wwww.baidu.com?aa=11
                    url += "&" + sb.toString();
                }
            } else { // url后面没有问号，如：http://wwww.baidu.com
                url += "?" + sb.toString();
            }
            return url;
        }
    }

    // 将Map映射为query-string，形式：key1=value1&key2=value2
    public static String mapToStr(Map<String, String> params) {
        StringBuilder sb = new StringBuilder("");
        Set<String> keys = params.keySet();
        for (String key : keys) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

}