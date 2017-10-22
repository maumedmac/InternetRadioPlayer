package io.github.vladimirmi.radius.presentation.media

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.github.vladimirmi.radius.domain.interactor.media.MediaInteractor
import io.github.vladimirmi.radius.data.repository.MediaBrowserController
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Vladimir Mikhalev 30.09.2017.
 */

@InjectViewState
class MediaPresenter
@Inject constructor(private val mediaInteractor: MediaInteractor,
                    private val router: Router,
                    private val browserController: MediaBrowserController)
    : MvpPresenter<MediaView>() {

    override fun onFirstViewAttach() {
        viewState.setMediaList(mediaInteractor.getMediaList())
        browserController.registerCallback(callback)
    }

    override fun onDestroy() {
        browserController.unRegisterCallback(callback)
    }

    fun playPause(uri: Uri) {
        if (browserController.isPlaying(uri)) {
            browserController.stop()
        } else {
            browserController.play(uri)
        }
    }

    fun onBackPressed() = router.exit()

    private val callback = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
            Timber.e("onPlaybackStateChanged: $state")
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            Timber.e("onMetadataChanged: ${metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)}: " +
                    " ${metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)} - " +
                    " ${metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)}")
        }
    }
}


