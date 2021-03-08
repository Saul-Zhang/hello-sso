# 一、简介

## 1.1 开发环境

- Spring Boot 2.4.3
- JDK 1.8
- redis 6.0.9

## 项目目录

 - hello-sso-server：这个模块提供sso服务
 - hello-sso-demo：这个模块主要是模拟需要单点登录的系统

# 二、使用方法

- 在两个模块的`application.yml`中的分别配置redis信息

- 修改hosts文件

  ```shell
  #模拟跨域
  127.0.0.1 hellosso.com
  127.0.0.1 client1.com
  127.0.0.1 client2.com
  ```

- 分别运行`HelloSsoDemoApplication` 和`HelloSsoServerApplication`

- 浏览器访问`http://client1.com:8088/hello`和`http://client1.com:8088/hello`

  # 三、关于

- 有关sso单点登录的介绍及运行效果，可以访问http://zsly.xyz/archives/sso 查看

