package com.strem.app.ui.details

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.strem.app.R
import com.strem.app.ui.home.MovieItem

/**
 * Details activity for displaying movie or TV show details.
 */
class DetailsActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        
        if (savedInstanceState == null) {
            val movieItem = intent.getSerializableExtra(MOVIE_ITEM) as MovieItem
            val fragment = DetailsFragment.newInstance(movieItem)
            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, fragment)
                .commitNow()
        }
    }

    companion object {
        const val MOVIE_ITEM = "movie_item"
    }
}