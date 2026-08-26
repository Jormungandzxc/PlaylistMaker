package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.search.domain.Track
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity:AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var timerTextView: TextView
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val toolbar = findViewById<MaterialToolbar>(R.id.playerToolbar)
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

        //Иницилизация ViewModel через Factory
        val previewUrl = track?.previewUrl.orEmpty()
        val factory = PlayerViewModelFactory(previewUrl, Creator.providePlayerInteractor())
        viewModel = ViewModelProvider(this, factory)[PlayerViewModel::class.java]

        //Подписка на LiveData состояние плеера
        viewModel.playerStateLiveData.observe(this){state ->
            when(state){
                1, 3 -> {
                    playButton.isEnabled = true
                    playButton.setImageResource(R.drawable.ic_play_btn)
                }
                2 -> {
                    playButton.isEnabled = true
                    playButton.setImageResource(R.drawable.ic_pause_btn)
                }
            }
        }

        //Подписка на LiveData времени трека
        viewModel.timerLiveData.observe(this){time ->
            timerTextView.text = time
        }


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

        }

        playButton.isEnabled = false
        playButton.setOnClickListener{
            viewModel.playbackControl()
        }

        //бегущая строка альбома
        albumValue.isSelected = true

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }






}