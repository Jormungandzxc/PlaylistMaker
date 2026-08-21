package com.example.playlistmaker.presentation.player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity:AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var timerTextView: TextView
    private lateinit var playerInteractor: PlayerInteractor

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat by lazy{
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    private var playerState = STATE_DEFAULT

    companion object{
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_DELAY_MILLIS = 300L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        playerInteractor = Creator.providePlayerInteractor()

        val toolbar = findViewById<MaterialToolbar>(R.id.searchToolbar)
        val albumPlaceholder = findViewById<ImageView>(R.id.albumPlaceholder)
        val trackName = findViewById<TextView>(R.id.playerTrackName)
        val artistName = findViewById<TextView>(R.id.playerArtistName)

        val durationValue = findViewById<TextView>(R.id.trackDurationValue)
        val albumTitle = findViewById<TextView>(R.id.albumTitle)
        val albumValue = findViewById<TextView>(R.id.albumTitleValue)
        val yearTitle = findViewById<TextView>(R.id.trackYear)
        val yearValue = findViewById<TextView>(R.id.trackYearValue)
        val genreValue = findViewById<TextView>(R.id.trackGenreValue)
        val countryValue =  findViewById<TextView>(R.id.countryValue)

        playButton = findViewById(R.id.playButton)
        timerTextView = findViewById(R.id.track_timer)

        //ToolBar
        toolbar.setNavigationOnClickListener { finish() }


        //Получение объекта трека
        val track = intent.getSerializableExtra("selected_track") as? Track

        track?.let {
            trackName.text = it.trackName
            artistName.text = it.artistName
            genreValue.text = it.primaryGenreName
            countryValue.text = it.country
            durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis)

            if(it.releaseDate.isNullOrEmpty()){
                yearValue.visibility = View.GONE
                yearTitle.visibility = View.GONE
            }else{
                yearValue.text = it.releaseDate.substring(0, 4)
                yearValue.visibility = View.VISIBLE
                yearTitle.visibility = View.VISIBLE
            }

            if(it.collectionName.isNullOrEmpty()){
                albumValue.visibility = View.GONE
                albumTitle.visibility = View.GONE
            }else{
                albumValue.text = it.collectionName
                albumValue.visibility = View.VISIBLE
                albumTitle.visibility = View.VISIBLE
            }

            val radiusInPx = resources.getDimensionPixelSize(R.dimen.album_cover_corner_radius)
            Glide.with(this)
                .load(it.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder_player)
                .centerCrop()
                .transform(RoundedCorners(radiusInPx))
                .into(albumPlaceholder)

            //Подготовка плеера
            track.previewUrl?.let { url ->
                playerInteractor.preparePlayer(
                    previewUrl = url,
                    onPrepared = {
                        playButton.isEnabled = true
                        playerState = STATE_PREPARED
                        playButton.setImageResource(R.drawable.ic_play_btn)
                    },
                    onCompletion = {
                        playerState = STATE_PREPARED
                        playButton.setImageResource(R.drawable.ic_play_btn)
                        timerTextView.text =getString(R.string.timerTVText)
                        handler.removeCallbacks(updateTimerRunnable)
                    }
                )
            }
        }

        playButton.isEnabled = false
        playButton.setOnClickListener{
            playbackControl()
        }

        //бегущая строка альбома
        albumValue.isSelected = true

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.releasePlayer()
        handler.removeCallbacks(updateTimerRunnable)
    }

    //Измеенение состояния плеера
    private fun  startPlayer(){
        playerInteractor.startPlayer()
        playButton.setImageResource(R.drawable.ic_pause_btn)
        playerState = STATE_PLAYING
        handler.post(updateTimerRunnable)
    }

    private fun pausePlayer(){
        playerInteractor.pausePlayer()
        playButton.setImageResource(R.drawable.ic_play_btn)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimerRunnable)
    }

    private fun playbackControl(){
        when(playerState){
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    //Runnable для обновления времени
    private val updateTimerRunnable = object : Runnable{
        override fun run() {
            if(playerState == STATE_PLAYING){
                val currentPosition = playerInteractor.getCurrentPosition()
                timerTextView.text = dateFormat.format(currentPosition)
                handler.postDelayed(this, TIMER_DELAY_MILLIS)
            }
        }
    }



}