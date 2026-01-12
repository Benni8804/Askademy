# 🎓 Askademy - Learning Management Platform

> A modern, full-stack learning management system featuring real-time Q&A, AI-powered question grouping, course management, and role-based access control.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

---

## 🚀 Quick Start (From ZIP Download)

### Prerequisites

Install these before starting:

| Tool | Version | Download |
|------|---------|----------|
| **Java** | 17+ | [Download JDK](https://www.oracle.com/java/technologies/downloads/) |
| **Maven** | 3.9+ | [Download Maven](https://maven.apache.org/download.cgi) |
| **Node.js** | 16+ | [Download Node.js](https://nodejs.org/) |
| **Docker** | Latest | [Download Docker](https://www.docker.com/products/docker-desktop/) |

### Step-by-Step Setup

#### 1️⃣ Extract the ZIP
```bash
# Unzip the downloaded file
unzip The_Student_Hub.zip
cd The_Student_Hub
```

#### 2️⃣ Start PostgreSQL Database
```bash
docker-compose up -d postgres
```
> This starts PostgreSQL on port **5433** with the pgvector extension for AI features.

#### 3️⃣ Start the Backend
```bash
cd backend
mvn spring-boot:run
```
✅ Backend runs on: **http://localhost:8080**

#### 4️⃣ Start the Frontend (new terminal)
```bash
cd frontend
npm install
npm start
```
✅ Frontend runs on: **http://localhost:3000**

---

## � Demo Accounts

The application starts with pre-registered accounts:

| Role | Email | Password |
|------|-------|----------|
| 👨‍🏫 **Professor** | `prof@demo.com` | `password` |
| 🎓 **Student** | `student@demo.com` | `password` |
| 🛡️ **Admin** | `admin@demo.com` | `password` |

> **💡 Tip:** Open two browser tabs to test both roles simultaneously. Each tab has its own session.

---

## ✨ Features

- 🔐 **JWT Authentication** - Secure login with role-based permissions
- 📚 **Course Management** - Create courses with unique enrollment codes
- 💬 **Q&A System** - Ask questions (optionally anonymous), answer, and verify
- 🤖 **AI Smart Grouping** - Groups similar questions using semantic embeddings
- 📢 **Announcements** - Push notifications to enrolled students
- �️ **Admin Dashboard** - View system stats and manage all content
- �🗑️ **Role-Based Deletion** - Professors can delete their courses, questions, answers
- 🎨 **Dark Mode** - Neo-brutalist UI with accessibility support

---

## 🎮 Using the Application

### As Professor (`prof@demo.com`)

1. **Create a Course** → Click "Create New Course" on dashboard
2. **Share Course Code** → Give the 8-character code to students
3. **Post Announcements** → Navigate to course → Announcements tab
4. **Answer Questions** → View student questions and provide answers
5. **Verify Answers** → Mark correct answers with verification badge
6. **AI Grouping** → Toggle "AI Smart Grouping" to batch-answer similar questions
7. **Delete Content** → Use trash icons on courses, questions, answers, announcements

### As Student (`student@demo.com`)

1. **Enroll in Course** → Enter course code on dashboard
2. **Ask Questions** → Go to course → Q&A tab → Ask Question
3. **Answer Peers** → Help fellow students by answering questions
4. **Check Notifications** → Bell icon shows recent activity

### As Admin (`admin@demo.com`)

1. **Dashboard Overview** → View real-time statistics (User/Course counts)
2. **Manage Content** → Delete any Course, Question, or Answer/Listing
3. **User Management** → View all registered users

---

## 🗂️ Project Structure

```
The_Student_Hub/
├── backend/                 # Spring Boot API
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   └── pom.xml             # Maven dependencies
├── frontend/               # React application
│   ├── src/components/     # Reusable UI components
│   ├── src/pages/          # Page components
│   └── package.json        # npm dependencies
├── docker-compose.yml      # PostgreSQL container config
└── docs/                   # Documentation
```

---

## 🧪 Running Tests

```bash
cd backend
mvn test
```

Expected output:
```
Tests run: 48, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## ⚙️ Configuration

### Application Modes

The app automatically loads demo data (sample users, courses, questions) on first run if the database is empty.

To reset to a fresh state:
```bash
docker-compose down -v
docker-compose up -d postgres
```

### Environment Variables (Optional)

Override defaults by setting these:

```bash
DB_URL=jdbc:postgresql://localhost:5433/askademy
DB_USERNAME=askademy_user
DB_PASSWORD=askademy_password
JWT_SECRET=your-secret-key-here
```

---

## 🐳 Docker Commands

```bash
# Start database
docker-compose up -d postgres

# Stop database
docker-compose down

# View logs
docker logs askademy-postgres

# Reset database (delete all data)
docker-compose down -v
docker-compose up -d postgres
```

---

## � Troubleshooting

### "Port 8080 already in use"
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux
lsof -i :8080
kill -9 <PID>
```

### "Connection refused to database"
Make sure Docker is running and PostgreSQL container is up:
```bash
docker ps
# Should show "askademy-postgres" container
```

### "npm install fails"
Delete `node_modules` and try again:
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Backend** | Spring Boot 3.2, Java 17, Spring Security, JPA/Hibernate |
| **Frontend** | React 18, React Router, Axios, Tailwind CSS |
| **Database** | PostgreSQL 15 with pgvector extension |
| **AI** | Sentence embeddings for semantic question grouping |
| **Auth** | JWT tokens with BCrypt password hashing |

---




