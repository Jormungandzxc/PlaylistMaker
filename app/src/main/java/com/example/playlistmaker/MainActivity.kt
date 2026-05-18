package com.example.playlistmaker

import android.content.Intent
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

        //Окраска статус бара и навигации
        val windowController = ViewCompat.getWindowInsetsController(window.decorView)
        val statusBarColor = getColor(R.color.blue_color)
        val navigationBarColor = getColor(R.color.blue_color)
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        windowController?.isAppearanceLightStatusBars = true
        windowController?.isAppearanceLightNavigationBars = true

        //Обработка нажатий
        val searchButton = findViewById<MaterialButton>(R.id.btn_search)

        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener{
            override fun onClick(v: View?) {
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        }

        searchButton.setOnClickListener(searchButtonClickListener)

        val libraryButton = findViewById<MaterialButton>(R.id.btn_library)

        libraryButton.setOnClickListener{
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }

        val settingsButton = findViewById<MaterialButton>(R.id.btn_settings)

        settingsButton.setOnClickListener{
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

    }
}