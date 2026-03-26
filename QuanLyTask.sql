IF EXISTS (SELECT name FROM sys.databases WHERE name = 'quan_ly_task')
    DROP DATABASE quan_ly_task;
GO
 
CREATE DATABASE quan_ly_task;
GO
 
USE quan_ly_task;
GO
 
 
-- ================================================
-- BƯỚC 2: TẠO BẢNG
-- ------------------------------------------------
-- Thứ tự: roles → users → projects → tasks → user_roles
-- ================================================
 
CREATE TABLE roles (
    id   VARCHAR(10) NOT NULL,
    name VARCHAR(20) NOT NULL,  -- USER hoặc MANAGER
    PRIMARY KEY (id)
);
GO
 
CREATE TABLE users (
    id       VARCHAR(20)   NOT NULL,
    name     NVARCHAR(100) NOT NULL,
    email    VARCHAR(100)  NOT NULL,
    password VARCHAR(255)  NOT NULL DEFAULT '',
 
    PRIMARY KEY (id),
    UNIQUE (email)
);
GO
 
CREATE TABLE projects (
    id   VARCHAR(10)   NOT NULL,
    name NVARCHAR(100) NOT NULL,
 
    PRIMARY KEY (id)
);
GO
 
CREATE TABLE tasks (
    id          VARCHAR(20)   NOT NULL,
    title       NVARCHAR(200) NOT NULL,
    status      VARCHAR(20)   NOT NULL DEFAULT 'TODO',
    assignee_id VARCHAR(20)   DEFAULT NULL,
    project_id  VARCHAR(10)   NOT NULL,
    deadline    DATE          NULL,
 
    PRIMARY KEY (id),
 
    FOREIGN KEY (assignee_id) REFERENCES users(id),
    FOREIGN KEY (project_id)  REFERENCES projects(id),
 
    CONSTRAINT chk_status CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE'))
);
GO
 
