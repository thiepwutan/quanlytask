// ================================
// 1. ENUM - trạng thái của task
// ================================
const TaskStatus = {
  TODO: "TODO",
  IN_PROGRESS: "IN_PROGRESS",
  DONE: "DONE",
  CANCELLED: "CANCELLED"
};

// ================================
// 2. CLASS USER
// ================================
class User {
  constructor(id, name, email) {
    this.id = id;
    this.name = name;
    this.email = email;

    // validate luôn khi tạo
    if (!this.name) throw new Error("Tên không được để trống");
    if (!this.email.includes("@")) throw new Error("Email không hợp lệ");
  }
}

// ================================
// 3. CLASS TASK
// ================================
class Task {
  constructor(id, title, projectId) {
    this.id = id;
    this.title = title;
    this.projectId = projectId;
    this.status = TaskStatus.TODO; // mặc định khi mới tạo
    this.assigneeId = null;        // chưa giao cho ai
  }

  updateStatus(newStatus) {
    if (!Object.values(TaskStatus).includes(newStatus)) {
      throw new Error(`Status "${newStatus}" không hợp lệ`);
    }
    this.status = newStatus;
  }
}

// ================================
// 4. CLASS PROJECT
// ================================
class Project {
  constructor(id, name) {
    this.id = id;
    this.name = name;
    this.tasks = [];
  }

  addTask(task) {
    if (this.tasks.find(t => t.id === task.id)) {
      throw new Error(`Task "${task.id}" đã tồn tại trong project`);
    }
    this.tasks.push(task);
  }

  updateTask(taskId, newTitle) {
    const task = this.tasks.find(t => t.id === taskId);
    if (!task) throw new Error(`Không tìm thấy task "${taskId}"`);
    task.title = newTitle;
  }

  deleteTask(taskId) {
    const index = this.tasks.findIndex(t => t.id === taskId);
    if (index === -1) throw new Error(`Không tìm thấy task "${taskId}"`);
    this.tasks.splice(index, 1);
  }
}

// ================================
// 5. CLASS TASKMANAGER
// ================================
class TaskManager {
  constructor() {
    this.users = [];
    this.projects = [];
  }

  addUser(user) {
    if (this.users.find(u => u.email === user.email)) {
      throw new Error(`Email "${user.email}" đã tồn tại`);
    }
    this.users.push(user);
  }

  addProject(project) {
    this.projects.push(project);
  }

  assignTask(task, userId) {
    const user = this.users.find(u => u.id === userId);
    if (!user) throw new Error(`Không tìm thấy user "${userId}"`);
    task.assigneeId = userId;
  }
}

// ================================
// 6. CHẠY THỬ + GHI LOG
// ================================
console.log("===== CHẠY CHƯƠNG TRÌNH =====\n");

try {
  const manager = new TaskManager();

  // Tạo user
  const u1 = new User("u1", "An", "an@gmail.com");
  const u2 = new User("u2", "Bình", "binh@gmail.com");
  manager.addUser(u1);
  manager.addUser(u2);
  console.log("Đã thêm 2 user:", u1.name, ",", u2.name);

  // Tạo project
  const p1 = new Project("p1", "Website Redesign");
  manager.addProject(p1);
  console.log("Đã thêm project:", p1.name);

  // Tạo task và thêm vào project
  const t1 = new Task("t1", "Thiết kế homepage", "p1");
  const t2 = new Task("t2", "Viết API login", "p1");
  p1.addTask(t1);
  p1.addTask(t2);
  console.log("Đã thêm 2 task vào project");

  // Gán task cho user
  manager.assignTask(t1, "u1");
  manager.assignTask(t2, "u2");
  console.log("Đã gán task cho user");

  // Đổi trạng thái
  t1.updateStatus(TaskStatus.IN_PROGRESS);
  t2.updateStatus(TaskStatus.DONE);
  console.log("Đã cập nhật trạng thái task");

  // Sửa task
  p1.updateTask("t1", "Thiết kế homepage v2");
  console.log("Đã sửa tên task t1");

  // Xóa task
  p1.deleteTask("t2");
  console.log("Đã xóa task t2");

  // Kết quả cuối
  console.log("\n===== KẾT QUẢ =====");
  console.log("Số task còn lại:", p1.tasks.length);
  p1.tasks.forEach(t => {
    console.log(`  Task: ${t.title} | Status: ${t.status} | Giao cho: ${t.assigneeId}`);
  });

} catch (err) {
  console.error("Lỗi:", err.message);
}

// Test bắt lỗi
console.log("\n===== TEST LỖI =====");

try {
  const manager2 = new TaskManager();
  manager2.addUser(new User("u1", "An", "an@gmail.com"));
  manager2.addUser(new User("u2", "Fake", "an@gmail.com")); // email trùng
} catch (err) {
  console.error("Lỗi email trùng:", err.message);
}

try {
  const t = new Task("t1", "Test", "p1");
  const manager3 = new TaskManager();
  manager3.assignTask(t, "u-ao"); // user không tồn tại
} catch (err) {
  console.error("Lỗi user không tồn tại:", err.message);
}