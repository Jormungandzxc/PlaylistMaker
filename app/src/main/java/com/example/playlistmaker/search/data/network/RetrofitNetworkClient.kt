package com.example.playlistmaker.search.data.network

import android.content.Context
import com.example.playlistmaker.search.data.dto.ITunesRequest
import com.example.playlistmaker.search.data.dto.ITunesResponse
import com.example.playlistmaker.search.data.dto.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(
    private val iTunesService: ITunesApi,
    private val context: Context
): NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto !is ITunesRequest) return  Response().apply{resultCode = 400}

        return  try {
            val resp = iTunesService.search(dto.expression).execute()
            val body = resp.body() ?: ITunesResponse(resultCount = 0, results = emptyList())
            body.apply { resultCode = resp.code() }
        }catch (e: Exception){
            // Если нет интернета, возвращаем код -1
            Response().apply { resultCode = -1 }
        }
    }
}