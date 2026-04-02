# Reel-Meter

**Reel-Meter** is a unique Android application designed to promote digital wellness by gamifying the "doomscrolling" habit. It measures the physical distance your thumb travels while scrolling through Instagram Reels and compares it to real-world landmarks.

---

## Features

* **Real-time Scroll Tracking:** Uses Android Accessibility Services to calculate scroll distance in meters and kilometers.
* **Gamified Progress:** Visualize your scroll distance by "climbing" landmarks like the Eiffel Tower, Burj Khalifa, and Mount Everest.
* **Background Service:** Tracks your journey even when the app is minimized.
* **Privacy-First:** No personal data is collected or stored. The app only monitors scroll events within the Instagram app.

## Built With

* **Kotlin** - Modern language for Android development.
* **Android SDK** - Accessibility Service API, Broadcast Receivers, and SharedPreferences.
* **ViewBinding** - For efficient and safe UI component interaction.

## Screenshots

| Main Dashboard | Landmark Progress |
|---|---|
| ![Logo](https://github.com/musharaf04/Reel-Meter/blob/master/app_logo.jpg?raw=true) | *Add a screenshot here later!* |

## 🛠️ How it Works (Technical)

The app utilizes the `AccessibilityService` API to listen for `TYPE_VIEW_SCROLLED` events specifically from the Instagram package. It calculates the pixel-to-meter conversion based on standard screen density and broadcasts the updates to a UI-bound `BroadcastReceiver`.

---
*Developed by Musharaf Shaik as a project to explore background services and custom UI in Android.*
