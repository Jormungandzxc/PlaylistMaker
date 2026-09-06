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
) : ViewModel() {

    companion object {
        private const val DEFAULT_TIME = "00:00"
        private const val TIMER_DELAY_MILLIS = 300L
    }

    //LiveData для состояния плеера
    private val _playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerStateLiveData: LiveData<PlayerState> = _playerStateLiveData


    private val handler = Handler(Looper.getMainLooper())

    private val dateFormat by lazy {
        SimpleDateFormat("mm:ss", Locale.getDefault())
    }

    //Runnable для обновления времени
    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            val currentState = _playerStateLiveData.value
            if (currentState is PlayerState.Playing) {
                val currentPosition = playerInteractor.getCurrentPosition()
                val formattedTime = dateFormat.format(currentPosition)
                _playerStateLiveData.postValue(PlayerState.Playing(formattedTime))
                handler.postDelayed(this, TIMER_DELAY_MILLIS)
            }
        }
    }

    init {
        preparePlayer()
    }

    private fun preparePlayer() {
        if (previewUrl.isEmpty()) return

        playerInteractor.preparePlayer(
            previewUrl = previewUrl,
            onPrepared = {
                _playerStateLiveData.postValue(PlayerState.Prepared(DEFAULT_TIME))
            },
            onCompletion = {
                handler.removeCallbacks(updateTimerRunnable)
                _playerStateLiveData.postValue(PlayerState.Prepared(DEFAULT_TIME))
            }
        )
    }

    fun playbackControl() {
        when (_playerStateLiveData.value) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        playerInteractor.startPlayer()
        val currentProgress = getCurrentProgressTime()
        _playerStateLiveData.value = PlayerState.Playing(currentProgress)
        handler.post(updateTimerRunnable)
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        handler.removeCallbacks(updateTimerRunnable)
        val currentProgress = getCurrentProgressTime()
        _playerStateLiveData.value = PlayerState.Paused(currentProgress)
    }

    private fun getCurrentProgressTime(): String{
        return dateFormat.format(playerInteractor.getCurrentPosition())
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.releasePlayer()
        handler.removeCallbacks(updateTimerRunnable)
    }


}