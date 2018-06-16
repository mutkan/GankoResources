package com.example.cristian.myapplication.ui.menu

import android.arch.lifecycle.ViewModel
import android.graphics.Color
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.*
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.andEx
import com.example.cristian.myapplication.util.applySchedulers
import com.example.cristian.myapplication.util.equalEx
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by Ana Marin on 11/03/2018.
 */
class MenuViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    //region Menu
    var content: Int = 2
    val querySubject = PublishSubject.create<String>()

    val data: List<MenuItem> = listOf(
            MenuItem(MenuItem.TYPE_TITLE, titleText = userSession.farm),
            MenuItem(MenuItem.TYPE_BUTTON, icon = R.drawable.ic_back_white, title = R.string.change_farm),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_bovine, R.string.bovines),
            MenuItem(MenuItem.TYPE_MENU, R.color.img, R.drawable.ic_bovine, R.string.menu_groups),
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

    fun getFarmId(): String = userSession.farmID

    fun getBovine(idFinca: String): Single<List<Bovino>> =
            db.listByExp("finca" equalEx idFinca andEx ("retirado" equalEx false), Bovino::class)
                    .applySchedulers()

    fun deleteBovine(idBovino: String): Single<Unit> = db.remove(idBovino).applySchedulers()

    fun getManagement(idFinca: String):Single<List<Manage>> =
            getBovine(idFinca)
                    .flatMapObservable {
                        it.toObservable()
                    }
                    .flatMap {
                        it.manejo!!.toObservable()

                    }
                    .toList()
                    .applySchedulers()

    fun getStraw(idFinca: String):Single<List<Straw>> =
            db.listByExp("idFarm" equalEx idFinca, Straw::class)
                    .applySchedulers()

    fun getHealth(idFinca: String): Single<List<Sanidad>> =
            db.listByExp("idFarm" equalEx idFinca, Sanidad::class)
                    .applySchedulers()

    fun getMilk(idFinca: String): Single<List<SalidaLeche>> =
            db.listByExp("idFarm" equalEx idFinca, SalidaLeche::class)
                    .applySchedulers()

    fun getFeed(idFinca: String): Single<List<Feed>> =
            db.listByExp("idFarm" equalEx idFinca, Feed::class)
                    .applySchedulers()

    fun getMeadows(idFinca: String): Single<Pair<List<Pradera>, Long>> =
            db.listByExp("idFinca" equalEx idFinca, Pradera::class)
                    .map {
                        it.toObservable().toList()
                    }.flatMap {
                        it.zipWith(it.flatMapObservable { it.toObservable() }.filter { it.isEmptyMeadow == false }.count())
                    }
                    .applySchedulers()

    fun getMeadow(id:String): Maybe<Pradera> =
            db.oneById(id,Pradera::class).applySchedulers()

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
    //Proposito
    fun getMilkPurpose(Idfinca: String,filter:Filter):Single<List<Bovino>> =
            db.listByExp("Idfinca" equalEx Idfinca andEx ("proposito" equalEx "leche"),Bovino::class)

    fun getCebaPurpose(Idfinca: String,filter: Filter):Single<List<Bovino>> =
            db.listByExp("Idfinca" equalEx Idfinca andEx ("proposito" equalEx "ceba"),Bovino::class)

    fun getCebaAndMilkPurpose(Idfinca: String,filter: Filter):Single<List<Bovino>> =
            db.listByExp("Idfinca" equalEx Idfinca andEx ("proposito" equalEx "leche y ceba"),Bovino::class)

    fun getBovinesFilter(Idfinca: String,filter: Filter):Single<List<Bovino>> =
            db.listByExp("Idfinca" equalEx Idfinca ,Bovino::class)



}