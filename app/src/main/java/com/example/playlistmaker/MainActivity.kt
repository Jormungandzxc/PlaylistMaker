package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<MaterialButton>(R.id.btn_search)

        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "тык Поиск", Toast.LENGTH_SHORT).show()
            }
        }

        searchButton.setOnClickListener(searchButtonClickListener)

        val libraryButton = findViewById<MaterialButton>(R.id.btn_library)

        libraryButton.setOnClickListener{
            Toast.makeText(this@MainActivity,"тык Медиатека", Toast.LENGTH_SHORT).show()
        }

        val settingsButton = findViewById<MaterialButton>(R.id.btn_settings)

        settingsButton.setOnClickListener{
            Toast.makeText(this@MainActivity,"тык Настройки", Toast.LENGTH_SHORT).show()
        }

    }
}