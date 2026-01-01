# Shared Academic Calendar Mobile Application

## 📌 Project Overview
The **Shared Academic Calendar Mobile Application** is a cross-platform mobile system designed to provide a **centralized and unified academic scheduling platform** for the university community.  
It enables students, lecturers, and administrative staff to access **real-time academic schedules, lecture timings, lab sessions, and important deadlines** through their mobile devices.

The system aims to improve **operational efficiency**, **communication**, and **student engagement** by ensuring accurate and timely academic information.

---

## 🎯 Project Objectives
- Provide a single, reliable source for academic schedules
- Enable role-based access for Students, Lecturers, and Admin staff
- Deliver real-time updates and notifications
- Support offline access to schedules
- Improve coordination between academic stakeholders

---

## 🧩 System Architecture
The project follows a **client–server architecture**:

- **Frontend**: Flutter mobile application (Android & iOS)
- **Backend**: Spring Boot REST API (Java 17)
- **Database**: PostgreSQL (Supabase)
- **Notifications**: Supabase Realtime & native push notifications

---

## 🛠️ Technology Stack

### Frontend
- Flutter (Cross-platform mobile development)
- Dart
- Android & iOS support

### Backend
- Java 17
- Spring Boot (3.x)
- Maven (Wrapper included)

### Database & Services
- PostgreSQL
- Supabase (Realtime & Backend Services)

---

## 📄 About `setup.bat`

`setup.bat` is a **Windows batch script** located at the root of the repository.  
It automates the dependency installation process by executing the standard build and package-management commands for each component of the system.

### The script performs the following tasks:
1. Navigates to the **backend** directory and installs all Spring Boot dependencies using the **Maven Wrapper** (Java 17).
2. Navigates to the **Flutter frontend** directory and downloads all required Flutter packages.
3. Ensures a consistent and reproducible development environment for all team members.

---

## ▶️ How to Use `setup.bat`

### Step 1: Open Command Prompt
Navigate to the root directory of the project.

### Step 2: Run the setup script
```bash
setup.bat

