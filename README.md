# CabinSync

A guest management system combining an Android mobile app with a lightweight Admin Web dashboard. CabinSync helps record guest entries, schedule meetings, approve/deny visit requests, and generate printable reports — all backed by Firebase Realtime Database.

## Overview
- **Mobile App (Android)**: Guest registration, login, meeting scheduling, user profile management, and real-time status updates.
- **Admin Web**: Manage authorities and users, review guest entries, bulk approve/deny, and print date-filtered "User-Entry" reports.
- **Backend**: Firebase Realtime Database with Google Services configuration (mobile) and web SDK (admin).

## Features
- **Guest Entry & Registration**: Guests can register and submit visit details from the mobile app.
- **Meeting Scheduling & Management**: Create, view, and manage meeting details and statuses.
- **Approvals Workflow**: Admin dashboard supports bulk Allow/Deny and printing reports.
- **Search & Filters**: Date filter for reports; organized tables for quick review.
- **Realtime Sync**: Firebase powers instantaneous updates between mobile and web admin.

## Tech Stack
- **Android**: Kotlin, AndroidX, Material Components, Navigation, ViewBinding.
- **Web Admin**: HTML/CSS/JS (ES modules), Firebase Web SDK.
- **Backend**: Firebase Realtime Database; project config via `google-services.json` (Android) and web config in admin pages.

## Project Structure
- **Android App**: [CabinSync/](CabinSync)
  - Build files: [CabinSync/build.gradle.kts](CabinSync/build.gradle.kts), [CabinSync/settings.gradle.kts](CabinSync/settings.gradle.kts), [CabinSync/gradle.properties](CabinSync/gradle.properties)
  - App module: [CabinSync/app](CabinSync/app)
  - Manifest & resources: [CabinSync/app/src/main/AndroidManifest.xml](CabinSync/app/src/main/AndroidManifest.xml), [CabinSync/app/src/main/res](CabinSync/app/src/main/res)
- **Admin Web**: [CS Admin/](CS%20Admin)
  - Pages: [CS Admin/index.html](CS%20Admin/index.html), [CS Admin/adminpage.html](CS%20Admin/adminpage.html), [CS Admin/manage_user.html](CS%20Admin/manage_user.html), [CS Admin/manage_authority.html](CS%20Admin/manage_authority.html), [CS Admin/authority_status.html](CS%20Admin/authority_status.html)
  - Report page: [CS Admin/report.html](CS%20Admin/report.html)
  - Assets: [CS Admin/assets](CS%20Admin/assets)
- **Screenshots**: [Screenshots/Admin Web](Screenshots/Admin%20Web), [Screenshots/User Mobile](Screenshots/User%20Mobile)

## Screenshots

### Admin Web

<p>
  <img src="Screenshots/Admin%20Web/Admin%20Login%20Page.png" alt="Admin Login" width="320">
  <br/>
  <em>Admin Login:</em> Secure login screen for administrators to access the dashboard.
</p>

<p>
  <img src="Screenshots/Admin%20Web/Manage%20User.png" alt="Manage User" width="320">
  <br/>
  <em>Manage User:</em> Create, view, update, and remove user accounts.
</p>

<p>
  <img src="Screenshots/Admin%20Web/Manage%20Authority.png" alt="Manage Authority" width="320">
  <br/>
  <em>Manage Authority:</em> Maintain authority profiles, roles, and permissions.
</p>

<p>
  <img src="Screenshots/Admin%20Web/Authority%20Status.png" alt="Authority Status" width="320">
  <br/>
  <em>Authority Status:</em> Monitor status changes and approvals for authorities.
</p>

<p>
  <img src="Screenshots/Admin%20Web/Guest%20Entry%20Details.png" alt="Guest Entry Details" width="320">
  <br/>
  <em>Guest Entry Details:</em> Review guest visits with date, purpose, contact, and status.
</p>

### Mobile App

<p>
  <img src="Screenshots/User%20Mobile/Splash%20Screen.jpg" alt="Splash Screen" width="320">
  <br/>
  <em>Splash Screen:</em> App launch screen introducing CabinSync.
</p>

<p>
  <img src="Screenshots/User%20Mobile/Login%20Page.jpg" alt="Login Page" width="320">
  <br/>
  <em>Login Page:</em> User authentication to access app features.
</p>

<p>
  <img src="Screenshots/User%20Mobile/User%20Home%20Page.jpg" alt="User Home Page" width="320">
  <br/>
  <em>User Home:</em> Main dashboard with quick actions and navigation.
</p>

<p>
  <img src="Screenshots/User%20Mobile/Guest%20Registration.jpg" alt="Guest Registration" width="320">
  <br/>
  <em>Guest Registration:</em> Capture guest details and contact information.
</p>

<p>
  <img src="Screenshots/User%20Mobile/Scheduling%20Page.jpg" alt="Scheduling Page" width="320">
  <br/>
  <em>Scheduling:</em> Request and manage meetings with date and purpose.
</p>

<p>
  <img src="Screenshots/User%20Mobile/Chat%20Assist.jpg" alt="Chat Assist" width="320">
  <br/>
  <em>Chat Assist:</em> Conversational helper for guidance and quick support.
</p>

## Key Mobile Screens (from Manifest)
- `splash_screen` (Launcher)
- `login_screen`
- `user_home`
- `guest_register` and `guest_main`
- `personal_details`, `user_profile`, `Changepassword`
- `MeetingsDetails`, `ManageMeeting`

## Getting Started

### Prerequisites
- Android Studio (latest) with SDK 34
- JDK 8+ (project targets JVM 1.8)
- Optional: A Firebase project for your own config

### Build the Android App (Windows)
1. Open [CabinSync](CabinSync) in Android Studio, or use Gradle Wrapper:

```powershell
# From the inner Android project folder
cd "CabinSync"
```

```powershell
# Build a debug APK
./gradlew.bat assembleDebug
```

The APK will be under `app/build/outputs/apk/debug/`.

### Run the Admin Web
- Easiest: Open [CS Admin/index.html](CS%20Admin/index.html) directly in your browser.
- Optional local server (if you prefer):
  - Using Python: `python -m http.server` from the `CS Admin` folder
  - Using VS Code Live Server extension

### Firebase Setup
- **Android**: The project already contains [CabinSync/app/google-services.json](CabinSync/app/google-services.json). If you fork this project, replace it with your own Firebase config from the Firebase Console.
- **Web Admin**: The Firebase web configuration is embedded in [CS Admin/report.html](CS%20Admin/report.html). Update keys there if you use a different Firebase project.

## How It Works (Brief)
- Mobile app writes guest and meeting data under `Guest` in Firebase Realtime Database.
- Admin web reads from `Guest/{guestId}/Meetings/{meetingId}` and allows bulk status updates (Allowed/Denied) and prints date-filtered reports.
- Changes reflect in realtime across app and admin due to Firebase sync.

## Notes
- Sensitive configs (Firebase keys) are currently included for demo. For production, manage secrets securely.
- Consider role-based auth for admin pages if deploying publicly.

## Contributing
Pull requests are welcome. Please open issues for bugs or feature requests.

## Acknowledgements
- Firebase Web SDK & Google Services
- AndroidX & Material Components
