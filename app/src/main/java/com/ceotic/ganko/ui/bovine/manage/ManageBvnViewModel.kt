package com.ceotic.ganko.ui.bovine.manage

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.SelectResult
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.RegistroManejo
import com.ceotic.ganko.util.*
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import javax.inject.Inject

class ManageBvnViewModel @Inject constructor(private val db: CouchRx, private val bd: Database):ViewModel(){

    fun listManageBovine(idBovino: String): Single<List<RegistroManejo>> {
        val groupQuery = QueryBuilder.select(SelectResult.property("nombre"))
                .from(DataSource.database(bd))
                .where("bovines" containsEx idBovino andEx ("type" equalEx "Group"))
        return db.listByQuery(groupQuery)
                .flatMap { it.toObservable().map { it.getString(0) }.toList() }
                .flatMap { grupos ->
                    Log.d("GRUPOS", grupos.toString())
                    db.listByExp("bovinos" containsEx idBovino orEx  ("grupo" inEx grupos), RegistroManejo::class)
                }.applySchedulers()
    }

}
