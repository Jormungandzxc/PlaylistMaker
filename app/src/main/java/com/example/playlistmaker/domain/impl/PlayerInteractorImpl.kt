package com.example.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.PlayerInteractor

class PlayerInteractorImpl : PlayerInteractor{
    private val mediaPlayer = MediaPlayer()
    private var isPlayerPrepared = false

    override fun preparePlayer(
        previewUrl: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit
    ) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener{
            isPlayerPrepared = true
            onPrepared()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion()
        }
    }

    override fun startPlayer() {
        if(isPlayerPrepared){
            mediaPlayer.start()
        }
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
}