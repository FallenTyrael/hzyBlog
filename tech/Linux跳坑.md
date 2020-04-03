# Linux各种坑

## Java创建文件权限不足

### 问题原因

通过ps -ef|grep tomcat 可知该进程权限为tomcat

服务进程为权限tomcat，对应文件夹权限为root，导致无法创建文件

### 解决方案

```shell
chown -R tomcat $file$
-R 为递归
```
