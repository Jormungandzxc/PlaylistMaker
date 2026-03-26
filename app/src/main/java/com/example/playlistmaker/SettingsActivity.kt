package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.settingsToolbar)

        toolbar.setNavigationOnClickListener{
            finish()
        }

        val shareButton = findViewById<MaterialTextView>(R.id.btn_share)

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val shareText = getString(R.string.share_app_text)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_chooser_title)))
        }

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

        val userAgreementButton = findViewById<MaterialTextView>(R.id.btn_user_agreement)

        userAgreementButton.setOnClickListener {
            val url = getString(R.string.agreement_link)
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.data = Uri.parse(url)
            startActivity(agreementIntent)
        }

            //Свич темы
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.switch_btn_dark_mode)

        val isDarkTheme = (applicationContext as App).darkTheme
        themeSwitcher.isChecked = isDarkTheme
        themeSwitcher.setOnCheckedChangeListener{switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}