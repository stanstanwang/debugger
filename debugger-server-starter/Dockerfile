FROM registry.cn-hangzhou.aliyuncs.com/terminus/terminus-openjdk8-node-maven:1.0.4
EXPOSE 8080
WORKDIR /
COPY ./target/debugger-server-starter-0.0.1-SNAPSHOT.jar /app/app.jar


RUN echo "Asia/shanghai" > /etc/timezone;
CMD java $JAVA_OPTS $CATALINA_OPTS -jar /app/app.jar

# docker build -t registry.erda.cloud/trantor/debugger-server:1.0 .
