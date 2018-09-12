package io.github.vladimirmi.internetradioplayer.presentation.iconpicker

import com.arellomobile.mvp.InjectViewState
import io.github.vladimirmi.internetradioplayer.model.interactor.PlayerControlsInteractor
import io.github.vladimirmi.internetradioplayer.model.interactor.StationInteractor
import io.github.vladimirmi.internetradioplayer.model.service.Metadata
import io.github.vladimirmi.internetradioplayer.navigation.Router
import io.github.vladimirmi.internetradioplayer.presentation.root.RootPresenter
import io.github.vladimirmi.internetradioplayer.ui.base.BasePresenter
import javax.inject.Inject

/**
 * Created by Vladimir Mikhalev 15.12.2017.
 */

@InjectViewState
class IconPickerPresenter
@Inject constructor(private val rootPresenter: RootPresenter,
                    private val stationInteractor: StationInteractor,
                    private val controlsInteractor: PlayerControlsInteractor,
                    private val router: Router)
    : BasePresenter<IconPickerView>() {

    var currentIcon = stationInteractor.currentStation.icon

    override fun onFirstViewAttach() {
        viewState.setIcon(currentIcon)
    }

    override fun attachView(view: IconPickerView?) {
        super.attachView(view)
        rootPresenter.viewState.showControls(false)
        rootPresenter.viewState.showMetadata(false)
    }

    fun saveIcon() {
        stationInteractor.currentStation = stationInteractor.currentStation.copy(icon = currentIcon)
        exit()
    }

    fun onBackPressed(): Boolean {
        exit()
        return true
    }

    fun exit() {
        rootPresenter.viewState.showControls(true)
        if (!controlsInteractor.isStopped &&
                Metadata.create(controlsInteractor.playbackMetaData.blockingFirst()).isSupported) {
            rootPresenter.viewState.showMetadata(true)
        }
        router.exit()
    }
}
