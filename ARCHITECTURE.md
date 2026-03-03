# RandomBoxd Architecture

## Overview

RandomBoxd is a **Kotlin Multiplatform (KMP)** application built with **Compose Multiplatform** targeting Android and iOS. It fetches random movies from Letterboxd user watchlists and custom lists via web scraping. The project follows **Clean Architecture** principles with **MVVM** in the presentation layer.

---

## Module Structure

```
RandomBoxd/
├── composeApp/                    # Shared KMP application module
│   └── src/
│       ├── commonMain/            # Shared business logic and UI
│       ├── commonTest/            # Shared unit tests
│       ├── androidMain/           # Android platform implementations
│       ├── androidInstrumentedTest/ # Android UI tests
│       ├── iosMain/               # iOS platform implementations
│       └── nativeMain/            # Native-common code (iOS targets)
├── iosApp/                        # iOS entry point (Swift)
├── gradle/libs.versions.toml      # Version catalog (single source of truth for dependencies)
├── build.gradle.kts               # Root build configuration
└── settings.gradle.kts            # Module inclusion
```

There is a **single shared module** (`composeApp`). There are no separate feature modules; feature separation is achieved through package structure within `commonMain`.

---

## Package Layout (commonMain)

```
com.nacchofer31.randomboxd/
├── app/                              # App entry point and navigation
│   └── RandomBoxdApp.kt             # NavHost setup, route definitions
│
├── core/
│   ├── data/                         # Infrastructure: HTTP client, endpoints, safe call wrapper
│   │   ├── HttpClientFactory.kt      # Ktor client builder (timeouts, logging, JSON)
│   │   ├── RandomBoxdEndpoints.kt    # URL constants and path builders
│   │   └── SafeCallExtension.kt      # Wraps HTTP calls into ResultData with error mapping
│   ├── domain/                       # Shared domain types
│   │   ├── DataError.kt              # Sealed interface: Remote + Local error enums
│   │   └── ResultData.kt             # Result monad: Success | Error
│   └── presentation/                 # Design system tokens
│       ├── RandomBoxdColors.kt       # Color palette constants
│       └── RandomBoxdTypography.kt   # Custom font (Conduit) + Material3 type scale
│
├── di/                               # Dependency injection (Koin)
│   ├── Modules.kt                    # Common Koin module definitions
│   └── InitKoin.kt                   # Koin initialization entry point
│
├── onboarding/                       # Onboarding feature
│   └── presentation/
│       ├── OnboardingScreen.kt       # 4-page horizontal pager
│       └── components/               # Onboarding-specific composables
│
└── random_film/                      # Main feature
    ├── data/
    │   ├── dto/                      # Data Transfer Objects (FilmDto)
    │   ├── mappers/                  # DTO-to-domain mapping extensions
    │   └── repository_impl/          # Repository implementations
    │       ├── RandomFilmScrappingRepository.kt  # Primary: HTML scraping via Ksoup
    │       └── RandomFilmRepositoryImpl.kt       # Secondary: API-based (incomplete)
    ├── domain/
    │   ├── model/                    # Domain entities (Film, FilmSearchMode)
    │   └── repository/               # Repository interfaces (contracts)
    └── presentation/
        ├── viewmodel/
        │   └── RandomFilmViewModel.kt # State management, action handling
        ├── screen/
        │   └── RandomFilmScreen.kt    # Root screen composable
        └── components/                # 20+ reusable UI components
```

---

## Architecture Layers

### Presentation Layer

- **Pattern**: MVVM with unidirectional data flow.
- **ViewModels** extend `ViewModel` (from `lifecycle-viewmodel-compose`) and are scoped to navigation graphs.
- **State** is exposed as `StateFlow<State>` and collected in Compose via `collectAsStateWithLifecycle()`.
- **Actions** are modeled as a sealed interface (`RandomFilmAction`) and dispatched from UI to ViewModel via a single `onAction(action)` function.
- **UI** is 100% Jetpack Compose Multiplatform. No XML layouts, no SwiftUI. Screens compose smaller components from the `components/` package.

**State flow**:
```
UI Event → Action (sealed interface) → ViewModel processes → State update → UI recomposition
```

