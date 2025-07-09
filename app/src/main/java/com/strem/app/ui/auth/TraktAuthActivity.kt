package com.strem.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.strem.app.R
import com.strem.app.data.trakt.TraktRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity for handling Trakt authentication callback.
 */
class TraktAuthActivity : FragmentActivity() {

    private lateinit var traktRepository: TraktRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        
        traktRepository = TraktRepository(this)
        
        // Handle the OAuth callback
        handleIntent(intent)
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent) {
        val uri = intent.data
        
        if (uri != null && uri.scheme == "strem" && uri.host == "auth") {
            val code = uri.getQueryParameter("code")
            
            if (code != null) {
                // Exchange code for token
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            traktRepository.getToken(code)
                        }
                        
                        if (result.isSuccess) {
                            Toast.makeText(
                                this@TraktAuthActivity,
                                "Successfully logged in to Trakt",
                                Toast.LENGTH_SHORT
                            ).show()
                            
                            // Return to settings
                            val settingsIntent = Intent(this@TraktAuthActivity, Class.forName("com.strem.app.ui.settings.SettingsActivity"))
                            settingsIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(settingsIntent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@TraktAuthActivity,
                                "Failed to login: ${result.exceptionOrNull()?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            finish()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@TraktAuthActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Authentication failed: No code received",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        } else {
            finish()
        }
    }
}