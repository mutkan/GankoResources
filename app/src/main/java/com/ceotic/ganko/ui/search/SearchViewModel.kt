package com.ceotic.ganko.ui.search

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.*
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    fun searchManage(query: String): Single<List<RegistroManejo>> =
            db.listByExp(("tipo" regexEx query) andEx ("idFinca" regexEx userSession.farmID), RegistroManejo::class).applySchedulers()

    fun searchBovine(query: String): Single<List<Bovino>> =
            db.listByExp(("codigo" regexEx query) andEx ("finca" regexEx userSession.farmID), Bovino::class).applySchedulers()

    fun searchStraw(query: String): Single<List<Straw>> =
            db.listByExp((("layette" regexEx query) orEx ("idStraw" regexEx query)) andEx ("idFarm" regexEx userSession.farmID), Straw::class).applySchedulers()

    fun searchAlimentacion(query: String): Single<List<RegistroAlimentacion>> =
            db.listByExp(("tipoAlimento" regexEx query) andEx ("idFinca" regexEx userSession.farmID), RegistroAlimentacion::class).applySchedulers()

    fun searchHealth(query: String): Single<List<Sanidad>> =
            db.listByExp((("diagnostico" regexEx query) orEx ("evento" regexEx query)) andEx ("idFinca" regexEx userSession.farmID), Sanidad::class).applySchedulers()

    fun searchVaccine(query: String): Single<List<RegistroVacuna>> =
            db.listByExp(("nombre" regexEx query) andEx ("idFinca" regexEx userSession.farmID), RegistroVacuna::class).applySchedulers()

    fun returnSearchType(list: List<Any>, type: Int): Single<List<SearchActivity.SearchType>> =
        list.toObservable()
                .map {SearchActivity.SearchType(it, type)}
                .toList()
                .applySchedulers()


}