**Key ViewModel conventions**:
- Actions are emitted into a `MutableSharedFlow` and processed via Flow operators (`filterIsInstance`, `flatMapLatest`, `onEach`).
- Sharing strategy: `SharingStarted.WhileSubscribed(5000L)`.
- Loading states use null/non-null patterns on the result field plus an explicit `isLoading` boolean.

### Domain Layer

- **Pure Kotlin** — no framework dependencies.
- Contains repository **interfaces** (contracts), domain **models** (data classes/enums), and the **error/result types**.
- `ResultData<D, E>` is the canonical result wrapper — a sealed interface with `Success(data)` and `Error(error)` variants.
- `DataError` is a sealed interface split into `Remote` and `Local` enums covering all expected failure modes.
- Domain models: `Film` (slug, imageUrl, releaseYear, name), `UserName` (id, username — also a Room entity), `FilmSearchMode` (UNION, INTERSECTION).

### Data Layer

- **Repositories** implement domain interfaces and coordinate data sources.
- **Primary data source**: Web scraping of Letterboxd HTML using **Ksoup** (CSS selector-based parsing).
- **Secondary data source**: REST API via **Ktor** client (JSON serialization).
- **Local persistence**: **Room** database for saved usernames, with platform-specific `RoomDatabase.Builder` via expect/actual.
- **DTOs** are `@Serializable` data classes; mapping to domain models happens through extension functions in `mappers/`.

### Core / Infrastructure

- `HttpClientFactory` builds the Ktor client with platform-injected engine, content negotiation (JSON), logging, and 20-second timeouts.
- `SafeCallExtension.safeCall {}` wraps suspend blocks and maps exceptions to `DataError.Remote` variants.
- `RandomBoxdEndpoints` is a static object holding the base URL (`https://www.watchlistpicker.com`) and URL builder functions.

---

## Dependency Injection

**Framework**: Koin 4.1.1

**Module organization**:
- `commonMain/di/Modules.kt` — shared module registering repositories, ViewModels, HTTP client, dispatchers.
- `androidMain/di/Modules.android.kt` — provides `OkHttp` engine, Room database builder, `OnboardingPreferences` (SharedPreferences).
- `iosMain/di/Modules.ios.kt` — provides `Darwin` engine, Room database builder, `OnboardingPreferences` (NSUserDefaults).
- Platform modules are composed via `platformModule` (expect/actual).

**ViewModel registration**: Uses `viewModelOf(::RandomFilmViewModel)` for constructor injection.

**Initialization**:
- Android: `initKoin()` called from `RandomBoxdApplication.onCreate()` with `androidLogger()` and `androidContext()`.
- iOS: `initKoin()` called from Swift entry point.

---

## Navigation

**Framework**: Jetpack Compose Navigation with type-safe routes (Kotlin Serializable).

**Route definitions**:
```kotlin
sealed interface RandomBoxdRoute {
    @Serializable data object Onboarding : RandomBoxdRoute
    @Serializable data object Home : RandomBoxdRoute
    @Serializable data object RandomFilm : RandomBoxdRoute
}
```

**Navigation graph** (in `RandomBoxdApp.kt`):
- Start destination depends on `OnboardingPreferences.isFirstRun()`.
- `Onboarding` route → `OnboardingScreen` (navigates to Home on completion).
- `Home` navigation graph contains `RandomFilm` as its start destination.
- ViewModel is scoped to the `Home` navigation graph.

**External navigation**: Film clicks open the Letterboxd URL via `LocalUriHandler.current.openUri()`.

---

## Platform Abstraction (expect/actual)

The following use the expect/actual mechanism:

| Abstraction | Android (`androidMain`) | iOS (`iosMain`) |
|---|---|---|
| `OnboardingPreferences` | `SharedPreferences` | `NSUserDefaults` |
| `UserNameDatabaseConstructor` | Room `databaseBuilder` with context | Room with `BundledSQLiteDriver`, Documents dir |
| `platformModule` (Koin) | OkHttp engine + Android DB + prefs | Darwin engine + iOS DB + prefs |

