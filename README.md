# Chronicle – NIT3213 Assignment 2

**Student:** Shoaib Abedin (s8125078)
**Unit:** NIT3213 Android Application Development, Victoria University (Footscray)

Chronicle is an Android app that logs in to the NIT3213 API, loads the list of entities for my topic and shows the full details of each one. My keypass is `history`, so the app shows historical events, but it works with any topic the API returns.

## Features

- **Login screen** with input validation, a show/hide password toggle and clear error messages (wrong details, no internet, server timeout).
- **Dashboard** with a RecyclerView of entity cards. Each card shows a summary without the description, as the spec asks.
- **Details screen** with every field of the selected entity, including the description.
- A loading indicator, plus a "waking up the server" hint when the API is slow (it's hosted on Render's free tier, which sleeps when idle).
- Retry when loading fails, and Log out.
- Works for any keypass. Entities are parsed as key/value maps, so different topics with different fields still display properly.

## Logging in

| Field | Value |
|---|---|
| Student ID | `8125078` |
| Password | `Shoaib Abedin` (case-sensitive) |

The app sends these to `POST /footscray/auth`. The first login can take up to a minute if the server has been asleep.

## How to build and run

**Requirements**
- Android Studio (recent version, the project uses AGP 9.4.1 and Gradle 9.6)
- JDK 17 or newer (the one bundled with Android Studio works)
- An emulator or device running Android 7.0 (API 24) or later, with internet access

**Steps**
1. Clone the repository:
   ```
   git clone https://github.com/ShoaibAbedin/8125078Assignment2.git
   ```
2. In Android Studio choose **File → Open** and select the `8125078Assignment2` folder.
3. Wait for Gradle sync to finish. The first sync downloads all dependencies.
4. Select an emulator and press **Run**.
5. Log in with the details above.

## Running the unit tests

- In Android Studio: right-click `app/src/test/java` → **Run 'Tests in 'java''**
- Or from the terminal in the project folder:
  ```
  ./gradlew testDebugUnitTest
  ```
  (on Windows: `gradlew.bat testDebugUnitTest`)

There are 36 unit tests covering:

| Test class | What it checks |
|---|---|
| `LoginViewModelTest` | Validation, successful login, error messages, slow-server hint, navigation reset |
| `DashboardViewModelTest` | Loading on init, error states, retry, empty state, missing keypass |
| `AuthRepositoryImplTest` | Keypass returned, correct endpoint, HTTP 401/500, timeout and network errors |
| `DashboardRepositoryImplTest` | Mapping the response, `entityTotal` fallback, invalid response, errors |
| `EntityMapperTest` | Titles, year ranges, labels, value formatting, other topics |
| `EntityAdapterTest` | Item count and `notifyDataSetChanged` after `setData` |

The tests use JUnit 4, MockK to mock dependencies and kotlinx-coroutines-test (`StandardTestDispatcher`, `advanceUntilIdle`) for the coroutine code.

## Architecture

The app follows the layered structure from the unit: UI → ViewModel → Repository → API.

```
Fragment (UI only)
   │  collects StateFlow, sends user actions
   ▼
ViewModel (@HiltViewModel)
   │  holds screen state, calls the repository in viewModelScope
   ▼
Repository (interface + implementation)
   │  calls the API, maps the response, returns ApiResult
   ▼
ApiService (Retrofit)  ──►  https://nit3213apinew.onrender.com
```

- **Single activity.** `MainActivity` only hosts a `NavHostFragment`. The three screens are Fragments connected in `nav_graph.xml` with the Navigation Component.
- **Safe Args** passes the keypass to the Dashboard and the selected entity to the Details screen.
- **StateFlow** exposes each screen's state (`LoginUiState`, `DashboardUiState`), and the Fragments render it with `repeatOnLifecycle`.
- **ApiResult** wraps every API call as `Success` or `Failure(ApiError)`, so ViewModels don't need try/catch. The error type is mapped to a message in `ApiErrorMessages.kt`.

## Dependency injection (Hilt)

- `ChronicleApplication` is annotated with `@HiltAndroidApp`.
- `MainActivity` and all Fragments use `@AndroidEntryPoint`.
- `LoginViewModel` and `DashboardViewModel` use `@HiltViewModel` with `@Inject` constructors.
- `NetworkModule` (`@Module`, `@InstallIn(SingletonComponent::class)`) `@Provides` the `Gson`, `OkHttpClient`, `Retrofit` and `ApiService` singletons.
- `RepositoryModule` uses `@Binds` to connect `AuthRepository` and `DashboardRepository` to their implementations.
- `AuthRepositoryImpl`, `DashboardRepositoryImpl` and `EntityMapper` are constructor-injected.

No class creates its own dependencies, which is why the ViewModels and repositories can be unit tested with mocks.

## Project structure

```
app/src/main/java/com/s8125078/assignment2/
├── ChronicleApplication.kt
├── MainActivity.kt
├── data/
│   ├── mapper/        EntityMapper
│   ├── model/         Entity, EntityField
│   ├── remote/        ApiService, ApiConfig, ApiResult, SafeApiCall
│   │   └── dto/       LoginRequest, LoginResponse, DashboardResponse
│   └── repository/    AuthRepository, DashboardRepository (+ implementations)
├── di/                NetworkModule, RepositoryModule
└── ui/
    ├── common/        ApiErrorMessages
    ├── login/         LoginFragment, LoginViewModel, LoginUiState
    ├── dashboard/     DashboardFragment, DashboardViewModel, DashboardUiState, EntityAdapter
    └── details/       DetailsFragment
```

## Libraries

| Library | Used for |
|---|---|
| Hilt 2.60.1 (with KSP) | Dependency injection |
| Retrofit 2.11 + Gson converter | API calls and JSON parsing |
| OkHttp logging interceptor | Logging requests in Logcat |
| Kotlin coroutines + StateFlow | Asynchronous work and UI state |
| Navigation Component + Safe Args | Fragment navigation and passing data |
| RecyclerView | Dashboard list |
| Material Components (Material 3) | UI components and theme |
| JUnit 4, MockK, kotlinx-coroutines-test | Unit tests |

## Notes

- **Gson instead of Moshi:** Moshi's `KotlinJsonAdapterFactory` relies on kotlin-reflect, which didn't line up with the Kotlin version bundled with this Android Gradle Plugin. Gson was the simpler option and is also covered in the lecture.
- **KSP instead of kapt:** AGP 9 has built-in Kotlin support and the old kapt plugin isn't compatible with it, so Hilt's compiler runs through KSP. The Hilt annotations are exactly the same.
- **Serializable instead of @Parcelize:** the Parcelize plugin isn't confirmed to work with AGP 9's built-in Kotlin yet, so `Entity` implements `Serializable`, which Safe Args also supports.
- **Privacy:** the password is only sent in the login request and is never saved on the device. The logging interceptor is set to `BASIC`, so request bodies (and the password) never appear in Logcat.
- The UI uses XML layouts with Views (no Jetpack Compose), as required for this unit.
