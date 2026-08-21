package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.ITunesRequest
import com.example.playlistmaker.data.dto.ITunesResponse
import com.example.playlistmaker.data.dto.mapToDomain
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl (private val networkClient: NetworkClient): TracksRepository{
    override fun searchTracks(expression: String): List<Track>? {
        val response = networkClient.doRequest(ITunesRequest(expression))
        return if (response.resultCode == 200){
             (response as ITunesResponse).results.map {it.mapToDomain()}
        }else{
            null
        }
    }
}