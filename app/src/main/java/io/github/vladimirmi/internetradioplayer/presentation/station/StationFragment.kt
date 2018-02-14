package io.github.vladimirmi.internetradioplayer.presentation.station

import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import io.github.vladimirmi.internetradioplayer.R
import io.github.vladimirmi.internetradioplayer.di.Scopes
import io.github.vladimirmi.internetradioplayer.extensions.inputMethodManager
import io.github.vladimirmi.internetradioplayer.extensions.visible
import io.github.vladimirmi.internetradioplayer.model.entity.Station
import io.github.vladimirmi.internetradioplayer.presentation.root.ToolbarBuilder
import io.github.vladimirmi.internetradioplayer.presentation.root.ToolbarView
import io.github.vladimirmi.internetradioplayer.ui.TagView
import io.github.vladimirmi.internetradioplayer.ui.base.BackPressListener
import io.github.vladimirmi.internetradioplayer.ui.base.BaseFragment
import kotlinx.android.synthetic.main.view_station_info.*
import toothpick.Toothpick


/**
 * Created by Vladimir Mikhalev 18.11.2017.
 */

class StationFragment : BaseFragment(), StationView, BackPressListener {

    override val layoutRes = R.layout.fragment_station
    private var editTextBg: Int = 0
    private lateinit var stationId: String

    @InjectPresenter
    lateinit var presenter: StationPresenter

    @ProvidePresenter
    fun providePresenter(): StationPresenter {
        return Toothpick.openScopes(Scopes.ROOT_ACTIVITY, this)
                .getInstance(StationPresenter::class.java).also {
            Toothpick.closeScope(this)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        // save default edit text background
        val typedValue = TypedValue()
        activity.theme.resolveAttribute(android.R.attr.editTextBackground, typedValue, true)
        editTextBg = typedValue.resourceId

        // set action DONE on the multiline text
        titleTil.editText?.imeOptions = EditorInfo.IME_ACTION_DONE
        titleTil.editText?.setRawInputType(InputType.TYPE_CLASS_TEXT)

        urlTv.linkStyle(true)
        uriTv.linkStyle(true)
        urlTv.setOnClickListener { openLink(it as TextView) }
        uriTv.setOnClickListener { openLink(it as TextView) }

        changeIconBt.setOnClickListener { presenter.changeIcon() }
    }

    override fun onBackPressed() = presenter.onBackPressed()

    //region =============== StationView ==============

    override fun buildToolbar(builder: ToolbarBuilder) {
        builder.build(activity as ToolbarView)
    }

    override fun setStation(station: Station) {
        stationId = station.id
        titleTil.setTextWithoutAnimation(station.name)

        groupTil.setTextWithoutAnimation(station.group)
        groupTil.visible(station.group.isNotBlank())

        uriTv.text = station.uri

        urlTv.text = station.url
        station.url.isNotBlank().let {
            urlTitleTv.visible(it)
            urlTv.visible(it)
        }

        bitrateTv.text = getString(R.string.unit_bitrate, station.bitrate)
        (station.bitrate != 0).let {
            bitrateTitleTv.visible(it)
            bitrateTv.visible(it)
        }

        sampleTv.text = getString(R.string.unit_sample_rate, station.sample)
        (station.sample != 0).let {
            sampleTitleTv.visible(it)
            sampleTv.visible(it)
        }

        genresTv.visible(station.genre.isNotEmpty())
        genresFl.removeAllViews()
        station.genre.forEach { genresFl.addView(TagView(context, it, null)) }
    }

    override fun setStationIcon(icon: Bitmap) {
        iconIv.setImageBitmap(icon)
    }

    override fun setEditMode(editMode: Boolean) {
        titleTil.setEditable(editMode)
        changeIconBt.visible(editMode)

        if (editMode) {
            titleTil.editText!!.requestFocus()
            titleTil.editText!!.setSelection(titleTil.text.length)
        } else {
            context.inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    override fun editStation() {
        presenter.edit(constructStation())
    }

    override fun createStation() {
        presenter.create(constructStation())
    }

    override fun openRemoveDialog() {
        RemoveDialog().show(childFragmentManager, "remove_dialog")
    }

    override fun openLinkDialog(url: String) {
        LinkDialog.newInstance(url).show(childFragmentManager, "link_dialog")
    }

    override fun openCancelEditDialog(currentStation: Station, iconChanged: Boolean) {
        if (currentStation.copy(favorite = false) != constructStation() || iconChanged) {
            CancelEditDialog().show(childFragmentManager, "cancel_edit_dialog")
        } else {
            presenter.cancelEdit()
        }
    }

    override fun openCancelCreateDialog() {
        CancelCreateDialog().show(childFragmentManager, "cancel_create_dialog")
    }

    override fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    //endregion

    private fun constructStation(): Station {
        val genres = ArrayList<String>()
        (0 until genresFl.childCount)
                .forEach {
                    val tagView = genresFl.getChildAt(it) as TagView
                    genres.add(tagView.text.toString())
                }
        return Station(
                id = stationId,
                uri = uriTv.text.toString(),
                name = titleTil.text,
                group = groupTil.text,
                genre = genres
        )
    }

    private fun TextView.linkStyle(enable: Boolean) {
        val string = text.toString()
        val color = ContextCompat.getColor(context, R.color.blue_500)
        text = if (enable) {
            val spannable = SpannableString(string)
            spannable.setSpan(URLSpan(string), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(color), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable
        } else {
            string
        }
    }

    private fun openLink(it: TextView) {
        presenter.openLink(it.text.toString())
    }

    private fun TextInputLayout.setTextWithoutAnimation(string: String) {
        isHintAnimationEnabled = false
        editText?.setText(string)
        isHintAnimationEnabled = true
    }

    private val TextInputLayout.text: String
        get() = editText!!.text.toString()

    private fun TextInputLayout.setEditable(enable: Boolean) {
        editText?.apply {
            isFocusable = enable
            isClickable = enable
            isFocusableInTouchMode = enable
            isCursorVisible = enable

            if (enable) setBackgroundResource(editTextBg)
            else setBackgroundResource(0)
        }
    }
}