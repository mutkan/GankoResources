package com.ceotic.ganko.ui.menu

import android.arch.lifecycle.ViewModel
import android.graphics.Color
import com.ceotic.ganko.R
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.data.models.ProxStates.Companion.NOT_APPLIED
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.ui.common.SearchBarActivity
import com.ceotic.ganko.ui.menu.reports.AverageViewModel
import com.ceotic.ganko.ui.menu.reports.ReportViewModel
import com.ceotic.ganko.util.*
import com.ceotic.ganko.work.NotificationWork
import com.couchbase.lite.Expression
import com.couchbase.lite.Ordering
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Ana Marin on 11/03/2018.
 */
class MenuViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    fun getFarmId(): String = userSession.farmID
    fun setFarmId(farm: String) {
        userSession.farmID = farm
    }

    val reports: ReportViewModel by lazy { ReportViewModel(db, userSession) }
    val averages: AverageViewModel by lazy { AverageViewModel(userSession, db) }


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
                   var titleText: String? = null) {

        companion object {
            val TYPE_TITLE: Int = 0
            val TYPE_BUTTON: Int = 1
            val TYPE_MENU: Int = 2
        }
    }
    //endregion

    fun getFarm(idFarm: String): Maybe<String> = db.oneById(idFarm, Finca::class)
            .map { it.nombre }
            .doOnSuccess {
                userSession.farm = it
                data[0].titleText = it
            }
            .applySchedulers()

    fun getBovineByFilter(idFinca: String): Observable<List<Bovino>> {
        val filter = FilterFragment.filter
                .startWith(Filter())

        val query = SearchBarActivity.query
                .startWith("")

        return Observables.combineLatest(filter, query)
                .flatMapSingle {
                    var exp = "finca" equalEx idFinca andEx ("retirado" equalEx false)
                    if (it.second != "") exp = exp andEx (("nombre" likeEx "%${it.second}%") orEx ("codigo" likeEx "%${it.second}%"))
                    db.listByExp(it.first.makeExp(exp), Bovino::class)
                }
                .applySchedulers()
    }

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

    fun getStraw(idFinca: String): Observable<List<Straw>> = SearchBarActivity.query
            .startWith("")
            .flatMapSingle {
                var exp = "idFarm" equalEx idFinca
                if (it != "") exp = exp andEx ("idStraw" likeEx "%$it%" orEx ("breed" likeEx "%$it%") orEx ("layette" likeEx "%$it%"))
                db.listByExp(exp, Straw::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
            }
            .applySchedulers()

    fun reportesPrueba(): Single<List<List<String>>> =
            db.listByExp("idFinca" equalEx userSession.farmID, Sanidad::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
                    .flatMapObservable { it.toObservable() }
                    .map { listOf(it.descripcion!!, it.producto!!, it.evento!!, it.diagnostico!!, it.descripcion!!, it.producto!!) }
                    .toList().applySchedulers()

    fun getHealth(idFinca: String): Observable<List<Sanidad>> = SearchBarActivity.query
            .startWith("")
            .flatMapSingle {
                var exp = "idFinca" equalEx userSession.farmID
                if (it != "") exp = exp andEx ("evento" likeEx "%$it%" orEx ("diagnostico" likeEx "%$it%"))
                db.listByExp(exp, Sanidad::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()
            }
            .applySchedulers()


    fun getNextHealth(from: Date, to: Date): Observable<List<Sanidad>> =
            db.listObsByExp("idFinca" equalEx userSession.farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx ProxStates.NOT_APPLIED), Sanidad::class)
                    .applySchedulers()

    fun getPendingHealth(from: Date): Observable<List<Sanidad>> =
            db.listObsByExp("idFinca" equalEx userSession.farmID andEx ("fechaProxima" lt from) andEx ("estadoProximo" equalEx ProxStates.NOT_APPLIED), Sanidad::class)
                    .applySchedulers()

    fun updateHealth(sanidad: Sanidad): Single<Unit> = db.update(sanidad._id!!, sanidad)
            .flatMap { cancelAlarm(sanidad._id!!) }
            .applySchedulers()

    fun getMilk(idFinca: String): Observable<List<SalidaLeche>> = SearchBarActivity.query
            .startWith("")
            .flatMapSingle {
                var exp = "idFarm" equalEx idFinca
                if (it != "") exp = exp andEx ("operacion" likeEx "%$it%")
                db.listByExp(exp, SalidaLeche::class)
            }
            .applySchedulers()

    fun getFeed(): Observable<List<RegistroAlimentacion>> = SearchBarActivity.query
            .startWith("")
            .flatMap {
                var exp = "idFinca" equalEx userSession.farmID
                if (it != "") exp = exp andEx ("grupo" likeEx "%$it%" orEx ("tipoAlimento" likeEx "%$it%"))
                db.listObsByExp(exp, RegistroAlimentacion::class)
            }
            .applySchedulers()

    fun getMeadows(idFinca: String): Single<Pair<List<Pradera>, Long>> {
        val order = Ordering.property("orderValue").ascending()
        return db.listByExp("idFinca" equalEx idFinca, Pradera::class,
                100, null, arrayOf(order))
                .map {
                    Single.just(it)
                }.flatMap {
                    it.zipWith(it.flatMapObservable { it.toObservable() }.filter { it.emptyMeadow == false }.count())
                }
                .applySchedulers()
    }


    fun getMeadow(id: String): Maybe<Pradera> =
            db.oneById(id, Pradera::class).applySchedulers()

    fun saveMeadow(pradera: Pradera): Single<String> =
            db.insert(pradera).applySchedulers()

    fun updateMeadow(id: String, pradera: Pradera): Single<Unit> =
            db.update(id, pradera).applySchedulers()

    fun updateGroup(group: Group): Single<Unit> =
            db.update(group._id!!, group).applySchedulers()

    fun insertMovement(movimiento: Movimiento): Single<String> = getLastMovement(movimiento.idPradera!!)
            .map {
                movimiento.activo = true
                if (it.isNotEmpty()) {
                    val preDate = it[0].fechaSalida!!.time
                    val current = movimiento.transactionDate.time
                    val days = (current.toDouble() - preDate) / 86400000
                    movimiento.diasLibres = Math.ceil(days).toInt()
                    movimiento
                } else {
                    movimiento.diasLibres = 0
                    movimiento
                }
            }
            .flatMap { db.insert(it) }
            .applySchedulers()

    fun freeMeadow(meadow: String): Single<Unit> = getLastMovement(meadow)
            .flatMap {
                if (it.isNotEmpty()) {
                    val mov = it[0]
                    mov.activo = false
                    mov.fechaSalida = Date()
                    db.update(mov._id!!, mov)
                } else {
                    Single.just(Unit)
                }
            }
            .flatMap {
                val now = Date().time / 60000
                val milis = now + 7200

                val alarm = Alarm(
                        titulo = "5 Dias libre Paradera $meadow",
                        descripcion = "La pradera $meadow lleva 5 dias desocupada",
                        alarma = ALARM_MEADOW_EXIT,
                        fechaProxima = Date(milis * 60000),
                        type = TYPE_ALARM,
                        activa = true,
                        reference = meadow,
                        idFinca = userSession.farmID
                )
                val uuid = NotificationWork.notify(NotificationWork.TYPE_MEADOW, alarm.titulo!!, alarm.descripcion!!,
                        alarm.reference!!, 7200, TimeUnit.MINUTES, userSession.farmID)

                alarm.device = listOf(AlarmDevice(userSession.device, uuid.toString()))
                db.insert(alarm).map { Unit }
            }
            .applySchedulers()


    private fun getLastMovement(meadow: String): Single<List<Movimiento>> =
            db.listByExp("idFarm" equalEx getFarmId() andEx ("idPradera" equalEx meadow),
                    Movimiento::class, 1, orderBy = arrayOf(Ordering.property("transactionDate").descending()))

    fun getUsedMeadows(idFinca: String): Observable<List<Pradera>> =
            db.listObsByExp("idFinca" equalEx idFinca andEx ("available" equalEx false), Pradera::class)
                    .applySchedulers()

    fun getUnusedMeadows(idFinca: String): Observable<List<Pradera>> =
            db.listObsByExp("idFinca" equalEx idFinca andEx ("available" equalEx true), Pradera::class)
                    .applySchedulers()

    fun getGroups(idFinca: String): Observable<List<Group>> =
            db.listObsByExp("finca" equalEx idFinca andEx (Expression.property("pradera").isNullOrMissing), Group::class)
                    .applySchedulers()

    fun getGroupById(idGroup: String): Maybe<Group> =
            db.oneByExp("nombre" equalEx idGroup, Group::class)
                    .applySchedulers()

    // Filtros
    fun getBovinesFilter(idFinca: String): Single<List<Bovino>> =
            db.listByExp("finca" equalEx idFinca, Bovino::class)
                    .applySchedulers()


    //region Vacunas
    fun inserVaccine(registroVacuna: RegistroVacuna): Single<String> = db.insert(registroVacuna)
            .flatMap {
                makeAlarm(it, registroVacuna.bovinos ?: emptyList(), registroVacuna.grupo,
                        registroVacuna.titulo!!, registroVacuna.descripcion!!, null, null,
                        registroVacuna.fechaProxima, ALARM_VACCINE
                )
            }
            .applySchedulers()

    fun inserFirstVaccine(registroVacuna: RegistroVacuna): Single<String> = db.insertDosisUno(registroVacuna)
            .flatMap {
                makeAlarm(it, registroVacuna.bovinos ?: emptyList(), registroVacuna.grupo,
                        registroVacuna.titulo!!, registroVacuna.descripcion!!, null, null,
                        registroVacuna.fechaProxima, ALARM_VACCINE
                )
            }
            .applySchedulers()

    fun updateVaccine(registroVacuna: RegistroVacuna): Single<Unit> = db.update(registroVacuna._id!!, registroVacuna)
            .flatMap { cancelAlarm(registroVacuna._id!!) }
            .applySchedulers()

    fun getVaccinations(): Observable<List<RegistroVacuna>> = SearchBarActivity.query
            .startWith("")
            .flatMap {
                var exp = "idFinca" equalEx userSession.farmID
                if (it != "") exp = exp andEx ("tipo" likeEx "%$it%" orEx ("nombre" likeEx "%$it%"))
                db.listObsByExp(exp, RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()
            }
            .applySchedulers()


    fun getNextVaccines(from: Date, to: Date): Observable<List<RegistroVacuna>> =
            db.listObsByExp("idFinca" equalEx userSession.farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    fun getPendingVaccines(from: Date): Observable<List<RegistroVacuna>> =
            db.listObsByExp("idFinca" equalEx userSession.farmID andEx ("fechaProxima".lte(from)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx ASCENDING)).applySchedulers()

    fun getVaccinesByDosisUno(idDosisUno: String): Single<List<RegistroVacuna>> =
            db.listByExp("idFinca" equalEx userSession.farmID andEx ("idAplicacionUno" equalEx idDosisUno), RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    //endregion
    fun getHealthApplied(idDosisUno: String): Single<List<Sanidad>> =
            db.listByExp("idFinca" equalEx userSession.farmID andEx ("idAplicacionUno" equalEx idDosisUno), Sanidad::class).applySchedulers()


    //region Manejo
    fun insertManageFirst(registroManejo: RegistroManejo): Single<String> = db.insertDosisUno(registroManejo)
            .flatMap {
                val title = if (registroManejo.tipo == "Otro") registroManejo.otro!!
                else registroManejo.tipo!!

                makeAlarm(it, registroManejo.bovinos ?: emptyList(), registroManejo.grupo,
                        title, registroManejo.tratamiento ?: "", registroManejo.aplicacion ?: 0,
                        registroManejo.numeroAplicaciones ?: 0,
                        registroManejo.fechaProxima, ALARM_MANAGE)
            }
            .applySchedulers()

    fun insertManage(registroManejo: RegistroManejo): Single<String> = db.insertDosisUno(registroManejo)
            .flatMap {
                val title = if (registroManejo.tipo == "Otro") registroManejo.otro!!
                else registroManejo.tipo!!

                makeAlarm(it, registroManejo.bovinos ?: emptyList(), registroManejo.grupo,
                        title, registroManejo.tratamiento ?: "", registroManejo.aplicacion ?: 0,
                        registroManejo.numeroAplicaciones ?: 0,
                        registroManejo.fechaProxima, ALARM_MANAGE)
            }
            .applySchedulers()

    fun getManages(): Observable<List<RegistroManejo>> = SearchBarActivity.query
            .startWith("")
            .flatMap {
                var exp = "idFinca" equalEx userSession.farmID
                if (it != "") exp = exp andEx ("titulo" likeEx "%$it%" orEx ("tratamiento" likeEx "%$it%") orEx ("otro" likeEx "%$it%") orEx ("producto" likeEx "%$it%"))
                db.listObsByExp(exp, RegistroManejo::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
            }.applySchedulers()

    fun updateManage(registroManejo: RegistroManejo): Single<Unit> = db.update(registroManejo._id!!, registroManejo)
            .flatMap { cancelAlarm(registroManejo._id!!) }
            .applySchedulers()

    fun getNextManages(from: Date, to: Date): Observable<List<RegistroManejo>> =
            db.listObsByExp("idFinca" equalEx userSession.farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroManejo::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    fun getPendingManages(from: Date): Observable<List<RegistroManejo>> =
            db.listObsByExp("idFinca" equalEx userSession.farmID andEx ("fechaProxima".lte(from)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroManejo::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    fun getManagesByDosisUno(idDosisUno: String): Single<List<RegistroManejo>> =
            db.listByExp("idFinca" equalEx userSession.farmID andEx ("idAplicacionUno" equalEx idDosisUno), RegistroManejo::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    //endregion

    fun getAllCows(): Single<List<Bovino>> = db.listByExp("finca" equalEx userSession.farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
            .applySchedulers()

    fun getBovineById(idBovino: String) = db.oneById(idBovino, Bovino::class).applySchedulers()


    fun validatePlan(): Boolean = userSession.validatePlanDate().first


    //region Notificaiones

    fun getNotifications(from: Date, to: Date): Single<List<Alarm>> =
            db.listByExp("idFinca" equalEx userSession.farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("activa" equalEx true), Alarm::class, orderBy = arrayOf("fechaProxima" orderEx DESCENDING))
                    .applySchedulers()

    private fun prepareNotificationBovine(id: String, bovines: List<String> = emptyList(), group: Grupo? = null) =
            if (bovines.size == 1 && group == null)
                db.oneById(bovines[0], Bovino::class)
                        .map { "${it._id};;${it.nombre};;${it.codigo}" to id }
                        .defaultIfEmpty("" to id)
                        .toSingle()
            else
                Single.just("" to id)

    private fun prepareAlarm(data: Pair<String, String>, bovines: List<String> = emptyList(), group: Grupo? = null, title: String, description: String, nextDate: Date?, notifyTime: Long, type: Int): Single<String> {
        val bvn = data.first.split(";;")
        var bvnInfo: AlarmBovine? = null
        var info = ""
        if (bvn.size > 2) {
            bvnInfo = AlarmBovine(bvn[0], bvn[1], bvn[2])
            info = "- Bovino ${bvnInfo.codigo}"
        }

        if (group != null) {
            info = "- Grupo ${group.nombre}"
        }

        val des = description + info

        val uuid = NotificationWork.notify(NotificationWork.TYPE_HEALTH, title,
                des, data.second, notifyTime, TimeUnit.HOURS, userSession.farmID)

        val alarm = Alarm(
                titulo = title,
                descripcion = description,
                alarma = type,
                idFinca = userSession.farmID,
                fechaProxima = nextDate,
                type = TYPE_ALARM,
                device = listOf(
                        AlarmDevice(userSession.device, uuid.toString())
                ),
                grupo = group,
                bovinos = bovines,
                activa = true,
                reference = data.second,
                bovino = bvnInfo
        )

        return db.insert(alarm)
    }

    private fun makeAlarm(id: String, bovines: List<String> = emptyList(), group: Grupo? = null, title: String, description: String, application: Int? = null, numApplication: Int? = null, nextDate: Date?, type: Int): Single<String> {
        val to = ((nextDate?.time ?: 0) / 3600000)
        val now = Date().time / 3600000
        return if (to > (now - DAY_7_HOUR) && (application == null || application < numApplication!!)) {
            prepareNotificationBovine(id, bovines, group)
                    .flatMap { prepareAlarm(it, bovines, group, title, description, nextDate, to - now, type) }
        } else {
            Single.just("")
        }
    }

    private fun cancelAlarm(reference: String): Single<Unit> {
        return db.listByExp("reference" equalEx reference
                andEx ("activa" equalEx true)
                andEx ("fechaProxima" gt Date())
                , Alarm::class)
                .flatMap {
                    if (it.isNotEmpty()) {
                        it[0].activa = false
                        NotificationWork.cancelAlarm(it[0], userSession.device)
                        db.update(it[0]._id!!, it[0])
                    } else {
                        Single.just(Unit)
                    }
                }
    }


    //endregion

}