package io.github.vladimirmi.radius.presentation.root

import android.support.annotation.StringRes
import android.view.MenuItem
import io.github.vladimirmi.radius.R

class ToolbarBuilder {
    private var isToolbarVisible = true
    @StringRes private var titleId = R.string.app_name
    private var title = ""
    private var backNavEnabled = false
    private val menuHolder = MenuHolder()

    fun setToolbarVisible(toolbarVisible: Boolean = true): ToolbarBuilder {
        isToolbarVisible = toolbarVisible
        return this
    }

    fun setToolbarTitleId(@StringRes titleId: Int): ToolbarBuilder {
        this.titleId = titleId
        return this
    }

    fun setToolbarTitle(title: String): ToolbarBuilder {
        this.title = title
        return this
    }

    fun enableBackNavigation(enable: Boolean = true): ToolbarBuilder {
        backNavEnabled = enable
        return this
    }

    fun setMenuActions(actions: (MenuItem) -> Unit): ToolbarBuilder {
        menuHolder.actions = actions
        return this
    }

    fun addMenuItem(menuItem: MenuItemHolder, position: Int = -1): ToolbarBuilder {
        menuHolder.addItem(menuItem, position)
        return this
    }

    fun replaceMenuItem(itemTitleResId: Int, menuItem: MenuItemHolder): ToolbarBuilder {
        menuHolder.replaceItem(itemTitleResId, menuItem)
        return this
    }

    fun removeMenuItem(itemTitleResId: Int): ToolbarBuilder {
        menuHolder.removeItem(itemTitleResId)
        return this
    }

    fun clearMenu(): ToolbarBuilder {
        menuHolder.clear()
        return this
    }

    fun build(toolbarView: ToolbarView) {
        toolbarView.setToolbarVisible(isToolbarVisible)
        if (title.isNotBlank()) {
            toolbarView.setToolbarTitle(title)
        } else {
            toolbarView.setToolbarTitle(titleId)
        }
        toolbarView.enableBackNavigation(backNavEnabled)
        toolbarView.setMenu(menuHolder)
    }
}

class MenuHolder {
    private val items = ArrayList<MenuItemHolder>()
    lateinit var actions: (MenuItem) -> Unit
    val menu: List<MenuItemHolder> get() = items

    fun addItem(item: MenuItemHolder, position: Int) {
        if (position < 0 || position > items.size) {
            items.add(item)
        } else {
            items.add(position, item)
        }
    }

    fun replaceItem(itemTitleResId: Int, menuItem: MenuItemHolder) {
        val idx = items.indexOfFirst { it.itemTitleResId == itemTitleResId }
        if (idx == -1) return
        items[idx] = menuItem
    }

    fun removeItem(itemTitleResId: Int) {
        items.removeAll { it.itemTitleResId == itemTitleResId }
    }

    fun clear() {
        items.clear()
    }
}

class MenuItemHolder(val itemTitleResId: Int,
                     val iconResId: Int,
                     val showAsAction: Boolean = false) {

    val id = itemTitleResId
}