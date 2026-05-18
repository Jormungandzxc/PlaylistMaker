package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity:AppCompatActivity() {
    companion object{
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var playButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

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
            preparePlayer(it.previewUrl)
        }

        playButton.setOnClickListener{
            playbackControl(playButton)
        }

        //бегущая строка альбома
        albumValue.isSelected = true

    }

    override fun onPause() {
        super.onPause()
        pausePlayer(playButton)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    //Подготовка плеера
    private fun preparePlayer(url: String?){
        if (url.isNullOrEmpty()) return

        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener{
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
        }
    }

    //Измеенение состояния плеера
    private fun  startPlayer(playButton: ImageButton){
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.ic_pause_btn)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer(playButton: ImageButton){
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.ic_play_btn)
        playerState = STATE_PAUSED
    }

    private fun playbackControl(playButton: ImageButton){
        when(playerState){
            STATE_PLAYING -> {
                pausePlayer(playButton)
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer(playButton)
            }
        }
    }

}