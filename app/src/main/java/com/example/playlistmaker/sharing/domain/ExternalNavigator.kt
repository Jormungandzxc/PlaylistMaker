package com.example.playlistmaker.sharing.domain

import androidx.annotation.StringRes

interface ExternalNavigator {
    fun shareLink(@StringRes shareAppLink: Int)
    fun openLink(@StringRes termsLink: Int)
    fun openEmail(emailData: EmailData)
}