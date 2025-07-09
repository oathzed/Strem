package com.strem.app.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.strem.app.R
import com.strem.app.ui.home.MainFragment

/**
 * Main Activity for the Strem app.
 * This is the entry point of the application.
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainFragment())
                .commitNow()
        }
    }
}