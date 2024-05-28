# Simple DNS Server
简单的DNS服务器，测试用！！！  

使用UDP协议监听53端口 
解析收到的DNS请求报文，提取domain，对比配置文件  
如果和配置文件中的域名匹配则返回配置的IP地址  
否则使用系统解析返回真实的IP地址

## 使用 (Usage)
使用默认配置文件
```
java -jar simple-dns-server.jar
```
指定配置文件
```
java -jar simple-dns-server.jar test.conf
```

启用debug模式，输出请求和响应报文
```
java -DenableDebug=true -jar simple-dns-server.jar
```

控制台输出：
```
################################
####    Simple DNS Server   ####
################################
Use default profile！
Read configuration file: D:\tools\DNS\Simple-DNS-Server\dns_resolve.conf
Configuration file contents:
- 10.10.10.2  test.com
- 192.168.0.100  example.com

Start service ...

```

## 配置文件 (Config file)
格式同host文件
默认的配置文件名称为：dns_resolve.conf
```
# 一个简单的配置文件，同host文件
# 用来存储域名和IP地址，每行一个IP地址和域名，用空格分割
# #号表示注释
# 格式如下：
# 142.250.179.202 www.google.com

192.168.0.100       example.com
10.10.10.2       test.com
```

## 验证 (Test)
命令行使用nslookup 并指定dns服务器地址进行测试
```
C:\Users\Administrator>nslookup example.com  127.0.0.1
服务器:  1.0.0.127.in-addr.arpa
Address:  127.0.0.1

非权威应答:
名称:    example.com
Addresses:  192.168.0.100
          192.168.0.100
```

程序控制台会输出：
```
Receive a DNS request：1.0.0.127.in-addr.arpa
Return system analysis results：127.0.0.1
Receive a DNS request：example.com
Match from configuration file：192.168.0.100
Receive a DNS request：example.com
Match from configuration file：192.168.0.100

```