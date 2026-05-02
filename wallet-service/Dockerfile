 # Sử dụng môi trường Java 21 siêu nhẹ
FROM eclipse-temurin:21-jre-alpine

# Khai báo thư mục làm việc bên trong container
WORKDIR /app

# Copy file .jar từ máy tính vào trong container và đổi tên thành app.jar
COPY target/*.jar app.jar

# Mở cổng 8083
EXPOSE 8083

# Lệnh khởi động ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]