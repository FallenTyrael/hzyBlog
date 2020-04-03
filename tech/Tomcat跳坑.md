# Tomcat各种坑

- [Tomcat各种坑](#tomcat%E5%90%84%E7%A7%8D%E5%9D%91)
  - [1.Tomcat GC overhead limit exceeded/Java heap space](#1tomcat-gc-overhead-limit-exceededjava-heap-space)
    - [1.1 问题原因](#11-%E9%97%AE%E9%A2%98%E5%8E%9F%E5%9B%A0)
    - [1.2 解决方案](#12-%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88)
  - [2.Tomcat 卡在Deploying web application directory](#2tomcat-%E5%8D%A1%E5%9C%A8deploying-web-application-directory)
    - [2.2问题原因](#22%E9%97%AE%E9%A2%98%E5%8E%9F%E5%9B%A0)
    - [2.3解决方案](#23%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88)
  - [3.Tomcat配置参数](#3tomcat%E9%85%8D%E7%BD%AE%E5%8F%82%E6%95%B0)


## 1.Tomcat GC overhead limit exceeded/Java heap space

### 1.1 问题原因

Tomcat内存溢出，需要设置初始内存和最大内存

### 1.2 解决方案

```text
Linux：修改$TOMCAT_HOME$/bin/catalina.sh

Windows：修改$TOMCAT_HOME$/bin/catalina.bat
在其上方添加
JAVA_OPTS="-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=1024m"
```

## 2.Tomcat 卡在Deploying web application directory 

### 2.2问题原因

linux或者部分unix系统提供随机数设备是/dev/random 和/dev/urandom，其中urandom安全性没有random高，但random需要时间间隔生成随机数，jdk默认调用random，从而生成随机数时间间隔长从而到时Tomcat启动速度慢

### 2.3解决方案

```text
打开$JAVA_HOME$/jre/lib/security/java.security
securerandom.source=file:/dev/random
修改为
securerandom.source=file:/dev/./urandom
```

## 3.Tomcat配置参数

以下命令可以查看Tomcat内存配置参数

```text
ps -ef|grep tomcat
```