CREATE TABLE user_roles (
    user_id VARCHAR(20) NOT NULL,
    role_id VARCHAR(10) NOT NULL,
 
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
GO
 
-- Index giúp query nhanh hơn
CREATE INDEX idx_status   ON tasks (status);
CREATE INDEX idx_assignee ON tasks (assignee_id);
CREATE INDEX idx_project  ON tasks (project_id);
GO
 
 
-- ================================================
-- BƯỚC 3: THÊM DỮ LIỆU TEST
-- ================================================
 
-- 2 roles
INSERT INTO roles VALUES
    ('r1', 'USER'),
    ('r2', 'MANAGER');
GO
 
-- 5 users (password để trống, sẽ hash bằng BCrypt khi register qua API)
INSERT INTO users (id, name, email, password) VALUES
    ('u1', N'Thiệp', 'thiep@gmail.com', ''),
    ('u2', N'Hoàng', 'hoang@gmail.com', ''),
    ('u3', N'Châu',  'chau@gmail.com',  ''),
    ('u4', N'Dũng',  'dung@gmail.com',  ''),
    ('u5', N'An',    'an@gmail.com',    '');
GO
 
-- Gán role USER cho tất cả
INSERT INTO user_roles VALUES
    ('u1', 'r1'),
    ('u2', 'r1'),
    ('u3', 'r1'),
    ('u4', 'r1'),
    ('u5', 'r1');
GO
 
-- 3 projects
INSERT INTO projects VALUES
    ('p1', N'Website Redesign'),
    ('p2', N'Mobile App'),
    ('p3', N'API Backend');
GO
 
-- 30 tasks
INSERT INTO tasks (id, title, status, assignee_id, project_id) VALUES
    ('t1',  N'Thiết kế homepage',    'IN_PROGRESS', 'u1', 'p1'),
    ('t2',  N'Viết API login',       'DONE',        'u2', 'p3'),
    ('t3',  N'Tạo database schema',  'DONE',        'u3', 'p3'),
    ('t4',  N'Thiết kế màn login',   'IN_PROGRESS', 'u4', 'p2'),
    ('t5',  N'Fix bug header',       'TODO',        'u1', 'p1'),
    ('t6',  N'Viết API register',    'IN_PROGRESS', 'u2', 'p3'),
    ('t7',  N'Thiết kế footer',      'DONE',        'u3', 'p1'),
    ('t8',  N'Setup CI/CD',          'IN_PROGRESS', 'u4', 'p3'),
    ('t9',  N'Viết unit test',       'TODO',        'u5', 'p3'),
    ('t10', N'Thiết kế màn home',    'IN_PROGRESS', 'u1', 'p2'),
    ('t11', N'Tối ưu query DB',      'DONE',        'u2', 'p3'),
    ('t12', N'Làm trang About',      'IN_PROGRESS', 'u3', 'p1'),
    ('t13', N'Push notification',    'TODO',        'u4', 'p2'),
    ('t14', N'Viết API task',        'IN_PROGRESS', 'u5', 'p3'),
    ('t15', N'Review code',          'DONE',        'u1', 'p1'),
    ('t16', N'Thiết kế icon app',    'IN_PROGRESS', 'u2', 'p2'),
    ('t17', N'Viết docs API',        'IN_PROGRESS', 'u3', 'p3'),
    ('t18', N'Test trên iOS',        'DONE',        'u4', 'p2'),
    ('t19', N'Test trên Android',    'IN_PROGRESS', 'u5', 'p2'),
    ('t20', N'Deploy lên server',    'TODO',        'u1', 'p3'),
    ('t21', N'Làm trang Contact',    'IN_PROGRESS', 'u2', 'p1'),
    ('t22', N'Viết API project',     'DONE',        'u3', 'p3'),
    ('t23', N'Làm splash screen',    'IN_PROGRESS', 'u4', 'p2'),
    ('t24', N'Backup database',      'DONE',        'u5', 'p3'),
    ('t25', N'Cấu hình nginx',       'TODO',        'u1', 'p3'),
    ('t26', N'Làm trang 404',        'IN_PROGRESS', 'u2', 'p1'),
    ('t27', N'Viết API user',        'DONE',        'u3', 'p3'),
    ('t28', N'Thiết kế onboarding',  'IN_PROGRESS', 'u4', 'p2'),
    ('t29', N'Load testing',         'TODO',        'u5', 'p3'),
    ('t30', N'Làm dark mode',        'IN_PROGRESS', NULL, 'p2');
GO
 
 
-- ================================================
-- BƯỚC 4: KIỂM TRA DỮ LIỆU
-- ================================================
 
SELECT 'roles'      AS bang, COUNT(*) AS so_luong FROM roles
UNION ALL
SELECT 'users'      AS bang, COUNT(*) AS so_luong FROM users
UNION ALL
SELECT 'projects'   AS bang, COUNT(*) AS so_luong FROM projects
UNION ALL
SELECT 'tasks'      AS bang, COUNT(*) AS so_luong FROM tasks
UNION ALL
SELECT 'user_roles' AS bang, COUNT(*) AS so_luong FROM user_roles;
GO
 
-- Task chưa giao cho ai
SELECT id, title FROM tasks WHERE assignee_id IS NULL;
GO
 
 
-- ================================================
-- BƯỚC 5: QUERY TASK THEO USER
-- ================================================
 
SELECT
    u.name   AS ten_user,
    t.title  AS ten_task,
    t.status AS trang_thai,
    p.name   AS ten_project
FROM tasks t
JOIN users    u ON t.assignee_id = u.id
JOIN projects p ON t.project_id  = p.id
WHERE u.id = 'u1'
ORDER BY t.status;
GO
 
 
-- ================================================
-- BƯỚC 6: QUERY TASK THEO PROJECT
-- ================================================
 
SELECT
    p.name   AS ten_project,
    t.title  AS ten_task,
    t.status AS trang_thai,
    u.name   AS nguoi_phu_trach
FROM tasks t
JOIN      projects p ON t.project_id  = p.id
LEFT JOIN users    u ON t.assignee_id = u.id
WHERE p.id = 'p1'
ORDER BY t.status;
GO
 
 
-- ================================================
-- BƯỚC 7: QUERY TASK THEO STATUS
-- ================================================
 
SELECT
    t.title  AS ten_task,
    u.name   AS nguoi_phu_trach,
    p.name   AS ten_project
FROM tasks t
LEFT JOIN users    u ON t.assignee_id = u.id
JOIN      projects p ON t.project_id  = p.id
WHERE t.status = 'IN_PROGRESS'
ORDER BY p.name;
GO
 
 
-- ================================================
-- BƯỚC 8: REVIEW & TỐI ƯU QUERY
-- Nhấn Ctrl + M để xem Execution Plan
-- Index Seek = tốt | Table Scan = chậm
-- ================================================
 
SELECT * FROM tasks WHERE status      = 'IN_PROGRESS';
SELECT * FROM tasks WHERE assignee_id = 'u1';
SELECT * FROM tasks WHERE project_id  = 'p1';
GO
 Select * from users