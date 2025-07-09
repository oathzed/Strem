package com.strem.app.ui.player

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.strem.app.R
import com.strem.app.ui.home.MovieItem

/**
 * Activity for playing video content.
 */
class PlayerActivity : FragmentActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var movieItem: MovieItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        
        movieItem = intent.getSerializableExtra(MOVIE_ITEM) as MovieItem
        
        initializePlayer()
    }

    private fun initializePlayer() {
        playerView = findViewById(R.id.player_view)
        
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        
        // TODO: Get actual stream URL from Stremio addon
        // This is just a placeholder URL
        val mediaItem = MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        player.setMediaItem(mediaItem)
        
        player.playWhenReady = true
        player.prepare()
        
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    // TODO: Save progress to Trakt
                    finish()
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        player.pause()
        
        // TODO: Save progress to Trakt
    }

    override fun onResume() {
        super.onResume()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    companion object {
        const val MOVIE_ITEM = "movie_item"
    }
}