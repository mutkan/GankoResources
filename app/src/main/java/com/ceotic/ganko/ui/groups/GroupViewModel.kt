package com.ceotic.ganko.ui.groups

import android.arch.lifecycle.ViewModel
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.Alarm
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.data.models.Group
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.ui.common.SearchBarActivity
import com.ceotic.ganko.ui.menu.Filter
import com.ceotic.ganko.util.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import javax.inject.Inject

class GroupViewModel @Inject constructor(private val db: CouchRx,
                                         private val session: UserSession) : ViewModel() {

    private val pageSize = 30

    fun farmId(): String = session.farmID

    fun list(): Single<List<Group>> = db.listByExp("finca" equalEx session.farmID, Group::class)
            .applySchedulers()


    fun listObservable(): Observable<List<Group>> = SearchBarActivity.query
                    .startWith("")
                    .flatMap {
                        var exp = "finca" equalEx session.farmID
                        if (it != "") exp = exp andEx ("nombre" likeEx "$it%")
                        db.listObsByExp(exp, Group::class)

                    }
                    .applySchedulers()


    fun add(group: Group): Single<Unit> = db.insert(group)
            .map { Unit }
            .applySchedulers()

    fun update(id: String, group: Group): Single<Unit> = db.update(id, group)
            .applySchedulers()

    fun remove(id: String): Single<Unit> = db.remove(id)
            .applySchedulers()

    fun listBovines(page: Int, filter: Filter, query: String? = null): Single<List<Bovino>> {
        val skip = page * pageSize

        var exp = "finca" equalEx session.farmID andEx ("retirado" equalEx false)
        if (query != null && query != "") exp = exp andEx (("nombre" likeEx "$query%") orEx ("codigo" likeEx "$query%"))

        return db.listByExp(filter.makeExp(exp), Bovino::class, pageSize, skip)
                .applySchedulers()
    }

    fun listSelected(ids: List<String>): Single<List<Bovino>> =
            db.listByExp("finca" equalEx session.farmID andEx ("retirado" equalEx false) andEx ("_id" inEx ids), Bovino::class)
                    .applySchedulers()


    fun listBovinesByDocId(id: String): Single<List<Bovino>> =
            db.oneById(id, Alarm::class)
                    .flatMapSingle { db.listByExp("_id" inEx (it.bovinos ?: emptyList()) andEx ("retirado" equalEx false), Bovino::class) }
                    .applySchedulers()

    fun listAllBovinesByDocId(id: String): Single<Pair<List<Bovino>, List<Bovino>>> = db.oneById(id, Alarm::class)
            .flatMapSingle {
                db.listByExp("_id" inEx (it.bovinos ?: emptyList()) andEx ("retirado" equalEx false), Bovino::class)
                        .zipWith(db.listByExp("_id" inEx it.noBovinos andEx ("retirado" equalEx false), Bovino::class))
            }
            .applySchedulers()

}

