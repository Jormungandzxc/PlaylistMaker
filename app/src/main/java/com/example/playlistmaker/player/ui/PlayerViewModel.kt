package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.PlayerInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val previewUrl: String,
    private val playerInteractor: PlayerInteractor
) : ViewModel(){

    companion object{
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_DELAY_MILLIS = 300L
    }

    private var playerState = STATE_DEFAULT

    //LiveData для состояния плеера
    private val _playerStateLiveData = MutableLiveData<Int>()
    val playerStateLiveData: LiveData<Int> = _playerStateLiveData

    //LiveData для времени воспроизведения
    private val _timerLiveData = MutableLiveData<String>()
    val timerLiveData: LiveData<String> = _timerLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy{
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    //Runnable для обновления времени
    private val updateTimerRunnable = object : Runnable{
        override fun run() {
            if(playerState == STATE_PLAYING){
                val currentPosition = playerInteractor.getCurrentPosition()
                _timerLiveData.postValue(dateFormat.format(currentPosition))
                handler.postDelayed(this, TIMER_DELAY_MILLIS)
            }
        }
    }

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        if(previewUrl.isEmpty()) return

        playerInteractor.preparePlayer(
            previewUrl = previewUrl,
            onPrepared = {
                playerState = STATE_PREPARED
                _playerStateLiveData.postValue(STATE_PREPARED)
            },
            onCompletion = {
                playerState = STATE_PREPARED
                _playerStateLiveData.postValue(STATE_PREPARED)
                handler.removeCallbacks(updateTimerRunnable)
                _timerLiveData.postValue("00:00")
            }
        )
    }

    fun playbackControl(){
        when(playerState){
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun  startPlayer(){
        playerInteractor.startPlayer()
        playerState = STATE_PLAYING
        _playerStateLiveData.postValue(STATE_PLAYING)
        handler.post(updateTimerRunnable)
    }

    fun pausePlayer(){
        playerInteractor.pausePlayer()
        playerState = STATE_PAUSED
        _playerStateLiveData.postValue(STATE_PAUSED)
        handler.removeCallbacks(updateTimerRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        handler.removeCallbacks(updateTimerRunnable)
    }


}