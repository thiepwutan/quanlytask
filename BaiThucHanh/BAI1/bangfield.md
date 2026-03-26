# Bảng Entity & Field

## User

| Field | Kiểu   | Mô tả                          |
| ----- | ------ | ------------------------------ |
| id    | string | Mã định danh, VD: "u1"         |
| name  | string | Tên người dùng, không để trống |
| email | string | Email, phải có dấu @           |

## Task

| Field      | Kiểu   | Mô tả                                      |
| ---------- | ------ | ------------------------------------------ |
| id         | string | Mã định danh, VD: "t1"                     |
| title      | string | Tên công việc                              |
| status     | string | TODO/ IN_PROGRESS / DONE / CANCELLED       |
| assigneeId | string | Id của User được giao (null nếu chưa giao) |
| projectId  | string | Id của Project chứa task này               |

## Project

| Field | Kiểu   | Mô tả                    |
| ----- | ------ | ------------------------ |
| id    | string | Mã định danh, VD: "p1"   |
| name  | string | Tên dự án                |
| tasks | Array  | Danh sách Task bên trong |
