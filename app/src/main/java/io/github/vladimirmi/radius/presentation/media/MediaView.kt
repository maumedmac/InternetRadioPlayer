package io.github.vladimirmi.radius.presentation.media

import android.arch.lifecycle.LiveData
import com.arellomobile.mvp.MvpView
import io.github.vladimirmi.radius.model.entity.Media

/**
 * Created by Vladimir Mikhalev 30.09.2017.
 */

interface MediaView : MvpView {
    fun setMediaList(mediaList: LiveData<List<Media>>)
    fun select(media: Media, playing: Boolean)
    fun unselect(media: Media)
}