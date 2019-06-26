# Nginx+Tomcat设置SSL跳坑
## Nginx配置SSL
多网站配置文件中，对一个地址添加2个server，一个监听443端口，用于https；一个监听80端口，用于http跳转https
```
server{
  ...
  listen 443;
  server_name $web_site$;
  ssl on;
  ssl_certificate $crt_path$;
  ssl_certificate_key $key_path$;
  ssl_session_timeout 5m;
  ssl_protocols SSLv2 SSLv3 TLSv1 TLSv1.1 TLSv1.2;
  ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:HIGH:!aNULL:!MD5:!RC4:!DHE;
  ssl_prefer_server_ciphers on;
  ...
}
server{
  listen 80;
  server_name $web_site$;
  rewrite ^/(.*)$ https://$web_site$:443/$1 permanent;
}
```
修改后，通过以下命令校验，重新载入
```
nginx -t
nginx -s reload
```
## Tomcat与Nginx通过http连接
修改Tomcat的server.xml文件,在Connector节点中，添加proxyPort="443",redirectPorty也修改为443
```
此处443位https默认端口，可自定义
<Connector port="8080" 
           protocol="HTTP/1.1"
           connectionTimeout="20000"
           URIEncoding="UTF-8"
           redirectPort="443" 
           proxyPort="443" />
```
