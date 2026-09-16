package com.example.playlistmaker.player.ui

sealed interface PlayerState {

    data class Default(val progress: String = "00:00") : PlayerState

    data class Playing(val progress: String) : PlayerState

    data class Paused(val progress: String) : PlayerState

    data class Prepared(val progress: String = "00:00") : PlayerState
}