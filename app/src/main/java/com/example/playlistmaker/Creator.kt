package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(): TracksRepository{
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor{
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository{
        val sharedPrefs = context.getSharedPreferences("playlist_maker_prefs", Context.MODE_PRIVATE)
        return SearchHistoryRepositoryImpl(sharedPrefs)
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor{
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    //Настройки темы
    private fun getThemeRepository(context: Context): ThemeRepository{
        val sharedPrefs = context.getSharedPreferences(App.PLAYLIST_MAKER_PREFS, Context.MODE_PRIVATE)
        return ThemeRepositoryImpl(sharedPrefs)
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor{
        return ThemeInteractorImpl(getThemeRepository(context))
    }

    //Плеер
    fun providePlayerInteractor(): PlayerInteractor{
        return PlayerInteractorImpl()
    }
}