**Dispatcher abstraction**: `DispatcherProvider` interface with `main`, `io`, `default` properties. Default implementation uses `Dispatchers.Main`, `Dispatchers.IO`, `Dispatchers.Default`. Test implementation uses `UnconfinedTestDispatcher`.

---

## Networking

- **Client**: Ktor 3.2.3 with platform-specific engines (OkHttp on Android, Darwin on iOS).
- **Primary approach**: Web scraping Letterboxd HTML pages. The `RandomFilmScrappingRepository` fetches watchlist/list pages and parses them with Ksoup using CSS selectors like `li.poster-container`.
- **Film data extraction**: Parses `data-film-slug`, `data-film-name`, `data-film-release-year`, and poster image `src` from HTML.
- **Multi-user support**: Fetches each user's list, then applies INTERSECTION (shared films) or UNION (all films) logic, and picks a random result.
- **Error handling**: `safeCall {}` maps exceptions and HTTP status codes to `DataError.Remote` variants.

---

## Database

- **ORM**: Room 2.7.2 with KSP code generation (`ksp.useKSP2=true`).
- **Single table**: `UserName` entity (Long id, String username).
- **DAO operations**: `upsert`, `delete`, `getAllUserNames()` (returns `Flow<List<UserName>>`).
- **Database class**: `UsernameDatabase` (abstract, extends `RoomDatabase`).
- **Platform constructors**: `getUserNameDatabase()` in each platform's source set provides the `RoomDatabase.Builder` with platform-appropriate storage paths.

---

## Design System

**Colors** (`RandomBoxdColors` object):
| Token | Hex | Usage |
|---|---|---|
| `BackgroundDarkColor` | `#14181C` | Primary background |
| `BackgroundLightColor` | `#99AABB` | Secondary surfaces |
| `TextFieldBackgroundColor` | `#2C3440` | Input fields |
| `CardBackground` | `#1C2228` | Card surfaces |
| `TextMuted` | `#556677` | Disabled/hint text |
| `GreenAccent` | `#00E054` | Primary accent, buttons |
| `OrangeAccent` | `#F27405` | Union mode indicator |
| `BlueAccent` | `#40BCF4` | Info elements |

**Typography**: Custom font `Conduit` (light/regular weights) applied to the full Material3 `Typography` scale. Sizes range from 12sp to 32sp.

**Theme**: Dark theme only. No light theme variant exists.

---

## State Management

The ViewModel uses a Flow-based reactive pipeline:

1. **Actions** arrive via `MutableSharedFlow<RandomFilmAction>` (extra buffer capacity, drop oldest on overflow).
2. **Processing pipelines** are set up in `init {}` using Flow operators:
   - `filterIsInstance<SpecificAction>()` to route actions.
   - `flatMapLatest {}` for cancellable async operations (new search cancels previous).
   - `onStart {}` and `onEach {}` for state mutations.
3. **State** is held in `MutableStateFlow<RandomFilmState>` and exposed as read-only `StateFlow`.
4. **UI collection**: `state.collectAsStateWithLifecycle()` in composables.

**State data class** (`RandomFilmState`):
- `userName: String` — current input.
- `resultFilm: Film?` — last successful result.
- `resultError: DataError.Remote?` — last error.
- `isLoading: Boolean` — loading indicator.
- `userNameSearchList: Set<String>` — multi-user search set.
- `filmSearchMode: FilmSearchMode` — INTERSECTION or UNION.

---

## Error Handling

**Result type**: `ResultData<D, E : Error>` sealed interface with `Success` and `Error` variants. Extension functions: `.map()`, `.onSuccess()`, `.onError()`.

**Remote errors** (`DataError.Remote`):
- `REQUEST_TIMEOUT` — socket timeout or HTTP 408.
- `TOO_MANY_REQUESTS` — HTTP 429.
- `NO_INTERNET` — unresolved address.
- `SERVER` — HTTP 5xx.
- `SERIALIZATION` — parsing failures.
- `NO_RESULTS` — scraping returned empty.
- `UNKNOWN` — catch-all.

**Local errors** (`DataError.Local`): `DISK_FULL`, `UNKNOWN`.

**Error display**: `FilmErrorView` composable maps each error enum to a user-facing string resource and icon.

---

