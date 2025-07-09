package com.strem.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.strem.app.ui.details.DetailsScreen
import com.strem.app.ui.home.HomeScreen
import com.strem.app.ui.player.PlayerScreen
import com.strem.app.ui.search.SearchScreen
import com.strem.app.ui.settings.SettingsScreen
import com.strem.app.ui.sources.SourcesScreen
import com.strem.app.ui.theme.StremTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StremTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF141414))
                ) {
                    val navController = rememberNavController()
                    StremNavHost(navController = navController)
                }
            }
        }
    }
}

@Composable
fun StremNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("search") {
            SearchScreen(navController = navController)
        }
        composable("details/{contentId}/{contentType}") { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
            val contentType = backStackEntry.arguments?.getString("contentType") ?: ""
            DetailsScreen(
                navController = navController,
                contentId = contentId,
                contentType = contentType
            )
        }
        composable("sources/{contentId}/{contentType}?seasonNumber={seasonNumber}&episodeNumber={episodeNumber}") { backStackEntry ->
            val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
            val contentType = backStackEntry.arguments?.getString("contentType") ?: ""
            val seasonNumber = backStackEntry.arguments?.getString("seasonNumber")?.toIntOrNull()
            val episodeNumber = backStackEntry.arguments?.getString("episodeNumber")?.toIntOrNull()
            SourcesScreen(
                navController = navController,
                contentId = contentId,
                contentType = contentType,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber
            )
        }
        composable("player/{url}?contentId={contentId}&contentType={contentType}&seasonNumber={seasonNumber}&episodeNumber={episodeNumber}") { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            val contentId = backStackEntry.arguments?.getString("contentId") ?: ""
            val contentType = backStackEntry.arguments?.getString("contentType") ?: ""
            val seasonNumber = backStackEntry.arguments?.getString("seasonNumber")?.toIntOrNull()
            val episodeNumber = backStackEntry.arguments?.getString("episodeNumber")?.toIntOrNull()
            PlayerScreen(
                navController = navController,
                url = url,
                contentId = contentId,
                contentType = contentType,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber
            )
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
}