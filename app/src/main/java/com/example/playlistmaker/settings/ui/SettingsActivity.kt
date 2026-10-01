package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupListeners()

        viewModel.themeSettingsLiveData.observe(this){ settings ->
            themeSwitcher.setOnCheckedChangeListener(null)
            themeSwitcher.isChecked = settings.isDarkThemeEnabled
            themeSwitcher.setOnCheckedChangeListener { _, checked ->
                viewModel.switchTheme(checked)
            }
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

        shareButton.setOnClickListener { viewModel.shareApp() }
        supportButton.setOnClickListener { viewModel.openSupport() }
        userAgreementButton.setOnClickListener { viewModel.openTerms() }
    }
}