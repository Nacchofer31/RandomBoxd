# 🎥 RandomBoxd 🍿


<p align="center">
  <img src="screenshots/randomboxd-logo.png" width="200" />
</p>

RandomBoxd is a **Kotlin Multiplatform** project designed to fetch a random movie from a Letterboxd user's **watchlists** or **custom lists**. This app is built for **Android** and **iOS** devices. 📱🎬

## ✨ Features
- 🎲 Fetch a random movie from a Letterboxd user's **watchlist** or **custom lists**.
- 📱 Supports **Android** and **iOS** platforms.
- 🔗 Seamless deep linking with the **Letterboxd** app.
- 🔍 Intuitive user search functionality. 
- 🏷️ Stores and displays previously entered UserNames locally via **Room**, shown as tappable tags for quick reuse or deletion.

## 🏗️ Project Architecture
This project follows the **Clean Architecture** principles to ensure a scalable and maintainable codebase.

### 🏛️ KMP Structure
- **`/composeApp`** is for code that will be shared across your **Compose Multiplatform** applications.
  - `commonMain` contains code common for all targets.
  - Other folders include platform-specific code (e.g., `iosMain` for iOS-specific code like **CoreCrypto** integration).

- **`/iosApp`** contains iOS applications.
  - Even if you're sharing UI with **Compose Multiplatform**, this folder is the **entry point** for the iOS app.
  - This is also where you can add **SwiftUI** code if needed.

## 🔧 Tech Stack
- **Kotlin Multiplatform Mobile (KMP)** - Shared logic for Android and iOS.
- **Jetpack Compose** - UI for Android.
- **Ktor** - Network requests.
- **Coroutines & Flow** - Asynchronous programming.
- **Koin** - Dependency Injection.
- **Coil** - Image loading.
- **Room** - Database.
- **Ksoup** - HTML parsing.
- **Navigation Compose** - Jetpack Compose navigation.
- **Kotlinx Serialization** - JSON serialization.
- **Spotless** - Code formatting.
- **Junit5** - Unit testing.
- **Turbine** - State testing.
- **Jacoco** - Code coverage.

## 🌍 Localization Support

RandomBoxd is built with **full localization support**, allowing the app to be translated into **any language**.
- 🏳️ Uses Kotlin Multiplatform's localization tools for seamless translations.
- 📝 Supports dynamic text updates based on user preferences.
- 🌐 Easily adaptable for different regions and languages.


## ✅ Testing

RandomBoxd's **codebase** is **tested** with:
- 🧪 **JUnit5** for unit tests.
- 🌊 **Turbine** for verifying Kotlin Flow emissions and state changes.
- 🔌 **Ktor Client Mock** for mocking and testing network requests.
- 📈 **Jacoco** to measure and ensure code coverage across the project.

## 📸 Screenshots

### 🤖 Android
<p align="center">
  <img src="screenshots/android-randomboxd-1.png" width="200" />
  <img src="screenshots/android-randomboxd-2.png" width="200" />
</p>

### 🍏 iOS
<p align="center">
  <img src="screenshots/ios-randomboxd-1.png" width="200" />
  <img src="screenshots/ios-randomboxd-2.png" width="200" />
</p>

### 🔍 Search user lists & Deeplinking

<p align="center">
  <img src="screenshots/search-user-list-1.png" width="200" />
  <img src="screenshots/search-user-list-2.png" width="200" />
</p>

### ✍️ See Previous UserName Prompts
<p align="center">
  <img src="screenshots/user-name-list.png" width="200" />
</p>

## 🚀 Getting Started
1. Clone the repository:
   ```sh
   git clone https://github.com/Nacchofer31/RandomBoxd.git
   ```

2. Open the project in **Android Studio (latest version with [KMP Plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform))**.
3. Run on an **Android Emulator** or an **iOS Simulator**.
   <img src="screenshots/run-config.png" width="400" />
4. Run spotless commmands:
   ```sh
   ./gradlew :composeApp:spotlessApply 
   ```
5. Generate jacoco reports:
   ```sh
   ./gradlew :composeApp:jacocoTestReport
   ```
6. Start selecting random movies from Letterboxd lists! 🎞️

## 🤝 Contributing
Feel free to **open issues** or **submit pull requests** to improve the project. 🛠️

## 📜 License
This project is licensed under the **MIT License**.

---
Enjoy **RandomBoxd** and never struggle to pick a movie again! 🎬🍀