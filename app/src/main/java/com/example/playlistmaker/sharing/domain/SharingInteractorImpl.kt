package com.example.playlistmaker.sharing.domain

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

    private fun getShareAppLink(): String {
        return "https://practicum.yandex.com/profile/android-developer-plus/"
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = "diborempty@gmail.com",
            subject = "Сообщение разработчикам приложения Playlist Maker",
            text = "Спасибо разработчикам за замечательное приложение!"
        )
    }

    private fun getTermsLink(): String {
        return "https://yandex.ru/legal/practicum_offer/ru/"
    }
}