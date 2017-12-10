package io.github.vladimirmi.radius.presentation.metadata

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.arellomobile.mvp.InjectViewState
import io.github.vladimirmi.radius.model.repository.MediaBrowserController
import io.github.vladimirmi.radius.ui.base.BasePresenter
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

/**
 * Created by Vladimir Mikhalev 08.12.2017.
 */

@InjectViewState
class MetadataPresenter
@Inject constructor(private val browserController: MediaBrowserController)
    : BasePresenter<MetadataView>() {

    override fun onFirstViewAttach() {
        browserController.playbackMetaData
                .subscribeBy { handleMeta(it) }
                .addTo(compDisp)

        browserController.playbackState
                .subscribeBy { handleState(it) }
                .addTo(compDisp)
    }

    private fun handleMeta(meta: MediaMetadataCompat) {
        val s = with(meta.description) { "$subtitle - $title" }
        viewState.setInfo(s)
    }

    private fun handleState(state: PlaybackStateCompat) {
        when (state.state) {
            PlaybackStateCompat.STATE_BUFFERING -> viewState.setInfo("Загрузка...")
            PlaybackStateCompat.STATE_STOPPED -> viewState.hide()
        }
    }
}