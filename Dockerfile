FROM maven:3.6-openjdk-11-slim as builder
ADD src /usr/src/app/src
ADD pom.xml /usr/src/app
WORKDIR /usr/src/app
RUN mvn clean install

FROM ghcr.io/senergy-platform/analytics-operator-base:latest
ENV NAME flex-threshold
COPY --from=builder /usr/src/app/target/operator-${NAME}-jar-with-dependencies.jar /opt/operator.jar
