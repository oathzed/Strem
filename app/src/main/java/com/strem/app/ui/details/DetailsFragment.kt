package com.strem.app.ui.details

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.strem.app.R
import com.strem.app.ui.home.MovieItem
import com.strem.app.ui.player.PlayerActivity

/**
 * Fragment for displaying movie or TV show details.
 */
class DetailsFragment : DetailsSupportFragment() {

    private lateinit var movieItem: MovieItem
    private lateinit var detailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var rowsAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        movieItem = arguments?.getSerializable(ARG_MOVIE_ITEM) as MovieItem
        
        detailsBackground = DetailsSupportFragmentBackgroundController(this)
        
        setupUI()
        setupDetailsOverviewRow()
        setupRelatedMovieListRow()
    }

    private fun setupUI() {
        title = movieItem.title
        
        // Set item details background
        detailsBackground.enableParallax()
        
        // Set onItemViewClickedListener
        setOnItemViewClickedListener { itemViewHolder, item, _, _ ->
            if (item is Action) {
                when (item.id) {
                    ACTION_WATCH -> {
                        val intent = Intent(activity, PlayerActivity::class.java)
                        intent.putExtra(PlayerActivity.MOVIE_ITEM, movieItem)
                        startActivity(intent)
                    }
                    ACTION_ADD_TO_LIST -> {
                        // TODO: Implement add to list functionality
                    }
                }
            } else if (item is MovieItem) {
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.MOVIE_ITEM, item)
                startActivity(intent)
            }
        }
    }

    private fun setupDetailsOverviewRow() {
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        
        // Set background color
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.background_dark)
        detailsPresenter.backgroundColor = backgroundColor
        
        // Set initial and state transitions
        val fadeAndSlideTransition = DetailsOverviewRowPresenter.FadeAndSlideLeftTransition()
        detailsPresenter.setSharedElementEnterTransition(activity, SHARED_ELEMENT_NAME)
        
        detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
            when (action.id) {
                ACTION_WATCH -> {
                    val intent = Intent(activity, PlayerActivity::class.java)
                    intent.putExtra(PlayerActivity.MOVIE_ITEM, movieItem)
                    startActivity(intent)
                }
                ACTION_ADD_TO_LIST -> {
                    // TODO: Implement add to list functionality
                }
            }
        }
        
        val detailsOverviewRow = DetailsOverviewRow(movieItem)
        
        // Load the poster image
        val width = DetailsOverviewRowPresenter.DETAIL_THUMB_WIDTH
        val height = DetailsOverviewRowPresenter.DETAIL_THUMB_HEIGHT
        
        Glide.with(requireActivity())
            .load(movieItem.posterUrl)
            .centerCrop()
            .error(R.drawable.default_background)
            .into(object : CustomTarget<Drawable>(width, height) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    detailsOverviewRow.imageDrawable = resource
                    detailsBackground.coverBitmap = resource.toBitmap()
                }
                
                override fun onLoadCleared(placeholder: Drawable?) {
                    // Do nothing
                }
            })
        
        // Add actions
        val actionAdapter = SparseArrayObjectAdapter()
        actionAdapter.set(
            ACTION_WATCH.toInt(),
            Action(
                ACTION_WATCH,
                getString(R.string.watch_now)
            )
        )
        actionAdapter.set(
            ACTION_ADD_TO_LIST.toInt(),
            Action(
                ACTION_ADD_TO_LIST,
                getString(R.string.add_to_list)
            )
        )
        detailsOverviewRow.actionsAdapter = actionAdapter
        
        // Setup adapter
        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        rowsAdapter.add(detailsOverviewRow)
        adapter = rowsAdapter
    }

    private fun setupRelatedMovieListRow() {
        val header = HeaderItem(0, getString(R.string.similar))
        val rowAdapter = ArrayObjectAdapter(CardPresenter())
        
        // TODO: Add actual similar items
        // This is just placeholder data
        for (i in 1..10) {
            rowAdapter.add(MovieItem("Similar $i", "https://picsum.photos/200/300?random=$i"))
        }
        
        rowsAdapter.add(ListRow(header, rowAdapter))
    }

    companion object {
        private const val ARG_MOVIE_ITEM = "movie_item"
        private const val SHARED_ELEMENT_NAME = "hero"
        private const val ACTION_WATCH = 1L
        private const val ACTION_ADD_TO_LIST = 2L
        
        fun newInstance(movieItem: MovieItem): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putSerializable(ARG_MOVIE_ITEM, movieItem)
            fragment.arguments = args
            return fragment
        }
    }
    
    /**
     * Card presenter for displaying similar movies.
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
            cardView.contentText = movie.year.toString()
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

/**
 * Presenter for displaying movie details.
 */
class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {
    
    override fun onBindDescription(viewHolder: ViewHolder, item: Any) {
        val movie = item as MovieItem
        
        viewHolder.title.text = movie.title
        viewHolder.subtitle.text = "${movie.year} • ${movie.rating}/10"
        viewHolder.body.text = movie.description
    }
}