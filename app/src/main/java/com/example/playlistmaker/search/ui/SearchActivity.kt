package com.example.playlistmaker.search.ui

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
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.player.ui.PlayerActivity
import com.google.android.material.button.MaterialButton


class SearchActivity : AppCompatActivity() {
    //Переменная для хранения текста
    private var searchText: String = ""

    private val tracks = ArrayList<Track>()
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private lateinit var viewModel: SearchViewModel

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var placeholderMessage: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var refreshButton: MaterialButton
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var historyLayout: ConstraintLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var progressBar: ProgressBar


    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //ViewModel
        val factory = SearchViewModelFactory(
            Creator.provideTracksInteractor(),
            Creator.provideSearchHistoryInteractor(this)
        )
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]

        initViews()

        //Подписка на LiveData
        viewModel.stateLiveData.observe(this) { state ->
            render(state)
        }

        setupAdapters()
        setupListeners()

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.playerToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }


        //Фокус на поле ввода
        val searchEditText = findViewById<EditText>(R.id.editText)
        searchEditText.post {
            searchEditText.requestFocus()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }

    }

    private fun initViews() {
        placeholderMessage = findViewById<LinearLayout>(R.id.placeholderMessage)
        placeholderImage = findViewById<ImageView>(R.id.placeholderImage)
        placeholderText = findViewById<TextView>(R.id.placeholderText)
        refreshButton = findViewById<MaterialButton>(R.id.refreshButton)
        trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        historyLayout = findViewById(R.id.historyLayout)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
    }

    private fun setupAdapters() {
        historyAdapter = TrackAdapter(ArrayList()) { track ->
            viewModel.saveTrackToHistory(track)
            openPlayer(track)
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        adapter = TrackAdapter(tracks) { track ->
            viewModel.saveTrackToHistory(track)
            openPlayer(track)
        }
        trackRecyclerView.layoutManager = LinearLayoutManager(this)
        trackRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        val searchEditText = findViewById<EditText>(R.id.editText)
        val clearButton = findViewById<ImageView>(R.id.clearButton)
        val toolbar = findViewById<MaterialToolbar>(R.id.playerToolbar)

        toolbar.setNavigationOnClickListener { finish() }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            viewModel.clearSearch()
            //Скрытие клавиатуры
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //Empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                if (searchEditText.hasFocus() && s.isNullOrEmpty()) {
                    viewModel.showHistory()
                } else {
                    viewModel.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //Empty
            }
        }

        searchEditText.addTextChangedListener(textWatcher)

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel.showHistory()
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchEditText.text.isNotEmpty()) {
                    viewModel.searchRequest(searchEditText.text.toString())
                }
                true
            }
            false
        }

        refreshButton.setOnClickListener {
            viewModel.searchRequest(searchEditText.text.toString())
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
    }

    //Метод отрисовки состояний UI
    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Empty -> showEmpty(getString(R.string.nothing_found))
            is SearchState.Error -> showError(getString(R.string.something_went_wrong))
            is SearchState.History -> showHistory(state.tracks)
        }
    }

    //Состояния
    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        trackRecyclerView.visibility = View.GONE
        historyLayout.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
    }

    private fun showContent(newTracks: List<Track>) {
        progressBar.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        historyLayout.visibility = View.GONE
        trackRecyclerView.visibility = View.VISIBLE

        tracks.clear()
        tracks.addAll(newTracks)
        adapter.notifyDataSetChanged()
    }

    private fun showEmpty(message: String) {
        progressBar.visibility = View.GONE
        trackRecyclerView.visibility = View.GONE
        historyLayout.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        placeholderImage.setImageResource(R.drawable.ic_not_found)
        placeholderText.text = message
        refreshButton.visibility = View.GONE
    }

    private fun showError(message: String) {
        progressBar.visibility = View.GONE
        trackRecyclerView.visibility = View.GONE
        historyLayout.visibility = View.GONE
        placeholderMessage.visibility = View.VISIBLE
        placeholderImage.setImageResource(R.drawable.ic_error_connection)
        placeholderText.text = message
        refreshButton.visibility = View.VISIBLE
    }

    private fun showHistory(historyTracks: List<Track>) {
        progressBar.visibility = View.GONE
        trackRecyclerView.visibility = View.GONE
        placeholderMessage.visibility = View.GONE

        if (historyTracks.isNotEmpty()) {
            historyLayout.visibility = View.VISIBLE
            historyAdapter.updateTracks(historyTracks)
        } else {
            historyLayout.visibility = View.GONE
        }
    }

    private fun openPlayer(track: Track) {
        if (clickDebounce()) {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("selected_track", track)
            startActivity(intent)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    //Сохранение данных EditText
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }
}