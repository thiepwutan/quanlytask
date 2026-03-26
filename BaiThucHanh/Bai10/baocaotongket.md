# Báo cáo tổng kết — Hệ thống quản lý Task

## 1. Tổng quan dự án
Xây dựng REST API quản lý Task theo mô hình OOP + Spring Boot,
kết nối SQL Server, có xác thực JWT và phân quyền theo role.

## 2. Những gì đã làm được

### Tuần 1 — OOP JavaScript
- Xây dựng các class: User, Task, Project, TaskManager
- Áp dụng nguyên tắc Single Responsibility
- Xử lý lỗi với try/catch

### Tuần 2 — Database Design
- Thiết kế ERD: 3 bảng chính + quan hệ FK
- Viết SQL tạo bảng, insert 30 records test
- Viết query theo user, project, status

### Tuần 3 — Spring Boot cơ bản
- Khởi tạo project với Spring Initializr
- Cấu hình kết nối SQL Server
- Viết UserEntity, UserRepository, UserService, UserController

### Tuần 4 — JPA Mapping
- Mapping quan hệ @ManyToOne, @OneToMany
- Fix lỗi lazy loading với @JsonIgnoreProperties
- Viết API cho Task và Project

### Tuần 5 — Business Logic
- Enum TaskStatus: TODO → IN_PROGRESS → DONE
- Validate projectId khi tạo task
- Chặn update task đã DONE

### Tuần 6 — Validation + Exception
- Thêm @NotBlank, @Size, @Future
- Tạo NotFoundException, BadRequestException
- Chuẩn hóa response với ApiResponse<T>

### Tuần 7 — JWT Security
- Thêm bảng roles, user_roles
- Hash password với BCrypt
- Tạo JwtUtil, JwtFilter, SecurityConfig
- Phân quyền: MANAGER tạo project, USER xem task

### Tuần 9 — Deploy + Docs
- Tách profile dev/prod
- Build JAR, run local
- Tích hợp Swagger UI
- Viết README

## 3. Kiến thức học được
- OOP: class, constructor, inheritance, encapsulation
- SQL: DDL, DML, JOIN, FK, index
- Spring Boot: IoC, DI, REST API, JPA
- Security: JWT, BCrypt, role-based authorization
- Công cụ: Git, Postman, Swagger, IntelliJ

## 4. Khó khăn gặp phải
- Lỗi lazy loading khi convert JPA entity sang JSON
- JWT filter không bắt đúng exception → vào handler 500
- SQL Server reserved keyword "user" gây lỗi query
- VARCHAR(10) quá ngắn cho id sinh tự động

## 5. Hướng cải thiện
- Thêm unit test với JUnit
- Dùng DTO thay vì trả thẳng Entity
- Thêm refresh token
- Deploy lên cloud (Railway, Render)