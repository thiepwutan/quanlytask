# Demo script — Tuần 10

## Chuẩn bị trước khi demo

- [ ] Chạy app: `java -jar quanlytask.jar`
- [ ] Mở Postman, đã có sẵn các request
- [ ] Mở Swagger: http://localhost:8080/swagger-ui.html
- [ ] Mở SSMS kiểm tra DB đang có data

## Thứ tự demo (15 phút)

### Phần 1 — Giới thiệu kiến trúc (2 phút)

- [ ] Chỉ vào sơ đồ: Client -> Controller -> Service -> Repository -> DB
- [ ] Giải thích 3 layer: Controller nhận request, Service xử lý logic, Repository làm việc với DB

### Phần 2 — Demo Register + Login (3 phút)

- [ ] POST /api/auth/register -> tạo tài khoản MANAGER
- [ ] POST /api/auth/register -> tạo tài khoản USER
- [ ] POST /api/auth/login -> lấy token
- [ ] Giải thích token trả về là JWT, gồm header.payload.signature

### Phần 3 — Demo CRUD Task (5 phút)

- [ ] POST /api/tasks?projectId=p1&title=Demo task -> tạo task mới
- [ ] PUT /api/tasks/{id}/assign?userId=u1 -> gán task cho user
- [ ] PUT /api/tasks/{id}/status?newStatus=IN_PROGRESS → đổi status
- [ ] PUT /api/tasks/{id}/status?newStatus=DONE -> hoàn thành
- [ ] PUT /api/tasks/{id}/status?newStatus=IN_PROGRESS → bị chặn (business rule)

### Phần 4 — Demo phân quyền (3 phút)

- [ ] POST /api/projects + token USER -> 403 Forbidden
- [ ] POST /api/projects + token MANAGER -> 200 OK
- [ ] Giải thích SecurityConfig.hasRole()

### Phần 5 — Q&A chuẩn bị (2 phút)

- [ ] JPA là gì? -> ORM, map class Java với bảng DB, không cần viết SQL thủ công
- [ ] JWT là gì? -> token tự chứa thông tin user, server không cần lưu session
- [ ] Tại sao dùng BCrypt? -> hash 1 chiều, không thể reverse
- [ ] LAZY vs EAGER? -> LAZY chỉ load khi cần, EAGER load hết luôn
