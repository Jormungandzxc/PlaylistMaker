package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.ui.SearchActivity.Companion

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val _stateLiveData = MutableLiveData<SearchState>()
    val stateLiveData: LiveData<SearchState> = _stateLiveData


    private val handler = Handler(Looper.getMainLooper())
    private var latestSearchText: String? = null

    //Отложенный поисковой запрос
    private val searchRunnable = Runnable {
        val newSearchText = latestSearchText ?: ""
        if (newSearchText.isNotEmpty()) {
            searchRequest(newSearchText)
        }
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) return
        this.latestSearchText = changedText

        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            _stateLiveData.postValue(SearchState.Loading)

            tracksInteractor.searchTracks(
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?) {
                        handler.post {
                            if (foundTracks != null) {
                                if (foundTracks.isNotEmpty()) {
                                    _stateLiveData.postValue(SearchState.Content(foundTracks))
                                } else {
                                    _stateLiveData.postValue(SearchState.Empty(""))
                                }
                            } else {
                                _stateLiveData.postValue(SearchState.Error(""))
                            }
                        }
                    }
                })
        }
    }

    fun showHistory(){
        val history = searchHistoryInteractor.getHistory()
        if(history.isNotEmpty()){
            _stateLiveData.postValue(SearchState.History(history))
        } else{
            _stateLiveData.postValue(SearchState.Content(emptyList()))
        }
    }

    fun saveTrackToHistory(track: Track){
        searchHistoryInteractor.saveTrack(track)
    }

    fun clearHistory(){
        searchHistoryInteractor.clearHistory()
        _stateLiveData.postValue(SearchState.Content(emptyList()))
    }

    fun clearSearch(){
        handler.removeCallbacks(searchRunnable)
        latestSearchText = ""
        showHistory()
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

}