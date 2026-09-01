package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val factory = SettingsViewModelFactory(
            Creator.provideSharingInteractor(this),
            Creator.provideSettingsInteractor(this),
            applicationContext as App
        )
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        initViews()
        setupListeners()

        viewModel.themeSettingsLiveData.observe(this){ settings ->
            themeSwitcher.isChecked = settings.isDarkThemeEnabled
        }

    }

    private fun initViews() {
        themeSwitcher = findViewById(R.id.switch_btn_dark_mode)
    }

    private fun setupListeners() {
        val toolbar = findViewById<MaterialToolbar>(R.id.settingsToolbar)
        val shareButton = findViewById<MaterialTextView>(R.id.btn_share)
        val supportButton = findViewById<MaterialTextView>(R.id.btn_support)
        val userAgreementButton = findViewById<MaterialTextView>(R.id.btn_user_agreement)

        toolbar.setNavigationOnClickListener { finish() }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        shareButton.setOnClickListener { viewModel.shareApp() }
        supportButton.setOnClickListener { viewModel.openSupport() }
        userAgreementButton.setOnClickListener { viewModel.openTerms() }
    }
}