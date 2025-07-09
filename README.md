# Strem - Android TV Streaming App

Strem is an Android TV streaming application that integrates with Stremio addons and Trakt.tv for a seamless streaming experience.

## Features

- Netflix-like UI designed specifically for Android TV
- Integration with Stremio addons for content streaming
- Trakt.tv authentication for watchlist, collection, and progress syncing
- Continue watching section to resume content
- Search functionality for movies and TV shows
- Detailed information about movies and TV shows
- Video player with subtitle support

## Setup

### Prerequisites

- Android Studio Arctic Fox or newer
- Android TV device or emulator running Android 8.0 (API 26) or higher
- TMDB API key
- Trakt.tv API credentials

### API Keys

To run the app, you need to provide your own API keys:

1. TMDB API key: Get it from [TMDB](https://www.themoviedb.org/settings/api)
2. Trakt Client ID and Secret: Register your app at [Trakt.tv](https://trakt.tv/oauth/applications)

Add these keys to the `app/build.gradle` file:

```gradle
buildTypes {
    debug {
        buildConfigField "String", "TMDB_API_KEY", "\"YOUR_TMDB_API_KEY\""
        buildConfigField "String", "TRAKT_CLIENT_ID", "\"YOUR_TRAKT_CLIENT_ID\""
        buildConfigField "String", "TRAKT_CLIENT_SECRET", "\"YOUR_TRAKT_CLIENT_SECRET\""
    }
    
    release {
        // ...
        buildConfigField "String", "TMDB_API_KEY", "\"YOUR_TMDB_API_KEY\""
        buildConfigField "String", "TRAKT_CLIENT_ID", "\"YOUR_TRAKT_CLIENT_ID\""
        buildConfigField "String", "TRAKT_CLIENT_SECRET", "\"YOUR_TRAKT_CLIENT_SECRET\""
    }
}
```

### Building the App

1. Clone the repository
2. Open the project in Android Studio
3. Add your API keys as described above
4. Build and run the app on your Android TV device or emulator

## Stremio Addon Support

Strem supports Stremio addons for streaming content. You can add addon URLs in the app settings. The app will fetch the addon manifest and use it to retrieve streams for movies and TV shows.

## Trakt.tv Integration

Strem integrates with Trakt.tv for:

- Syncing watchlist and collection
- Tracking watched content
- Displaying personalized recommendations

To use Trakt.tv integration, you need to authenticate with your Trakt.tv account in the app settings.

## Architecture

The app follows MVVM architecture with:

- Jetpack Compose for UI
- Koin for dependency injection
- Room for local database
- Retrofit for API calls
- Media3 ExoPlayer for video playback
- DataStore for preferences

## License

This project is licensed under the MIT License - see the LICENSE file for details.