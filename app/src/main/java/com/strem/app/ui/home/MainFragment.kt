package com.strem.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.strem.app.R
import com.strem.app.ui.details.DetailsActivity
import com.strem.app.ui.search.SearchActivity
import com.strem.app.ui.settings.SettingsActivity
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.ArrayObjectAdapter

/**
 * Main fragment for the home screen.
 * This fragment displays the main content categories and rows.
 */
class MainFragment : BrowseSupportFragment() {

    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUIElements()
        setupEventListeners()
        loadRows()
    }

    private fun setupUIElements() {
        title = getString(R.string.app_name)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        
        // Set fastLane background color
        brandColor = ContextCompat.getColor(requireContext(), R.color.primary)
        
        // Set search icon color
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.accent)
    }

    private fun setupEventListeners() {
        setOnSearchClickedListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }
        
        onItemViewClickedListener = ItemViewClickedListener()
    }

    private fun loadRows() {
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter
        
        // Add Continue Watching row
        addContinueWatchingRow()
        
        // Add Trending row
        addTrendingRow()
        
        // Add Popular row
        addPopularRow()
        
        // Add My Lists row (from Trakt)
        addMyListsRow()
        
        // Add Recommended row
        addRecommendedRow()
    }

    private fun addContinueWatchingRow() {
        val headerItem = HeaderItem(0, getString(R.string.continue_watching))
        val rowAdapter = ArrayObjectAdapter(CardPresenter())
        
        // TODO: Add actual continue watching items from Trakt
        // This is just placeholder data
        for (i in 1..10) {
            rowAdapter.add(MovieItem("Movie $i", "https://picsum.photos/200/300?random=$i"))
        }
        
        rowsAdapter.add(ListRow(headerItem, rowAdapter))
    }

    private fun addTrendingRow() {
        val headerItem = HeaderItem(1, getString(R.string.trending))
        val rowAdapter = ArrayObjectAdapter(CardPresenter())
        
        // TODO: Add actual trending items
        // This is just placeholder data
        for (i in 11..20) {
            rowAdapter.add(MovieItem("Trending $i", "https://picsum.photos/200/300?random=$i"))
        }
        
        rowsAdapter.add(ListRow(headerItem, rowAdapter))
    }

    private fun addPopularRow() {
        val headerItem = HeaderItem(2, getString(R.string.popular))
        val rowAdapter = ArrayObjectAdapter(CardPresenter())
        
        // TODO: Add actual popular items
        // This is just placeholder data
        for (i in 21..30) {
            rowAdapter.add(MovieItem("Popular $i", "https://picsum.photos/200/300?random=$i"))
        }
        
        rowsAdapter.add(ListRow(headerItem, rowAdapter))
    }

    private fun addMyListsRow() {
        val headerItem = HeaderItem(3, getString(R.string.my_lists))
        val rowAdapter = ArrayObjectAdapter(CardPresenter())
        
        // TODO: Add actual items from Trakt lists
        // This is just placeholder data
        for (i in 31..40) {
            rowAdapter.add(MovieItem("List Item $i", "https://picsum.photos/200/300?random=$i"))
        }
        
        rowsAdapter.add(ListRow(headerItem, rowAdapter))
    }

    private fun addRecommendedRow() {
        val headerItem = HeaderItem(4, getString(R.string.recommended))
        val rowAdapter = ArrayObjectAdapter(CardPresenter())
        
        // TODO: Add actual recommended items
        // This is just placeholder data
        for (i in 41..50) {
            rowAdapter.add(MovieItem("Recommended $i", "https://picsum.photos/200/300?random=$i"))
        }
        
        rowsAdapter.add(ListRow(headerItem, rowAdapter))
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            if (item is MovieItem) {
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.MOVIE_ITEM, item)
                startActivity(intent)
            }
        }
    }
}

/**
 * Data class representing a movie or TV show item.
 */
data class MovieItem(
    val title: String,
    val posterUrl: String,
    val id: String = "",
    val description: String = "",
    val year: Int = 0,
    val rating: Float = 0f,
    val type: String = "movie" // "movie" or "show"
) : java.io.Serializable