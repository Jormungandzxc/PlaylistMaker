package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeInteractor = Creator.provideThemeInteractor(this)

        val toolbar = findViewById<MaterialToolbar>(R.id.settingsToolbar)

        toolbar.setNavigationOnClickListener{
            finish()
        }

        //Кнопка Поделиться
        val shareButton = findViewById<MaterialTextView>(R.id.btn_share)

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val shareText = getString(R.string.share_app_text)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)))
        }

        // Кнопка Поддержка
        val supportButton = findViewById<MaterialTextView>(R.id.btn_support)

        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email_address)))
            val subject = getString(R.string.support_subject)
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            val message = getString(R.string.support_message)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }

        //Кнопка Пользовательского Соглашения
        val userAgreementButton = findViewById<MaterialTextView>(R.id.btn_user_agreement)

        userAgreementButton.setOnClickListener {
            val url = getString(R.string.agreement_link)
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.data = Uri.parse(url)
            startActivity(agreementIntent)
        }

            //Свич темы
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.switch_btn_dark_mode)
        themeSwitcher.isChecked = themeInteractor.isDarkThemeEnabled()
        themeSwitcher.setOnCheckedChangeListener{_, checked ->
            themeInteractor.saveTheme(checked)
            (applicationContext as App).switchTheme(checked)
        }
    }
}