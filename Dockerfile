# Docker 镜像构建
FROM public.ecr.aws/docker/library/openjdk:11
FROM rabbitmq:3.13.3-management
#openjdk:11-slim
#openjdk:11.0.11-jdk-slim

# Copy local code to the container image.
#COPY pom.xml .
#COPY src ./src
COPY ./target/ccbi-0.0.1-SNAPSHOT.jar /app/target/ccbi-0.0.1-SNAPSHOT.jar
WORKDIR /app

# Run the web service on container startup.
CMD ["java","-jar","/app/target/ccbi-0.0.1-SNAPSHOT.jar","--spring.profiles.active=dev"]
# 运行 RabbitMQ 服务
CMD ["rabbitmq-server"]