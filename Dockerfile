FROM public.ecr.aws/docker/library/openjdk:11
#openjdk:11-slim
#openjdk:11.0.11-jdk-slim

COPY ./target/ccbi-0.0.1-SNAPSHOT.jar /usr/src/ccbi-0.0.1-SNAPSHOT.jar
WORKDIR usr/src
#ENTRYPOINT ["java", "-jar", "ccbi-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=dev"]
#ENTRYPOINT ["rabbitmq-server", "&"]

# Run the web service on container startup.
CMD ["java","-jar","ccbi-0.0.1-SNAPSHOT.jar","--spring.profiles.active=dev"]