package io.github.vladimirmi.internetradioplayer.presentation.favoritelist

import io.github.vladimirmi.internetradioplayer.data.db.entity.Station
import io.github.vladimirmi.internetradioplayer.domain.interactor.FavoriteListInteractor
import io.github.vladimirmi.internetradioplayer.domain.interactor.StationInteractor
import io.github.vladimirmi.internetradioplayer.domain.model.FlatStationsList
import io.github.vladimirmi.internetradioplayer.extensions.subscribeX
import io.github.vladimirmi.internetradioplayer.presentation.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

/**
 * Created by Vladimir Mikhalev 30.09.2017.
 */

class FavoriteListPresenter
@Inject constructor(private val stationInteractor: StationInteractor,
                    private val favoriteListInteractor: FavoriteListInteractor)
    : BasePresenter<StationListView>() {

    override fun onAttach(view: StationListView) {
        favoriteListInteractor.stationsListObs
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeX(onNext = {
                    view.setStations(it)
                    view.showPlaceholder(it.size == 0)
                })
                .addTo(viewSubs)

        stationInteractor.stationObs
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeX(onNext = { view.selectStation(it) })
                .addTo(viewSubs)
    }

    fun selectStation(station: Station) {
        stationInteractor.station = station
    }

    fun selectGroup(id: String) {
        favoriteListInteractor.expandOrCollapseGroup(id)
                .subscribeX()
                .addTo(dataSubs)
    }

    fun removeGroup(id: String) {
        favoriteListInteractor.removeGroup(id)
                .subscribeX()
                .addTo(dataSubs)
    }

    fun moveGroupElements(stations: FlatStationsList) {
        favoriteListInteractor.moveGroupElements(stations)
                .subscribeX()
                .addTo(dataSubs)
    }
}


