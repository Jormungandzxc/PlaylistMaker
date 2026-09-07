package com.example.playlistmaker.sharing.domain

import androidx.annotation.StringRes
import com.example.playlistmaker.R

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    @StringRes
    private fun getShareAppLink(): Int = R.string.share_app_text

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = R.string.support_email_address,
            subject = R.string.support_subject,
            text = R.string.support_message
        )
    }

    @StringRes
    private fun getTermsLink(): Int = R.string.agreement_link
}