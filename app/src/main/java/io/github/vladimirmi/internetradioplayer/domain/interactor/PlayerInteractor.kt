package io.github.vladimirmi.internetradioplayer.domain.interactor

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import io.github.vladimirmi.internetradioplayer.data.repository.PlayerRepository
import io.github.vladimirmi.internetradioplayer.data.utils.NetworkChecker
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by Vladimir Mikhalev 25.11.2018.
 */

class PlayerInteractor
@Inject constructor(private val player: PlayerRepository,
                    private val networkChecker: NetworkChecker) {

    val playbackStateObs: Observable<PlaybackStateCompat> get() = player.playbackState
    val metadataObs: Observable<MediaMetadataCompat> get() = player.metadata
    val sessionEventObs: Observable<String> get() = player.sessionEvent

    val isPlaying: Boolean
        get() = with(player.playbackState) {
            hasValue() && (value?.state == PlaybackStateCompat.STATE_PLAYING ||
                    value?.state == PlaybackStateCompat.STATE_BUFFERING)
        }

    val isStopped: Boolean
        get() = with(player.playbackState) {
            hasValue() && (value?.state == PlaybackStateCompat.STATE_STOPPED)
        }

    val isNetAvail: Boolean get() = networkChecker.isAvailable()

    fun connect() = player.connect()

    fun disconnect() = player.disconnect()

    fun play() = player.play()

    fun playPause() {
        if (isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun stop() = player.stop()

    fun skipToPrevious() = player.skipToPrevious()

    fun skipToNext() = player.skipToNext()
}