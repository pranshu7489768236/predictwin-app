FROM eclipse-temurin:11-jre

# Use non-root user for security
RUN addgroup --system appgroup && adduser --system appuser && mkdir /app && chown appuser:appgroup /app
USER appuser
WORKDIR /app

ARG JAR_FILE=target/predictwin-backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