## Testing

**Frameworks**:
| Tool | Purpose |
|---|---|
| JUnit 4 | Test runner |
| Kotlin Test | Assertions and test utilities |
| Turbine | Flow/StateFlow emission testing |
| AssertK | Fluent assertions |
| Mockmp (Kodein) | Mocking with KSP generation |
| Ktor MockEngine | HTTP request interception |
| Compose UI Test | Instrumented UI testing |

**Test utilities** (in `commonTest`):
- `TestDispatchers`: Provides `UnconfinedTestDispatcher` for all dispatcher types.
- `HttpResponseData`: Helper to build mock Ktor responses.

**Testing patterns**:
- ViewModels are tested with `runTest` + Turbine's `state.test { awaitItem() }`.
- Repositories are tested with Ktor `MockEngine` returning canned HTML/JSON.
- UI tests use Compose test tags (`Modifier.testTag(...)`) for node lookup.

**Code coverage**: JaCoCo with a **70% minimum** line coverage requirement. Reports generated in HTML, CSV, and XML. Exclusions: generated code, app bootstrap classes.

---

## Build System

- **Gradle**: 9.2.1
- **Kotlin**: 2.2.10
- **AGP**: 9.0.1
- **Compose Multiplatform**: 1.10.0
- **KSP**: Used for Room code generation and Mockmp mock generation.

**Android targets**: minSdk 24, targetSdk 35, compileSdk 35, JVM target Java 21.

**iOS targets**: `iosArm64`, `iosX64`, `iosSimulatorArm64` — grouped via `iosMain` intermediate source set.

**Code formatting**: Spotless plugin with KtLint rules. Run via `./gradlew :composeApp:spotlessApply`.

**Key Gradle tasks**:
- `:composeApp:testDebugUnitTest` — run unit tests.
- `:composeApp:jacocoTestReport` — generate coverage report.
- `:composeApp:jacocoTestCoverageVerification` — enforce 70% threshold.
- `:composeApp:fullCoverageReport` — tests + coverage in one task.
- `:composeApp:spotlessApply` — format code.
- `:composeApp:spotlessCheck` — verify formatting.

---

## Localization

- String resources use Compose Multiplatform's `Res.string.*` mechanism.
- 40+ string keys defined.
- Supported languages: CA, DE, ES, FR, GL, IT, JA, PT (plus default English).
- All user-facing text must go through `stringResource(Res.string.key)`.

---

## Key Conventions and Rules

1. **No feature modules** — features are packages, not Gradle modules. All code lives in `composeApp`.
2. **Repository pattern** — domain layer defines interfaces; data layer provides implementations. Never depend on concrete repository classes from presentation.
3. **Single ViewModel per feature** — `RandomFilmViewModel` handles all state for the main feature, scoped to the `Home` navigation graph.
4. **Actions over callbacks** — UI communicates with ViewModels exclusively through sealed interface actions, not individual callback lambdas.
5. **ResultData everywhere** — all repository methods return `ResultData<Success, Error>`, never throw exceptions to callers.
6. **Expect/actual for platform code** — platform-specific implementations live in `androidMain`/`iosMain` with shared interfaces in `commonMain`.
7. **Koin for DI** — all dependencies are registered in Koin modules. No manual construction of repositories or ViewModels.
8. **Dark theme only** — the app uses a single dark color scheme. All colors are in `RandomBoxdColors`.
9. **Custom font** — Conduit is the only font. Typography is defined once in `RandomBoxdTypography` and applied globally.
10. **Test coverage gate** — JaCoCo enforces 70% line coverage. New code must include tests.
11. **Code formatting** — Spotless/KtLint must pass. Run `spotlessApply` before committing.
12. **Flow-based reactivity** — state management uses Kotlin Flow operators, not LiveData or callbacks.
13. **Web scraping as primary data source** — the app parses Letterboxd HTML, not a stable API. Scraped selectors (e.g., `li.poster-container`, `data-film-slug`) are brittle and may break if Letterboxd changes their markup.
14. **No network caching layer** — there is no HTTP cache or local film cache. Each search hits the network.
15. **Room for persistence** — only used for username history. Film data is not persisted locally.
