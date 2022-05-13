

## 前言

Local Debug 为提升开发测试环境调试效率而生， 让调试变得更简单。

为什么需要 Local Debug？ 通过Local Debug, 开发者可以本地单独启动需要开发的服务，接受线上的回放请求去直接去调试，省略了需要发版到线上的时间。

以下是 Local Debug 的工作流程：

## 工作流程

![img.png](img.png)

1. 线上接受到请求，在处理真正的逻辑前做拦截，转发给 隧道服务(debugger-server)
2. 隧道服务(debugger-server) 查找注册表， 找到与线上匹配的对应本地服务
3. 将请求转发给本地的隧道服务客户端(debugger-client)
4. 本地重新回放整个请求
5. 本地处理请求过程中，能正常调用线上服务(这块这次先不处理，因为dice环境下本地是能调用线上的)


## 使用说明


使用过程主要是了以下2步：
1. 使用前需要部署debug server 服务端， 用于将线上的请求回放到本地；
2. 远程服务和本地服务的业务模块加上依赖和添加配置便可


### 部署 debugger server 服务端

debugger 服务端负责将请求转发到本地， 所以跑localdebug的时候需要部署一个debugger server, 也可以用公用环境的。
对应镜像是这个 `registry.erda.cloud/trantor/debugger-server:1.0`



可用的配置如下：

| 变量名         | 含义 | 默认值 |
|-------------|----|---- |
| HTTP_PORT   | debug server 暴露的http端口 | 8080 |
| TUNNEL_PORT | debug server 暴露的隧道透传端口 | 7000 |


### 业务模块添加依赖


添加对 debugger-client的依赖
```xml
<dependency>
    <groupId>io.terminus.debugger</groupId>
    <artifactId>debugger-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <!-- 避免传递性依赖 -->
    <optional>true</optional>
</dependency>
```

另外考虑localdebug 只是在 开发、测试环境使用，可以考虑使用 maven 的 profile 做打包,  这样打包的时候不加上 -P debug , 默认都不会带上该 jar， 降低生产环境依赖的时候打包比较有用。

```xml
<profiles>
  <profile>
    <id>debug</id>
    <dependencies>
      <dependency>
        <groupId>io.terminus.debugger</groupId>
        <artifactId>debugger-client</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <optional>true</optional>
      </dependency>
    </dependencies>
  </profile>
</profiles>
```


### 添加配置

业务模块应用启动的时候加上环境配置, **需要重点注意的是远程服务和本地服务配置不同点在于 local 这个值**

```yaml
# application 的名称是必须的，用来标识注册到debugServer的该应用
spring:
  application:
    name: helloApplication
        
terminus:
  # debug 相关配置，本地和远程都要配置
  localdebug:
    # 是否开启debug，本地和远程服务都配置
    enable: true 
    # debug server 的地址
    server-host: 127.0.0.1 
    # debug server 的暴露的http端口
    http-port: 8080
    # debug server 的暴露的隧道透传端口
    tunnel-port: 7000
    # 当前服务是不是本地用于接受debug请求的服务，本地启动为true，默认为false
    local: true
    # 配置自定义debugKey,需保证全局唯一, 可不配置， 默认用网卡的 mac 地址
    # debugKey: xxx
```

需要本地测试可以 启动 debugger-server-starter, 和 example-application 的 RemoteApplication 和 LocalApplication。
其中 RemoteApplication 表示远程应用， LocalApplication 表示本地正在开发的应用， 这样所有对远程应用的请求， 都会直接路由到本地， 
大幅度缩减了本地开发过程中需要发版到线上联调的时间。


## 后续规划
- 目前仅支持feign的rpc，后续规划支持dubbo。
- `Debugger Server` 也可以做代理转发一些线上请求，避免客户端需要依赖很多中间件和基础服务。
- 简化接入方式，不用运行时对localdebug jar的强依赖， 或者可以agent的接入方式， 想 arthas那样。
