package io.github.vladimirmi.radius.navigation

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import io.github.vladimirmi.radius.R
import io.github.vladimirmi.radius.presentation.getstarted.GetStartedFragment
import io.github.vladimirmi.radius.presentation.iconpicker.IconPickerFragment
import io.github.vladimirmi.radius.presentation.root.RootActivity
import io.github.vladimirmi.radius.presentation.station.StationFragment
import io.github.vladimirmi.radius.presentation.stationlist.StationListFragment
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.terrakok.cicerone.commands.*

/**
 * Created by Vladimir Mikhalev 03.12.2017.
 */

class Navigator(activity: RootActivity, containerId: Int)
    : SupportAppNavigator(activity, containerId) {

    private var currentKey = ""

    init {
        with(activity.supportFragmentManager) {
            addOnBackStackChangedListener {
                currentKey = if (backStackEntryCount > 0) {
                    val name = getBackStackEntryAt(backStackEntryCount - 1).name
                    name
                } else {
                    ""
                }
            }
        }
    }

    override fun createActivityIntent(screenKey: String, data: Any?) = null

    override fun createFragment(screenKey: String, data: Any?): Fragment? {
        if (currentKey == screenKey) return null
        val screen = screenKey.substringBefore(Router.DELIMITER)
        return when (screen) {
            Router.GET_STARTED_SCREEN -> GetStartedFragment()
            Router.MEDIA_LIST_SCREEN -> StationListFragment()
            Router.STATION_SCREEN -> StationFragment()
            Router.ICON_PICKER_SCREEN -> IconPickerFragment()
            else -> null
        }
    }

    override fun applyCommand(command: Command?) {
        if ((command is NextStation || command is PreviousStation)
                && !currentKey.contains(Router.STATION_SCREEN)) {
            return
        }
        super.applyCommand(command)
    }

    override fun setupFragmentTransactionAnimation(command: Command?, currentFragment: Fragment?, nextFragment: Fragment?, fragmentTransaction: FragmentTransaction?) {
        when (command) {
        // order matters because Next and Previous is subclasses of Forward
            is NextStation -> forwardTransition(fragmentTransaction)
            is PreviousStation -> previousTransition(fragmentTransaction)
            is Forward -> forwardTransition(fragmentTransaction)
            is Back, is BackTo -> backTransition(fragmentTransaction)
            is Replace -> replaceTransition(fragmentTransaction)
        }
    }

    private fun previousTransition(fragmentTransaction: FragmentTransaction?) {
        fragmentTransaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun backTransition(fragmentTransaction: FragmentTransaction?) {
        fragmentTransaction?.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun forwardTransition(fragmentTransaction: FragmentTransaction?) {
        fragmentTransaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun replaceTransition(fragmentTransaction: FragmentTransaction?) {
        fragmentTransaction?.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun unknownScreen(command: Command?) {
        //do nothing
    }

    override fun backToUnexisting() {
        applyCommand(Replace(Router.MEDIA_LIST_SCREEN, null))
    }
}