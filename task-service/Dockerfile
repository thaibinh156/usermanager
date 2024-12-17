FROM eclipse-temurin:11-jdk-alpine AS build

WORKDIR /opt

RUN /opt/java/openjdk/bin/jlink \
    --add-modules ALL-MODULE-PATH \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /opt/jdk

FROM alpine:latest

COPY --from=build /opt/jdk /opt/jdk

ENV JAVA_HOME=/opt/jdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

LABEL maintainer="binh.thaidiep.com"

COPY target/task-service-0.0.1.jar task-service-0.0.1.jar

ENTRYPOINT ["java", "-jar", "/task-service-0.0.1.jar"]