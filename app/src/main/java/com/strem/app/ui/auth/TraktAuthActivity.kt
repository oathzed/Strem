package com.strem.app.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.strem.app.R
import com.strem.app.data.repository.TraktRepository
import com.strem.app.ui.MainActivity
import com.strem.app.ui.components.LoadingView
import com.strem.app.ui.theme.StremTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TraktAuthActivity : ComponentActivity() {
    
    private val traktRepository: TraktRepository by inject()
    
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle the incoming intent
        val uri = intent?.data
        if (uri != null && uri.scheme == "strem" && uri.host == "auth") {
            // Extract the auth code from the URI
            val code = uri.getQueryParameter("code")
            
            setContent {
                StremTheme {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (code != null) {
                            AuthorizingScreen(code)
                        } else {
                            ErrorScreen(stringResource(R.string.error_trakt_auth))
                        }
                    }
                }
            }
            
            if (code != null) {
                // Exchange the code for an access token
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        traktRepository.getAccessToken(code)
                        
                        // Navigate back to the main activity
                        val intent = Intent(this@TraktAuthActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                        
                    } catch (e: Exception) {
                        // Handle error
                        e.printStackTrace()
                    }
                }
            }
        } else {
            // No valid auth data, redirect to Trakt for authorization
            CoroutineScope(Dispatchers.IO).launch {
                val authUrl = traktRepository.getAuthorizationUrl()
                
                // Open the browser with the auth URL
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
                startActivity(intent)
                finish()
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AuthorizingScreen(code: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "Authorizing with Trakt...",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LoadingView()
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ErrorScreen(error: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text(
            text = "Authorization Error",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}