package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.dto.ITunesRequest
import com.example.playlistmaker.search.data.dto.ITunesResponse
import com.example.playlistmaker.search.data.dto.mapToDomain
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.Track

class TracksRepositoryImpl (private val networkClient: NetworkClient): TracksRepository {
    override fun searchTracks(expression: String): List<Track>? {
        val response = networkClient.doRequest(ITunesRequest(expression))
        return if (response.resultCode == 200){
             (response as ITunesResponse).results.map {it.mapToDomain()}
        }else{
            null
        }
    }
}