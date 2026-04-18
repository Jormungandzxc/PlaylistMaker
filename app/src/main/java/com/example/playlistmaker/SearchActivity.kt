package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : AppCompatActivity() {
    //Переменная для хранения текста
    private var searchText: String = ""

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private lateinit var adapter: TrackAdapter

    private lateinit var placeholderMessage: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var refreshButton: MaterialButton
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var historyLayout: ConstraintLayout

    private lateinit var searchHistory: SearchHistory
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var historyAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val searchEditText = findViewById<EditText>(R.id.editText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val toolbar = findViewById<MaterialToolbar>(R.id.searchToolbar)

        placeholderMessage = findViewById<LinearLayout>(R.id.placeholderMessage)
        placeholderImage = findViewById<ImageView>(R.id.placeholderImage)
        placeholderText = findViewById<TextView>(R.id.placeholderText)
        refreshButton = findViewById<MaterialButton>(R.id.refreshButton)
        trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)


        //История поиска
        val sharedPrefs = getSharedPreferences("playlist_maker_prefs", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)

        historyLayout = findViewById(R.id.historyLayout)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        historyAdapter = TrackAdapter(searchHistory.getHistory()){track -> searchHistory.addTrack(track)
            historyAdapter.updateTracks(searchHistory.getHistory())
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        adapter = TrackAdapter(tracks){track ->
            searchHistory.addTrack(track)
            historyAdapter.updateTracks(searchHistory.getHistory())
        }
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = adapter


        toolbar.setNavigationOnClickListener {
            finish()
        }

        //Восстановление текста в EditText.
        searchEditText.setText((searchText))

//        Очистка текстового поля
        clearButton.setOnClickListener{
            searchEditText.setText("")

            //Скрытие клавиатуры
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        val textWatcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearButton.visibility = if(s.isNullOrEmpty())
                    View.GONE else View.VISIBLE

                if(s.isNullOrEmpty()){
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                    showMessage("", "")
                }


                val history = searchHistory.getHistory()
                if(searchEditText.hasFocus() && s?.isEmpty() == true && history.isNotEmpty()){
                    historyLayout.visibility = View.VISIBLE
                    trackRecyclerView.visibility = View.GONE
                    placeholderMessage.visibility = View.GONE
                    historyAdapter.updateTracks(history)
                }else{
                    historyLayout.visibility = View.GONE
                    trackRecyclerView.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }

        searchEditText.addTextChangedListener(textWatcher)


        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                if(searchEditText.text.isNotEmpty()){
                    searchQuery(searchEditText.text.toString())
                }
                true
            }
            false
        }


        refreshButton.setOnClickListener{
            searchQuery(searchEditText.text.toString())
        }




        searchEditText.setOnFocusChangeListener{view, hasFocus ->
            val history = searchHistory.getHistory()
            historyLayout.visibility = if(hasFocus && searchEditText.text.isEmpty() && history.isNotEmpty()){
                View.VISIBLE
            }else{
                View.GONE
            }
        }

        //Фокус на поле ввода
        searchEditText.post{
            searchEditText.requestFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

        //Очистка истории
        clearHistoryButton.setOnClickListener{
            searchHistory.clearHistory()
            historyLayout.visibility = View.GONE
        }

    }

    //Сохранение данных EditText
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
    }

    companion object{
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }

    private fun searchQuery(text: String){
        iTunesService.search(text).enqueue(object : Callback<ITunesResponse>{
            override fun onResponse(
                call: Call<ITunesResponse>,
                response: Response<ITunesResponse>
            ) {
                if(response.code() == 200){
                    tracks.clear()
                    if(response.body()?.results?.isNotEmpty() == true){
                        tracks.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                        showMessage("", "")
                    } else{
                        showMessage(getString(R.string.nothing_found), "")
                    }
                } else{
                    showMessage(getString(R.string.something_went_wrong), "error")
                }
            }

            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                showMessage(getString(R.string.something_went_wrong), getString(R.string.something_went_wrong_additional_message))
            }
        })
    }

    //Функция для отображения заглушки
    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderText.text = text
            if (additionalMessage.isNotEmpty()) {
                placeholderImage.setImageResource(R.drawable.ic_error_connection)
                refreshButton.visibility = View.VISIBLE
            } else {
                placeholderImage.setImageResource(R.drawable.ic_not_found)
                refreshButton.visibility = View.GONE
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }
}