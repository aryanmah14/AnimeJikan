# AnimeJikan 📺

AnimeJikan is a modern Android application for exploring anime, powered by the Jikan API. It provides a smooth experience for browsing popular series and watching their trailers.

## ✨ Features

- **Home Screen**: Browse a curated list of top-ranked anime series.
- **Detailed View**: Access comprehensive information including synopsis, scores, episodes, and genres.
- **Native Trailer Playback**: Integrated `YouTubePlayerView` for direct in-app trailer watching.
- **Smart Fallback Mechanism**:
  - Automatically extracts video IDs from YouTube embed URLs.
  - Handles content-restricted videos (Error 152) by providing a **Watch on YouTube** button for external playback.
- **Offline Availability**: Powered by **Room Database**, allowing you to view previously loaded anime even without an internet connection.
- **Modern Tech Stack**:
  - **Kotlin First**
  - **MVVM Architecture**
  - **Hilt** (Dependency Injection)
  - **Retrofit & OkHttp** (Networking)
  - **Glide** (Image Loading)
  - **View Binding**

## 📸 Screenshots

| Home Screen | Anime Details 
|------------|---------------
| ![Home Screen](screenshots/home.png) | ![Anime Details](screenshots/details.png)

## 📝 Assumptions

1. **API Reliability**: The app assumes the Jikan API (v4) is accessible.
2. **Connectivity**: While some data is cached offline, an initial connection is required to fetch content.
3. **External Player**: For restricted trailers, it is assumed the user has a YouTube app or a web browser installed to handle external video links.

## ⚠️ Known Limitations

1. **Embedded Playback**: Some trailers are restricted by their owners for embedded playback. In such cases, the app provides a fallback button rather than attempting to force playback.
2. **Search**: This version focuses on discovery via the **Top Anime** list; manual search is not yet implemented.
3. **Data Refresh**: Manual pull-to-refresh is not implemented; data is refreshed on app launch.

## 🛠️ Installation & Setup

1. Clone or download this repository.
2. Open the project in **Android Studio**.
3. Sync Gradle and build the project.
4. Run the app on an emulator or a physical Android device.

## 📄 API Reference

- Jikan API (v4)  
  https://docs.api.jikan.moe/

---

*Developed as part of a technical exercise for anime discovery and Android development best practices.*


