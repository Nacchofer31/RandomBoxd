# 🎥 RandomBoxd 🍿


<p align="center">
  <img src="screenshots/randomboxd-logo.png" width="200" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Compose-Multiplatform-4285F4?logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Platform-Android%20%7C%20iOS-blue?logo=apple&logoColor=white" />
  <img src="https://img.shields.io/badge/License-Apache%202.0-green?logo=open-source-initiative&logoColor=white" />
  <img src="https://img.shields.io/badge/Test%20Coverage-JaCoCo-orange?logo=codecov&logoColor=white" />
</p>

RandomBoxd is a **Compose Multiplatform** project designed to fetch a random movie from a Letterboxd user's **watchlists** or **custom lists**. This app is built for **Android** and **iOS** devices. 📱🎬

## 📲 Download

- **Android** (phones & tablets): Available now on [Google Play](https://play.google.com/store/apps/details?id=com.nacchofer31.randomboxd).
- **iOS** (iPhone & iPad): Coming soon to the App Store.

## ✨ Features
- 🎲 Fetch a random movie from a Letterboxd user's **watchlist** or **custom lists**.
- 📱 Supports **Android** and **iOS** platforms.
- 🔗 Seamless deep linking with the **Letterboxd** app.
- 🔍 Intuitive user search functionality. 
- 🏷️ Stores and displays previously entered UserNames locally via **Room**, shown as tappable tags for quick reuse or deletion.
- ∩∪ **Intersection & Union** → Combine multiple users' watchlists and pick a random movie either from their **shared movies** (Intersection) or from the **merged pool** (Union).

## 🧙‍♂️ Watch the Magic

<p align="center">
  <img src="screenshots/randomboxd-demo.gif" width="250" />
</p>

## 🏗️ Project Architecture
This project follows the **Clean Architecture** principles to ensure a scalable and maintainable codebase.
For a detailed breakdown of layers, patterns, conventions, and technical decisions, see the [Architecture Document](ARCHITECTURE.md).

## 🏛️ KMP Structure
- **`/composeApp`** is for code that will be shared across your **Compose Multiplatform** applications.
  - `commonMain` contains code common for all targets.
  - Other folders include platform-specific code (e.g., `iosMain` for iOS-specific code like **CoreCrypto** integration).

- **`/iosApp`** contains iOS applications.
  - Even if you're sharing UI with **Compose Multiplatform**, this folder is the **entry point** for the iOS app.
  - This is also where you can add **SwiftUI** code if needed.

## 🔧 Tech Stack
- **Compose Multiplatform (CMP)** - Shared logic for Android and iOS with Compose.
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

## 🔍 Search user lists & Deeplinking

<p align="center">
  <img src="screenshots/search-user-list-1.png" width="200" />
  <img src="screenshots/search-user-list-2.png" width="200" />
</p>

## 🔗 Intersection & Union of Users' Watchlists
RandomBoxd allows you to combine multiple users' **watchlists** by holding the `Submit` button or selecting multiple user tags.  
From there, a single random movie is chosen based on two available modes:

- **∩ Intersection** → Selects only movies that are present in **every selected user's watchlist**.  
- **∪ Union** → Combines all movies from the selected users' watchlists and randomly picks one from the full collection.  

<p align="center">
  <img src="screenshots/intersection-users.png" width="200" />
  <img src="screenshots/union-users.png" width="200" />
</p>

## ✍️ See Previous UserName Prompts
<p align="center">
  <img src="screenshots/user-name-list.png" width="200" />
</p>

## 🚀 Getting Started
1. Clone the repository:
   ```sh
   git clone https://github.com/Nacchofer31/RandomBoxd.git
   ```

2. Open the project in **Android Studio (latest version with [CMP Plugin](https://www.jetbrains.com/compose-multiplatform/))**.
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
This project is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for details.

---
Enjoy **RandomBoxd** and never struggle to pick a movie again! 🎬🍀