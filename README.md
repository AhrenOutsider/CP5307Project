# CP5307Project – Mobile Learning App

## Overview
CP5307Project is a mobile learning application developed using **Android Studio** and **Jetpack Compose**.  
The app provides users with an interactive platform to explore different subjects, browse courses, and track their learning progress.

This project demonstrates modern Android development practices including **Material 3 UI, Compose Navigation logic, and state management**.

---

## Features

### Home
- Explore available learning subjects
- Browse popular courses
- View recommended learning content
- Access promotional courses through the banner section

### Subjects
- AI
- Machine Learning
- Deep Learning
- Coding

Users can select a subject to view related courses.

### Course Browsing
Each subject contains multiple courses with:
- Course title
- Description
- Cover image

Users can select a course to enter the **course watching page**.

### Course Watching
- Course introduction
- Lesson list
- Start watching button

### Learning
Track your personal learning progress including:
- Courses in progress
- Completed courses
- Progress indicators

### Search
Search functionality allows users to quickly find learning topics.

### Settings
User settings include:
- Notifications
- Dark mode
- Language selection
- Learning preferences

---

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Design System:** Material 3
- **IDE:** Android Studio
- **Architecture:** Compose-based UI structure

---

## Project Structure
CP5307Project
│
├── MainActivity.kt
├── Home Screen
│ ├── Banner Promotion
│ ├── Subject Section
│ ├── Popular Courses
│ └── Recommended Courses
│
├── Course Screens
│ ├── Subject Course List
│ └── Course Watch Page
│
├── Learning Screen
│ ├── Progress Tracking
│ └── Course Details
│
├── Search Screen
│
└── Settings Screen
