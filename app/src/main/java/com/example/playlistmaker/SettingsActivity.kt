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
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBack = findViewById<ImageButton>(R.id.btn_settings_back)

        buttonBack.setOnClickListener{
            finish()
        }

        val shareButton = findViewById<LinearLayout>(R.id.btn_share)

        shareButton.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val shareText = "https://practicum.yandex.com/profile/android-developer-plus/"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(shareIntent, "Поделиться через"))
        }

        val supportButton = findViewById<LinearLayout>(R.id.btn_support)

        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse("mailto:")
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("diborempty@gmail.com"))
            val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            val message = "«Спасибо разработчикам и разработчицам за крутое приложение!"
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }

        val userAgreementButton = findViewById<LinearLayout>(R.id.btn_user_agreement)

        userAgreementButton.setOnClickListener {
            val url = "https://yandex.ru/legal/practicum_offer/ru/"
            val agreementIntent = Intent(Intent.ACTION_VIEW)
            agreementIntent.data = Uri.parse(url)
            startActivity(agreementIntent)
        }
    }
}