package com.strem.app.ui.search

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.strem.app.R

/**
 * Activity for searching movies and TV shows.
 */
class SearchActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.search_fragment, SearchFragment())
                .commitNow()
        }
    }
}