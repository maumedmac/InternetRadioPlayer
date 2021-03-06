package io.github.vladimirmi.internetradioplayer.domain.interactor

import io.github.vladimirmi.internetradioplayer.data.utils.Preferences
import javax.inject.Inject

/**
 * Created by Vladimir Mikhalev 20.11.2018.
 */

class MainInteractor
@Inject constructor(private val prefs: Preferences) {

    fun saveMainPageId(pageId: Int) {
        prefs.mainPageId = pageId
    }

    fun getMainPageId(): Int {
        return prefs.mainPageId
    }
}