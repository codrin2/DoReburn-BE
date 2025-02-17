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

# 환경 변수 설정 (Docker 내부에서 사용)
ENV JAVA_OPTS="-javaagent:/pinpoint-agent/pinpoint-bootstrap-2.3.3-NCP-RC3.jar \
    -Dpinpoint.applicationName=doreburn \
    -Dpinpoint.agentId=admin"

# ENTRYPOINT 설정 (Pinpoint 적용)
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-javaagent:/pinpoint-agent/pinpoint-bootstrap-2.3.3-NCP-RC3.jar", "/app.jar"]