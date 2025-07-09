package com.strem.app.ui.search

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.leanback.app.SearchSupportFragment
import androidx.leanback.widget.*
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.strem.app.R
import com.strem.app.ui.details.DetailsActivity
import com.strem.app.ui.home.MovieItem

/**
 * Fragment for searching movies and TV shows.
 */
class SearchFragment : SearchSupportFragment(), SearchSupportFragment.SearchResultProvider {

    private val handler = Handler(Looper.getMainLooper())
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private val defaultSearchTimeout = 300L
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setSearchResultProvider(this)
        setOnItemViewClickedListener { _, item, _, _ ->
            if (item is MovieItem) {
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.MOVIE_ITEM, item)
                startActivity(intent)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set search background color
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background_dark))
    }

    override fun getResultsAdapter(): ObjectAdapter {
        return rowsAdapter
    }

    override fun onQueryTextChange(newQuery: String): Boolean {
        searchRunnable?.let { handler.removeCallbacks(it) }
        
        if (newQuery.isEmpty()) {
            rowsAdapter.clear()
            return true
        }
        
        searchRunnable = Runnable {
            // TODO: Implement actual search using Stremio addons
            performSearch(newQuery)
        }
        
        handler.postDelayed(searchRunnable, defaultSearchTimeout)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        searchRunnable?.let { handler.removeCallbacks(it) }
        
        if (query.isEmpty()) {
            rowsAdapter.clear()
            return true
        }
        
        // TODO: Implement actual search using Stremio addons
        performSearch(query)
        return true
    }

    private fun performSearch(query: String) {
        rowsAdapter.clear()
        
        // Mock search results
        val headerItem = HeaderItem(0, "Search Results")
        val listRowAdapter = ArrayObjectAdapter(CardPresenter())
        
        // Add mock results
        for (i in 1..10) {
            listRowAdapter.add(
                MovieItem(
                    "Result $i for $query",
                    "https://picsum.photos/200/300?random=$i",
                    description = "This is a search result for $query"
                )
            )
        }
        
        rowsAdapter.add(ListRow(headerItem, listRowAdapter))
    }

    /**
     * Card presenter for displaying search results.
     */
    private inner class CardPresenter : Presenter() {
        private var defaultCardImage: Drawable? = null
        private var defaultBackgroundColor = 0
        private var selectedBackgroundColor = 0
        
        override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
            defaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.card_background)
            selectedBackgroundColor = ContextCompat.getColor(parent.context, R.color.card_background_selected)
            defaultCardImage = ContextCompat.getDrawable(parent.context, R.drawable.default_background)
            
            val cardView = object : ImageCardView(parent.context) {
                override fun setSelected(selected: Boolean) {
                    updateCardBackgroundColor(this, selected)
                    super.setSelected(selected)
                }
            }
            
            cardView.isFocusable = true
            cardView.isFocusableInTouchMode = true
            updateCardBackgroundColor(cardView, false)
            return ViewHolder(cardView)
        }
        
        override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
            val movie = item as MovieItem
            val cardView = viewHolder.view as ImageCardView
            
            cardView.titleText = movie.title
            cardView.contentText = movie.description
            cardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
            
            Glide.with(cardView.context)
                .load(movie.posterUrl)
                .centerCrop()
                .error(defaultCardImage)
                .into(cardView.mainImageView)
        }
        
        override fun onUnbindViewHolder(viewHolder: ViewHolder) {
            val cardView = viewHolder.view as ImageCardView
            cardView.badgeImage = null
            cardView.mainImage = null
        }
        
        private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
            val color = if (selected) selectedBackgroundColor else defaultBackgroundColor
            view.setBackgroundColor(color)
            view.setInfoAreaBackgroundColor(color)
        }
        
        companion object {
            private const val CARD_WIDTH = 313
            private const val CARD_HEIGHT = 176
        }
    }
}