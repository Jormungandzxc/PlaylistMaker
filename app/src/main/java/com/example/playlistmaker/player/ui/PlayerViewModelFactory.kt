package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.PlayerInteractor

class PlayerViewModelFactory (
    private val previewUrl: String,
    private val playerInteractor: PlayerInteractor
) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(previewUrl, playerInteractor) as T
    }
}