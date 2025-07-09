# Strem - Android TV Streaming App

Strem is a Netflix-like streaming app for Android TV that integrates with Stremio addons and Trakt.tv.

## Features

- **Netflix-like UI**: Clean, modern interface designed specifically for Android TV
- **Stremio Addon Support**: Stream content from any Stremio addon
- **Trakt Integration**: Sync your watchlist, history, and progress with Trakt.tv
- **Continue Watching**: Resume where you left off
- **Personalized Recommendations**: Based on your viewing history
- **Multiple Categories**: Browse content by different categories
- **Search**: Find content across all your addons
- **Settings**: Customize the app to your preferences

## Technical Details

- Built for Android TV using Leanback library
- Kotlin-based application
- MVVM architecture
- Uses Retrofit for API calls
- ExoPlayer for video playback
- Room database for local storage

## Setup

### Prerequisites

- Android Studio
- Android SDK with TV support
- Java Development Kit (JDK)

### Building the App

1. Clone the repository
2. Open the project in Android Studio
3. Update the Trakt client ID and secret in `TraktRepository.kt`
4. Build and run the app on an Android TV device or emulator

## Stremio Addon Integration

Strem supports any Stremio addon that follows the Stremio addon protocol. To add an addon:

1. Go to Settings > Stremio Addons
2. Select "Add Addon"
3. Enter the addon URL
4. The app will fetch the addon manifest and add it to your collection

## Trakt Authentication

To enable Trakt integration:

1. Go to Settings > Account
2. Select "Login with Trakt"
3. Follow the on-screen instructions to authorize the app
4. Once authorized, your Trakt lists and history will be synced

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Stremio for their addon protocol
- Trakt.tv for their API
- The Android TV team for the Leanback library