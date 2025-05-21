# 📝 Java To-Do List Manager with Email Reminders

This is a desktop-based To-Do List application built with Java Swing. It allows users to add, delete, and mark tasks as completed, with due dates and priorities. The application also supports automated **email reminders** based on a user-specified number of days before the task's deadline.

---

## 📌 Features

- Add, delete, and mark tasks as done
- Set **due dates** and **priority levels** (High, Medium, Low)
- Set reminder timing (e.g., 2 days before deadline)
- Receive **email reminders** via Gmail
- Persistent storage using file-based saving (`tasks.txt`)
- Automatic sorting by due date and priority
- Minimal and intuitive GUI built using Java Swing

---

## 🚀 Technologies Used

- Java (JDK 8+)
- Java Swing (for GUI)
- Jakarta Mail (for email functionality)
- File I/O for persistence

---

## 🖥️ How to Run

### 🔧 Prerequisites

- Java installed (JDK 8 or above)
- Jakarta Mail JAR: [`jakarta.mail-2.0.1.jar`](https://repo1.maven.org/maven2/com/sun/mail/jakarta.mail/2.0.1/)
- Internet connection (for sending emails)

### 🗂 Folder Structure

```markdown
ToDoListProject/
├── EmailUtil.java
├── Task.java
├── ToDoListApp.java
├── lib/
│ └── jakarta.mail-2.0.1.jar
├── tasks.txt (auto-created)
```

### 🛠️ Compile

**Windows:**
```bash
javac -cp ".;lib/jakarta.mail-2.0.1.jar" EmailUtil.java Task.java ToDoListApp.java
```

### ▶️ Run

**Windows:**
```bash
java -cp ".:lib/jakarta.mail-2.0.1.jar" ToDoListApp
```
