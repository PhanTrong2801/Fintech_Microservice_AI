# Sử dụng Java 21 tương đồng với các service khác
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy file .jar từ thư mục target vào image
COPY target/*.jar app.jar
# Eureka Server chạy ở cổng 8761
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]