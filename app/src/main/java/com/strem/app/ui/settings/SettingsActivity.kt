package com.strem.app.ui.settings

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.strem.app.R

/**
 * Activity for app settings.
 */
class SettingsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_fragment, SettingsFragment())
                .commitNow()
        }
    }
}