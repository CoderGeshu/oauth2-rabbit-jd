package com.codergeshu.jd.auth.common;

import java.util.List;

/**
 * 授权信息
 *
 * @Date: 2022/3/10 22:40
 * @Author: Eric
 */
public class AuthInfo {
    private String appId;
    private String userId;
    private List<String> scope;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getScope() {
        return scope;
    }

    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "AuthInfo{" +
                "appId='" + appId + '\'' +
                ", userId='" + userId + '\'' +
                ", scope=" + scope +
                '}';
    }
}
