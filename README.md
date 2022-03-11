## oauth2-rabbit-jd
模拟 Oauth 2.0 授权码流程，第三方客户端经用户在开放平台授权，获取相关权限访问相关资源。

### rabbit-app

模拟第三方客户端，需要获取京东开放平台的授权。

**IDEA Tomcat 配置**

Open browser

- url: `http://localhost:8081/`

- VM options: `-Dfile.encoding=UTF-8`

Server Settings

- HTTP port: `8081`

- JMX port: `1099`

### jd-auth

模拟京东开放授权平台，向 Rabbit 授权。

**IDEA Tomcat 配置**

Open browser

- url: `http://localhost:8082/`

- VM options: `-Dfile.encoding=UTF-8`

Server Settings

- HTTP port: `8082`

- JMX port: `1010`

### 时序图

![image-20220306183435964](https://gitee.com/CoderGeshu/pic-go-images/raw/master/img/image-20220306183435964.png)
