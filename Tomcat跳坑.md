# Tomcat各种坑
## Tomcat GC overhead limit exceeded/Java heap space
### 问题原因
Tomcat内存溢出，需要设置初始内存和最大内存

### 解决方案
Linux：修改$TOMCAT_HOME$/bin/catalina.sh

Windows：修改$TOMCAT_HOME$/bin/catalina.bat
```
在其上方添加
JAVA_OPTS="-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=1024m"
```
## Tomcat 卡在Deploying web application directory 
### 问题原因
linux或者部分unix系统提供随机数设备是/dev/random 和/dev/urandom，其中urandom安全性没有random高，但random需要时间间隔生成随机数，jdk默认调用random，从而生成随机数时间间隔长从而到时Tomcat启动速度慢

### 解决方案
打开$JAVA_HOME$/jre/lib/security/java.security
```
securerandom.source=file:/dev/random
修改为
securerandom.source=file:/dev/./urandom
```

## Tomcat配置参数
以下命令可以查看Tomcat内存配置参数
```
ps -ef|grep tomcat
```
