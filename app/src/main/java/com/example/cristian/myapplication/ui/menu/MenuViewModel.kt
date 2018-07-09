package com.example.cristian.myapplication.ui.menu

import android.arch.lifecycle.ViewModel
import android.graphics.Color
import com.couchbase.lite.Ordering
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.*
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.*
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.PublishSubject
import java.util.*
import javax.inject.Inject

/**
 * Created by Ana Marin on 11/03/2018.
 */
class MenuViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    private val farmID = userSession.farmID

    fun getFarmId(): String = farmID


    //region Menu
    var content: Int = 2
    val querySubject = PublishSubject.create<String>()
    val pageSize: Int = 30


    val data: List<MenuItem> = listOf(
            MenuItem(MenuItem.TYPE_TITLE, titleText = userSession.farm),
            MenuItem(MenuItem.TYPE_BUTTON, icon = R.drawable.ic_back_white, title = R.string.change_farm),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_bovine, R.string.bovines),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_group, R.string.menu_groups),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_milk, R.string.milk),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_feed, R.string.feeding),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_management, R.string.management),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_movements, R.string.movement),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_vaccine, R.string.vaccines),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_health, R.string.health),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_straw, R.string.straw),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_prairies, R.string.prairies),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_reports, R.string.reports),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_notifications, R.string.notifications),
            MenuItem(MenuItem.TYPE_BUTTON, icon = R.drawable.ic_logout, title = R.string.logout)

    )

    val selectedColors: List<Int> = listOf(
            R.color.bovine_primary,
            R.color.group_primary,
            R.color.milk_primary,
            R.color.feed_primary,
            R.color.management_primary,
            R.color.movements_primary,
            R.color.vaccine_primary,
            R.color.health_primary,
            R.color.straw_primary,
            R.color.prairie_primary,
            R.color.reports_primary,
            R.color.notification_primary
    )

    fun getStatusBarColor(color: Int): Int {
        var red = Color.red(color) - 45
        var green = Color.green(color) - 45
        var blue = Color.blue(color) - 45

        red = if (red >= 0) red else 0
        blue = if (blue >= 0) blue else 0
        green = if (green >= 0) green else 0

        return Color.rgb(red, green, blue)
    }

    class MenuItem(val type: Int, var color: Int = 0, val icon: Int = 0, val title: Int = 0,
                   val titleText: String? = null) {

        companion object {
            val TYPE_TITLE: Int = 0
            val TYPE_BUTTON: Int = 1
            val TYPE_MENU: Int = 2
        }
    }
    //endregion

    fun getBovine(idFinca: String): Single<List<Bovino>> =
            db.listByExp("finca" equalEx idFinca andEx ("retirado" equalEx false), Bovino::class)
                    .applySchedulers()

    fun deleteBovine(idBovino: String): Single<Unit> = db.remove(idBovino).applySchedulers()

    fun getManagement(idFinca: String): Single<List<Manage>> =
            getBovine(idFinca)
                    .flatMapObservable {
                        it.toObservable()
                    }
                    .flatMap {
                        it.manejo!!.toObservable()

                    }
                    .toList()
                    .applySchedulers()

    fun getStraw(idFinca: String): Single<List<Straw>> =
            db.listByExp("idFarm" equalEx idFinca, Straw::class)
                    .applySchedulers()

    fun getHealth(idFinca: String): Single<List<Sanidad>> =
            db.listByExp("idFinca" equalEx idFinca, Sanidad::class)
                    .applySchedulers()

    fun getNextHealth(idFinca: String, page:Int): Single<List<Sanidad>>{
        val skip = page * pageSize
        val today = Date()
        val nextDate = Date(today.time + (86_400_000 * 8))
        return db.listByExp("idFinca" equalEx idFinca andEx ("fecha" gte today) andEx ("proximaAplicacion" equalEx 0), Sanidad::class,
                pageSize, skip,
                arrayOf(Ordering.property("fechaProxima").ascending()))
                .applySchedulers()
    }

    fun getPendingHealrh(idFinca: String,page: Int): Single<List<Sanidad>>{
        val skip = page * pageSize
        val today = Date()
        return db.listByExp("idFinca" equalEx idFinca andEx ("fecha" lte today) andEx ("proximaAplicacion" equalEx 0), Sanidad::class,
                pageSize,skip)
                .applySchedulers()
    }


    fun updateHealth(sanidad: Sanidad): Single<Unit> = db.update(sanidad._id!!, sanidad).applySchedulers()

    fun getMilk(idFinca: String): Single<List<SalidaLeche>> =
            db.listByExp("idFarm" equalEx idFinca, SalidaLeche::class)
                    .applySchedulers()

    fun getFeed(): Observable<List<RegistroAlimentacion>> = db.listObsByExp("idFinca" equalEx farmID, RegistroAlimentacion::class)
                    .applySchedulers()

    fun getMeadows(idFinca: String): Single<Pair<List<Pradera>, Long>> =
            db.listByExp("idFinca" equalEx idFinca, Pradera::class)
                    .map {
                        it.toObservable().toList()
                    }.flatMap {
                        it.zipWith(it.flatMapObservable { it.toObservable() }.filter { it.isEmptyMeadow == false }.count())
                    }
                    .applySchedulers()

    fun getMeadow(id: String): Maybe<Pradera> =
            db.oneById(id, Pradera::class).applySchedulers()

    fun saveMeadow(pradera: Pradera): Single<String> =
            db.insert(pradera).applySchedulers()

    fun updateMeadow(id: String, pradera: Pradera): Single<Unit> =
            db.update(id, pradera).applySchedulers()

    fun getUsedMeadows(idFinca: String): Single<MutableList<Pradera>> =
            db.listByExp("idFinca" equalEx idFinca, Pradera::class)
                    .flatMapObservable { it.toObservable() }
                    .filter { it.available == false }
                    .toList().applySchedulers()

    fun getUnusedMeadows(idFinca: String): Single<MutableList<Pradera>> =
            db.listByExp("idFinca" equalEx idFinca, Pradera::class)
                    .flatMapObservable { it.toObservable() }
                    .filter { it.available == true }
                    .toList()
                    .applySchedulers()

    fun getGroups(idFinca: String): Single<List<Group>> =
            db.listByExp("idFinca" equalEx idFinca, Group::class)
                    .applySchedulers()

    // Filtros
    fun getBovinesFilter(idFinca: String): Single<List<Bovino>> =
            db.listByExp("finca" equalEx idFinca, Bovino::class)
                    .applySchedulers()

    fun getMilkPurpose(Idfinca: String): Single<List<Bovino>> =
            db.listByExp("Idfinca" equalEx Idfinca andEx ("proposito" equalEx "leche"), Bovino::class)

    fun getCebaPurpose(Idfinca: String): Single<List<Bovino>> =
            db.listByExp("Idfinca" equalEx Idfinca andEx ("proposito" equalEx "Ceba"), Bovino::class)

    fun getCebaAndMilkPurpose(Idfinca: String): Single<List<Bovino>> =
            db.listByExp("Idfinca" equalEx Idfinca andEx ("proposito" equalEx "leche y ceba"), Bovino::class)


    //region Vacunas
    fun inserVaccine(registroVacuna: RegistroVacuna): Single<String> = db.insert(registroVacuna).applySchedulers()

    fun updateVaccine(registroVacuna: RegistroVacuna): Single<Unit> = db.update(registroVacuna._id!!, registroVacuna).applySchedulers()

    fun getVaccinations(): Observable<List<RegistroVacuna>> = db.listObsByExp("idFinca" equalEx farmID, RegistroVacuna::class).applySchedulers()

    fun getNextVaccines(from: Date, to: Date): Observable<List<RegistroVacuna>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProx".betweenDates(from, to)) andEx ("proxAplicado" equalEx false), RegistroVacuna::class).applySchedulers()

    fun getPendingVaccines(from: Date): Observable<List<RegistroVacuna>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProx".lte(from)) andEx ("proxAplicado" equalEx false), RegistroVacuna::class).applySchedulers()
    //endregion


    //region Manejo
    fun insertManage(registroManejo: RegistroManejo): Single<String> = db.insertManage(registroManejo).applySchedulers()

    fun getManages(): Observable<List<RegistroManejo>> = db.listObsByExp("idFinca" equalEx farmID, RegistroManejo::class).applySchedulers()

    fun getNextManages(from: Date, to: Date): Observable<List<RegistroManejo>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProx".betweenDates(from, to)), RegistroManejo::class).applySchedulers()

    fun getNextHealth1(from: Date, to: Date): Observable<List<Sanidad>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".betweenDates(from, to)), Sanidad::class).applySchedulers()

    //endregion

}