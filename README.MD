# 📝 TODO App

A simple and modern Android To-Do application built with the latest Android development tools. It allows users to manage their **projects** and **tasks** efficiently.

## ✨ Features

* **User Authentication** powered by Firebase Authentication.
* **Project and Task Management** — create, view, and organize your work easily.
* **Real-time Synchronization** with Firebase Cloud Firestore.
* **Clean, modern UI** built entirely with Jetpack Compose.

## 🛠️ Tech Stack & Libraries

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
* **Architecture:** MVI (Model–View–Intent) + Clean Architecture
* **Asynchronous & Reactive Programming:** Kotlin Coroutines and Flow
* **Dependency Injection:** [Koin](https://insert-koin.io/)
* **Navigation:** [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation)
* **Backend:** [Firebase](https://firebase.google.com/)

    * Authentication
    * Cloud Firestore
    * Analytics
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/)
* **Serialization:** [Kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)

## 🚀 Getting Started

1. **Clone the repository:**

   ```bash
   git clone <your-repository-url>
   ```

2. **Open the project** in Android Studio.

3. **Set up Firebase:**
   This project uses Firebase for backend services. To run the app, set up your own Firebase project:

    * Go to the [Firebase Console](https://console.firebase.google.com/)
    * Create a new Firebase project
    * Add an Android app with the package name `com.example.todo`
    * Download the generated `google-services.json` file
    * Place the file inside your project's `app/` directory

4. **Build and run** the app on an emulator or a physical device.
