package io.github.vladimirmi.internetradioplayer.presentation.base

/**
 * Created by Vladimir Mikhalev 10.11.2018.
 */

interface BaseView : BackPressListener {

    fun buildToolbar(builder: ToolbarBuilder)

    fun showMessage(resId: Int)
}