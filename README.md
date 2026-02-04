# 🔔 FCM Compose Sample App

> A clean, minimal Android application demonstrating Firebase Cloud Messaging (FCM) integration with Jetpack Compose and Kotlin.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-1.5.4-green.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-32.7.0-orange.svg)](https://firebase.google.com)
[![minSdk](https://img.shields.io/badge/minSdk-21-red.svg)](https://developer.android.com/about/versions/lollipop)

## 🎯 What This App Does

This sample demonstrates the core functionality of Firebase Cloud Messaging:

- **📱 Topic Subscription**: Subscribe your device to FCM topics with a single button tap
- **🔑 Token Management**: Retrieve and log your device's unique FCM registration token
- **💬 Push Notifications**: Receive and handle cloud messages in real-time
- **🎨 Modern UI**: Clean interface built with Jetpack Compose

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| **Subscribe to Topic** | One-tap subscription to the "general" topic for group messaging |
| **Print FCM Token** | Instantly retrieve and log your device token for testing |
| **Background Notifications** | Automatic notification handling when app is in background |
| **Foreground Messages** | Custom message handling when app is active |

## 📋 Prerequisites

Before getting started, make sure you have:

- ✅ Android Studio Hedgehog (2023.1.1) or newer
- ✅ JDK 11 or higher
- ✅ Android device or emulator (API 21+)
- ✅ Google account for Firebase Console
- ✅ Basic understanding of Kotlin and Jetpack Compose

## 🔥 Firebase Setup Guide

### Step 1: Create Firebase Project

1. Navigate to [Firebase Console](https://console.firebase.google.com/)
2. Click **"Add project"** or select existing one
3. Follow the setup wizard (Google Analytics is optional)

### Step 2: Register Your Android App

1. Click the **Android icon** in your Firebase project
2. Provide the following:
   - **Package name**: Must match your app's package (e.g., `com.example.fcmsample`)
   - **App nickname**: Optional friendly name
   - **SHA-1 certificate**: Required for certain features

   > 💡 **Get SHA-1 for debug build:**
   > ```bash
   > keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
   > ```
   > Default password: `android`

3. Click **"Register app"**

### Step 3: Download google-services.json

1. Download the `google-services.json` configuration file
2. Place it in your project's **`app/`** directory (NOT in `src/`)
   
   ```
   YourProject/
   ├── app/
   │   ├── google-services.json  ← HERE
   │   ├── build.gradle.kts
   │   └── src/
   ```

3. **Important**: Add to `.gitignore` to keep credentials secure:
   ```
   # Firebase
   google-services.json
   ```

### Step 4: Enable Cloud Messaging

Cloud Messaging is enabled by default for new Firebase projects. You can verify:
- Navigate to **Build** → **Cloud Messaging** in Firebase Console
- Note your **Server Key** (found in Project Settings → Cloud Messaging)

## 🛠️ Project Configuration

### 1. Project-level build.gradle.kts

Add the Google Services plugin to your project-level build file:

```kotlin
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}
```

### 2. App-level build.gradle.kts

Apply the plugin and add Firebase dependencies:

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Add this
}

dependencies {
    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    
    // Compose & Coroutines (versions in your project)
    // ... other dependencies
}
```

### 3. AndroidManifest.xml

Add required permissions and FCM service declaration:

```xml
<!-- Permissions -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- Inside <application> tag -->
<service
    android:name=".FCMService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

### 4. Sync & Build

1. Sync Gradle files
2. Clean and rebuild project
3. Verify no build errors

> ⚠️ **Common Issue**: If sync fails, ensure `google-services.json` is in the correct location

## 📦 Core Dependencies

| Library | Purpose |
|---------|---------|
| `firebase-messaging-ktx` | FCM SDK for receiving push notifications |
| `compose-bom` | Jetpack Compose Bill of Materials |
| `material3` | Material Design 3 components for UI |
| `kotlinx-coroutines-play-services` | Coroutines support for Firebase APIs |

## 🏗️ Architecture Overview

The app consists of three main components:

### 1. **FCMService** (extends FirebaseMessagingService)
- Handles incoming push notifications
- Manages token refresh events
- Logs message payloads for debugging

### 2. **MainActivity** (Compose UI)
- Displays two primary action buttons
- Manages notification permissions (Android 13+)
- Handles user interactions with coroutines

### 3. **Compose Screen**
- **Subscribe Button**: Calls `FirebaseMessaging.getInstance().subscribeToTopic("general")`
- **Print Token Button**: Retrieves token via `FirebaseMessaging.getInstance().token`
- Shows status messages for user feedback

## 🚀 How to Use

### Running the Application

1. ✅ Ensure `google-services.json` is in `app/` directory
2. ✅ Sync Gradle files
3. ✅ Build and run on a physical device (recommended) or emulator
4. ✅ Grant notification permission when prompted (Android 13+)

### App Features

#### 🔔 Subscribe to Topic Button
- Subscribes your device to the **"general"** FCM topic
- Allows sending group notifications to all subscribed devices
- Success confirmation shown on screen
- Check Logcat filter `FCM_SUBSCRIBE` for detailed logs

#### 🔑 Print Token Button  
- Retrieves your device's unique FCM registration token
- Token is logged to Logcat (filter: `FCM_TOKEN`)
- Use this token for sending targeted notifications to specific devices
- Token format: `eXXXXXX:APA91bXXXXXXX...`

## 🧪 Testing Push Notifications

### Option 1: Firebase Console (Easiest)

1. Open [Firebase Console](https://console.firebase.google.com/) → **Engage** → **Cloud Messaging**
2. Click **"Send your first message"** or **"New campaign"**
3. Fill in notification details:
   - Title: "Test Notification"
   - Text: "Hello from Firebase!"
4. Choose target:
   - **Topic**: `general` (if you subscribed)
   - **Single device**: Paste the FCM token from logs
5. Click **Review** → **Publish**

### Option 2: Using cURL (For Developers)

**Send to specific device:**
```bash
curl -X POST https://fcm.googleapis.com/fcm/send \
  -H "Authorization: key=YOUR_SERVER_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "DEVICE_TOKEN_HERE",
    "notification": {
      "title": "Test Title",
      "body": "Test message from cURL"
    }
  }'
```

**Send to topic subscribers:**
```bash
curl -X POST https://fcm.googleapis.com/fcm/send \
  -H "Authorization: key=YOUR_SERVER_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "/topics/general",
    "notification": {
      "title": "Topic Notification",
      "body": "Message to all subscribers"
    }
  }'
```

> 💡 **Find your Server Key**: Firebase Console → Project Settings → Cloud Messaging → Server key

### Viewing Logs

Use these Logcat tags to monitor FCM activity:
- `FCM_TOKEN` - Token generation and retrieval
- `FCM_SUBSCRIBE` - Topic subscription events  
- `FCM_MESSAGE` - Incoming notifications
- `FCM_PERMISSION` - Permission status

## 🔧 Troubleshooting

### Token Not Generating

**Issue**: FCM token is null or not appearing in logs

**Solutions**:
- ✅ Verify `google-services.json` is in `app/` directory (not `src/`)
- ✅ Confirm package name matches Firebase Console registration
- ✅ Check internet connectivity
- ✅ Clean and rebuild: `./gradlew clean build`
- ✅ Invalidate caches: **File → Invalidate Caches → Restart**

### Not Receiving Notifications

**Issue**: Notifications aren't showing up

**Solutions**:
- ✅ Grant notification permission (Settings → Apps → Your App → Notifications)
- ✅ Disable battery optimization for the app
- ✅ Test on a physical device (emulators can be unreliable)
- ✅ Verify correct Server Key and device token/topic
- ✅ Check Logcat for error messages

### Build/Sync Errors

**Issue**: Gradle sync or build failures

**Solutions**:
- ✅ Ensure Google Services plugin is applied: `id("com.google.gms.google-services")`
- ✅ Validate `google-services.json` format (must be valid JSON)
- ✅ Check internet connection for dependency downloads
- ✅ Update to latest Firebase BOM version
- ✅ Clear Gradle cache: `./gradlew clean`

### App Crashes on Launch

**Issue**: App crashes immediately after opening

**Solutions**:
- ✅ Check for missing `FCMService` declaration in AndroidManifest
- ✅ Verify all required permissions are declared
- ✅ Review Logcat stack trace for specific errors
- ✅ Ensure FCM service extends `FirebaseMessagingService`

> 💡 **Debug Tip**: Always check Logcat with package name filter for detailed error messages

## 🔐 Security Best Practices

- 🚫 **Never commit** `google-services.json` to public repositories
- 📝 Add to `.gitignore`: `google-services.json`
- 🔑 Store server keys as environment variables in production
- 🔄 Implement token refresh logic in `onNewToken()`
- ✅ Validate notification payloads server-side
- 🔒 Use HTTPS for all server communications

## 💡 Pro Tips

### Topic Naming
- Use lowercase with hyphens: `news-updates`, `sports-alerts`
- Avoid spaces and special characters
- Keep names descriptive and meaningful

### Production Checklist
- [ ] Implement backend service for token management
- [ ] Store tokens securely in database
- [ ] Handle `onNewToken()` for token refresh
- [ ] Add notification channels (Android 8.0+)
- [ ] Implement analytics for delivery tracking
- [ ] Test on multiple Android versions
- [ ] Configure proper notification icons and colors

## 📚 Learning Resources

- [Firebase Cloud Messaging Docs](https://firebase.google.com/docs/cloud-messaging)
- [Android FCM Client Guide](https://firebase.google.com/docs/cloud-messaging/android/client)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [FCM HTTP v1 API](https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages)

## 🤝 Contributing

Contributions are welcome! Feel free to:
- 🐛 Report bugs via Issues
- ✨ Suggest new features
- 🔧 Submit pull requests
- 📖 Improve documentation

## 📄 License

This project is available for educational purposes. Feel free to use and modify.

---

**Need Help?** Check [Stack Overflow](https://stackoverflow.com/questions/tagged/firebase-cloud-messaging) or [Firebase Community](https://firebase.google.com/community)

Made with ❤️ using Kotlin & Jetpack Compose
