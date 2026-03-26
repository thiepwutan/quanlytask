# Quản Lý Task API

REST API quản lý công việc theo project — Spring Boot + JWT + SQL Server.

## Yêu cầu cài đặt

- JDK 17
- Maven 3.8+
- SQL Server (đang chạy ở localhost:1433)

---

## Cách chạy project

### Bước 1 — Clone code về

git clone https://github.com/thiepwutan/quanlytask.git
cd quanlytask

### Bước 2 — Tạo database

Mở SQL Server Management Studio, chạy file:
`src/main/resources/init.sql`

### Bước 3 — Sửa file cấu hình

Mở file: `src/main/resources/application-dev.properties`

Sửa lại password cho đúng máy của bạn:
```
spring.datasource.password=PASSWORD_CUA_BAN
```

### Bước 4 — Chạy
```bash
mvn spring-boot:run
```

App chạy tại: http://localhost:8080

---

## Hướng dẫn test API

Mở Swagger UI tại: **http://localhost:8080/swagger-ui/index.html**

### 1. Đăng ký tài khoản
```
POST /api/auth/register
{
  "name": "Tên của bạn",
  "email": "email@gmail.com",
  "password": "123456",
  "role": "USER"
}
```

### 2. Đăng nhập lấy token
```
POST /api/auth/login
{
  "email": "email@gmail.com",
  "password": "123456"
}
```

Copy token từ response.

### 3. Gắn token vào Swagger

Click nút **Authorize** (🔒) → paste token → Authorize.

### 4. Test các API

Tất cả API đều có thể test trực tiếp trên Swagger UI.

---

## Danh sách API

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | /api/auth/register | Đăng ký |
| POST | /api/auth/login | Đăng nhập → lấy JWT |
| GET | /api/tasks | Lấy tất cả task |
| POST | /api/tasks | Tạo task mới |
| PUT | /api/tasks/{id}/assign | Giao task cho user |
| PUT | /api/tasks/{id}/status | Cập nhật trạng thái |
| GET | /api/tasks/project/{id} | Task theo project |
| GET | /api/tasks/user/{id} | Task của một user |
| GET | /api/projects | Lấy tất cả project |
| POST | /api/projects | Tạo project |
| GET | /api/users | Lấy tất cả user |
| DELETE | /api/tasks/{id} | Xóa task |

---

## Chạy Unit Test
```bash
mvn test
```
