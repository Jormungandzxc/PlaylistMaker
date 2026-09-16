package com.example.playlistmaker.sharing.domain

import androidx.annotation.StringRes

data class EmailData (
    @StringRes val email: Int,
    @StringRes val subject: Int,
    @StringRes val text: Int
)