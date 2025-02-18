# OpenJDK 17 사용
FROM openjdk:17

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# JAR_FILE 변수 선언
ARG JAR_FILE=build/libs/*.jar

# 애플리케이션 JAR 복사
COPY ${JAR_FILE} /app.jar

# ENTRYPOINT 설정 (Pinpoint 적용)
ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar /app.jar"]