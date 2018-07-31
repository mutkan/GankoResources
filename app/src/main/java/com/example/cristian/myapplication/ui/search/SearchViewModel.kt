package com.example.cristian.myapplication.ui.search

import android.arch.lifecycle.ViewModel
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.*
import com.example.cristian.myapplication.util.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val db: CouchRx) : ViewModel() {

    fun searchManage(query: String): Single<List<RegistroManejo>> =
            db.listByExp("tipo" regexEx query, RegistroManejo::class).applySchedulers()

    fun searchBovine(query: String): Single<List<Bovino>> =
            db.listByExp("codigo" regexEx query, Bovino::class).applySchedulers()

    fun searchStraw(query: String): Single<List<Straw>> =
            db.listByExp(("layette" regexEx query) orEx ("idStraw" regexEx query), Straw::class).applySchedulers()

    fun searchAlimentacion(query: String): Single<List<RegistroAlimentacion>> =
            db.listByExp("tipoAlimento" regexEx query, RegistroAlimentacion::class).applySchedulers()

    fun searchHealth(query: String): Single<List<Sanidad>> =
            db.listByExp(("diagnostico" regexEx query) orEx ("evento" regexEx query), Sanidad::class).applySchedulers()

    fun searchVaccine(query: String): Single<List<RegistroVacuna>> =
            db.listByExp("nombre" regexEx query, RegistroVacuna::class).applySchedulers()

    fun returnSearchType(list: List<Any>, type: Int): Single<List<SearchActivity.SearchType>> =
        list.toObservable()
                .map {SearchActivity.SearchType(it, type)}
                .toList()
                .applySchedulers()


}