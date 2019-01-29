package com.ceotic.ganko.ui.menu

import android.arch.lifecycle.ViewModel
import android.graphics.Color
import android.util.Log
import com.ceotic.ganko.R
import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.data.models.ProxStates.Companion.NOT_APPLIED
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.ui.common.SearchBarActivity
import com.ceotic.ganko.ui.menu.reports.ReportViewModel
import com.ceotic.ganko.util.*
import com.couchbase.lite.*
import hu.akarnokd.rxjava2.math.MathObservable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Created by Ana Marin on 11/03/2018.
 */
class MenuViewModel @Inject constructor(private val db: CouchRx, private val userSession: UserSession) : ViewModel() {

    private val farmID = userSession.farmID

    fun getFarmId(): String = farmID

    val reports:ReportViewModel by lazy { ReportViewModel(db, userSession)}


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
            db.listByExp("idFinca" equalEx farmID, Sanidad::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
                    .flatMapObservable { it.toObservable() }
                    .map { listOf(it.descripcion!!, it.producto!!, it.evento!!, it.diagnostico!!, it.descripcion!!, it.producto!!) }
                    .toList().applySchedulers()

    fun getHealth(idFinca: String): Observable<List<Sanidad>> = SearchBarActivity.query
            .startWith("")
            .flatMapSingle {
                var exp = "idFinca" equalEx farmID
                if (it != "") exp = exp andEx ("evento" likeEx "%$it%" orEx ("diagnostico" likeEx "%$it%"))
                db.listByExp(exp, Sanidad::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()
            }
            .applySchedulers()


    fun getNextHealth(from: Date, to: Date): Observable<List<Sanidad>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx ProxStates.NOT_APPLIED), Sanidad::class)
                    .applySchedulers()

    fun getPendingHealth(from: Date): Observable<List<Sanidad>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima" lt from) andEx ("estadoProximo" equalEx ProxStates.NOT_APPLIED), Sanidad::class)
                    .applySchedulers()

    fun updateHealth(id: String, sanidad: Sanidad): Single<Unit> = db.update(sanidad._id!!, sanidad).applySchedulers()

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
                var exp = "idFinca" equalEx farmID
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
    fun inserVaccine(registroVacuna: RegistroVacuna): Single<String> = db.insert(registroVacuna).applySchedulers()

    fun inserFirstVaccine(registroVacuna: RegistroVacuna): Single<String> = db.insertDosisUno(registroVacuna).applySchedulers()

    fun updateVaccine(registroVacuna: RegistroVacuna): Single<Unit> = db.update(registroVacuna._id!!, registroVacuna).applySchedulers()

    fun getVaccinations(): Observable<List<RegistroVacuna>> = SearchBarActivity.query
            .startWith("")
            .flatMap {
                var exp = "idFinca" equalEx farmID
                if (it != "") exp = exp andEx ("tipo" likeEx "%$it%" orEx ("nombre" likeEx "%$it%"))
                db.listObsByExp(exp, RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()
            }
            .applySchedulers()


    fun getNextVaccines(from: Date, to: Date): Observable<List<RegistroVacuna>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    fun getPendingVaccines(from: Date): Observable<List<RegistroVacuna>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".lte(from)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx ASCENDING)).applySchedulers()

    fun getVaccinesByDosisUno(idDosisUno: String): Single<List<RegistroVacuna>> =
            db.listByExp("idFinca" equalEx farmID andEx ("idAplicacionUno" equalEx idDosisUno), RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    //endregion
    fun getHealthApplied(idDosisUno: String): Single<List<Sanidad>> =
            db.listByExp("idFinca" equalEx farmID andEx ("idAplicacionUno" equalEx idDosisUno), Sanidad::class).applySchedulers()


    //region Manejo
    fun insertManage(registroManejo: RegistroManejo): Single<String> = db.insertDosisUno(registroManejo).applySchedulers()

    fun getManages(): Observable<List<RegistroManejo>> = SearchBarActivity.query
            .startWith("")
            .flatMap {
                var exp = "idFinca" equalEx farmID
                if (it != "") exp = exp andEx ("titulo" likeEx "%$it%" orEx ("tratamiento" likeEx "%$it%") orEx ("otro" likeEx "%$it%") orEx ("producto" likeEx "%$it%"))
                db.listObsByExp(exp, RegistroManejo::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
            }.applySchedulers()

    fun updateManage(registroManejo: RegistroManejo): Single<Unit> = db.update(registroManejo._id!!, registroManejo).applySchedulers()

    fun getNextManages(from: Date, to: Date): Observable<List<RegistroManejo>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroManejo::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    fun getPendingManages(from: Date): Observable<List<RegistroManejo>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".lte(from)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroManejo::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    fun getManagesByDosisUno(idDosisUno: String): Single<List<RegistroManejo>> =
            db.listByExp("idFinca" equalEx farmID andEx ("idAplicacionUno" equalEx idDosisUno), RegistroManejo::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    //endregion

    fun getNotifications(from: Date, to: Date): Single<List<Alarm>> =
            db.listByExpNoType("idFinca" equalEx farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx NOT_APPLIED), Alarm::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    //region ReportesReproductivo
    private val VAR_SERV = ArrayExpression.variable("servicio")
    private val VAR_NOVEDAD = ArrayExpression.variable("servicio.novedad.novedad")
    private val VAR_PARTO = ArrayExpression.variable("servicio.parto")
    private val VAR_EMPADRE = ArrayExpression.variable("servicio.empadre")
    private val VAR_FECHA_PARTO = ArrayExpression.variable("servicio.parto.fecha")
    private val VAR_FECHA_NOVEDAD = ArrayExpression.variable("servicio.novedad.fecha")
    private val VAR_FINALIZADO = ArrayExpression.variable("servicio.finalizado")
    private val VAR_CONFIRMACION = ArrayExpression.variable("servicio.diagnostio.confirmacion")


    fun totalServicios(from: Date, to: Date): Single<Promedio> =
            db.listByExp("finca" equalEx farmID
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter { servicio ->
                            servicio.fecha!!.time >= from.time && servicio.fecha!!.time <= to.time
                        }
                    }
                    .count().map {
                        Promedio("Servicios", it, desde = from, hasta = to)
                    }.applySchedulers()


    fun totalServicios(mes: Int, anio: Int): Single<Promedio> =
            db.listByExp("finca" equalEx farmID
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter { servicio ->
                            val fechaServicio = servicio.fecha!!
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = fechaServicio!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            month == mes && year == anio

                        }
                    }
                    .count().map {
                        Promedio("Servicios", it, mes = mes, anio = anio)
                    }.applySchedulers()

    fun totalServiciosEfectivos(from: Date, to: Date): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter { servicio ->
                            (servicio.diagnostico?.confirmacion
                                    ?: false) && servicio.fecha!!.time >= from.time && servicio.fecha!!.time <= to.time
                        }
                    }
                    .count().map {
                        Promedio("Servicios Efectivos", it, desde = from, hasta = to)
                    }.applySchedulers()

    fun totalServiciosEfectivos(mes: Int, anio: Int): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter { servicio ->
                            val fechaServicio = servicio.fecha!!
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = fechaServicio!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)

                            (servicio.diagnostico?.confirmacion
                                    ?: false) && month == mes && year == anio
                        }
                    }
                    .count().map {
                        Promedio("Servicios Efectivos", it, mes = mes, anio = anio)
                    }.applySchedulers()

    fun totalServiciosMontaNatural(): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_EMPADRE.equalTo(Expression.string("Monta Natural")))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()
                    }.count().map {
                        Promedio("Total Servicios Monta Natural", it)
                    }.applySchedulers()

    fun totalServiciosMontaNaturalEfectivos(): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_EMPADRE.equalTo(Expression.string("Monta Natural")))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter {
                            it.diagnostico?.confirmacion ?: false
                        }
                    }.count()
                    .map {
                        Promedio("Total Servicios Monta Natural Efectivos", it)
                    }.applySchedulers()

    fun totalServiciosInseminacionArtificial(): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_EMPADRE.equalTo(Expression.string("Inseminación Artificial")))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()
                    }.count().map {
                        Promedio("Total Servicios Inseminacion Artificial", it)
                    }.applySchedulers()

    fun totalServiciosInseminacionArtificialEfectivos(): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_EMPADRE.equalTo(Expression.string("Inseminación Artificial")))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter {
                            it.diagnostico?.confirmacion ?: false
                        }
                    }.count()
                    .map {
                        Promedio("Total Servicios Inseminacion Artificial Efectivos", it)
                    }.applySchedulers()

    fun promedioIntervaloPartos(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Maybe<Float> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        return db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(2)))
                andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                .satisfies(VAR_PARTO.notNullOrMissing() andEx (VAR_FECHA_PARTO.between(Expression.date(ini), Expression.date(end))))
                , Bovino::class)
                .flatMapObservable { it.toObservable() }
                .flatMap { (it.servicios ?: emptyList()).toObservable() }
                .filter { it.parto != null && it.parto!!.intervalo != 0 }
                .filter {
                    val current = it.parto!!.fecha!!.time
                    it.parto?.intervalo != null && current >= iniMilis && current < endMilis
                }
                .map { it.parto!!.intervalo }
                .to(MathObservable::averageFloat)
                .first(0f)
                .toMaybe()
                .applySchedulers()

    }


    fun intervaloPartosBovino(idBovino: String, from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Maybe<Float> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        return db.oneById(idBovino, Bovino::class)
                .flatMapObservable { (it.servicios ?: emptyList()).toObservable() }
                .filter { it.parto != null && it.parto!!.intervalo != 0 }
                .filter {
                    val current = it.parto!!.fecha!!.time
                    it.parto?.intervalo != null && current >= iniMilis && current < endMilis
                }
                .map { it.parto!!.intervalo }
                .to(MathObservable::averageFloat)
                .first(0f)
                .toMaybe()
                .applySchedulers()

    }

    fun promedioDiasVacios(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Maybe<Float> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        val currDate = Date()
        val currMilis = currDate.time

        return db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                andEx ArrayFunction.length(Expression.property("servicios")).greaterThan(Expression.intValue(0)), Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map { it._id to it.servicios }
                .groupBy { it.first }
                .flatMap { gp -> gp.flatMapSingle { processEmptyDays(it.second!!, ini, iniMilis, endMilis, currMilis, currDate) } }
                .flatMap { it.toObservable() }
                .map { it.diasVacios }
                .to(MathObservable::averageFloat)
                .first(0f)
                .toMaybe()

    }


    private fun processEmptyDays(x: List<Servicio>, ini: Date?, iniMilis: Long, endMilis: Long, currMilis: Long, currDate: Date) =
            x.toObservable()
                    .filter { it.diagnostico?.confirmacion == true && it.diasVacios != 0f }
                    .filter {
                        if (ini != null) {
                            val current = it.fecha!!.time
                            it.diasVacios != null && current >= iniMilis && current <= endMilis
                        } else {
                            it.diasVacios != null
                        }
                    }
                    .toList()
                    .flatMap { list ->
                        if (ini != null && list.size == 0) {
                            x.toObservable()
                                    .filter {
                                        val current = it.fecha!!.time
                                        current < iniMilis && (it.diagnostico?.confirmacion == true)
                                    }
                                    .filter { it.parto != null || it.novedad != null }
                                    .map {
                                        val milis = if (currMilis < endMilis) currMilis else endMilis
                                        val curr = if (it.parto != null) it.parto!!.fecha?.time else it.novedad!!.fecha.time
                                        var dif: Double = (milis - (curr ?: 0)).toDouble()
                                        dif = (dif - (dif % 86400000)) / 86400000
                                        Servicio(fecha = Date(milis), diasVacios = dif.toFloat(), diagnostico = Diagnostico(Date(), true))

                                    }
                                    .map { mutableListOf(it) }
                                    .first(mutableListOf())

                        } else {
                            Single.just(list)
                                    .map {
                                        val cMilis = if (currMilis < endMilis) currMilis else endMilis
                                        val srv = it.indexOfFirst { zz -> zz.novedad != null || zz.parto != null }

                                        if (srv >= 0 && (srv == 0 || it[0].diagnostico?.confirmacion != true)) {
                                            val preDate = if (it[srv].novedad != null) it[srv].novedad!!.fecha else it[srv].parto!!.fecha
                                            val milis = preDate!!.time
                                            var dif: Double = (cMilis - milis).toDouble()
                                            dif = (dif - (dif % 86400000)) / 86400000
                                            if (dif > 0) {
                                                it.add(0, Servicio(fecha = currDate, diasVacios = dif.toFloat(), diagnostico = Diagnostico(Date(), true)))
                                            }
                                        }

                                        it
                                    }

                        }
                    }


    fun promedioEdad(date: Date) =
            db.listByExp("finca" equalEx farmID
                    andEx (
                    (Expression.property("fechaIngreso").notNullOrMissing() andEx ("fechaIngreso" lte date))
                            orEx (Expression.property("fechaIngreso").isNullOrMissing andEx ("fechaNacimiento" lte date))
                    )
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .map {
                        val dif = date.time - it.fechaNacimiento!!.time
                        val days = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                        val months = days.toFloat() / 30f
                        months

                    }
                    .toList()
                    .flatMapMaybe {
                        val tot = it.size
                        it.toObservable().reduce { t1: Float, t2: Float -> t2 + t1 }.map { sum ->
                            sum / tot.toFloat()
                        }
                    }.defaultIfEmpty(0f).applySchedulers()

    fun diasVaciosBovino(idBovino: String, from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Maybe<Float> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        val currDate = Date()
        val currMilis = currDate.time
        return db.oneById(idBovino, Bovino::class)
                .map { it.servicios ?: emptyList() }
                .flatMapSingle { processEmptyDays(it, ini, iniMilis!!, endMilis, currMilis, currDate) }
                .flatMapObservable { it.toObservable() }
                .map { it.diasVacios }
                .to(MathObservable::averageFloat)
                .first(0f)
                .toMaybe()
    }


    fun totalAbortos(from: Date, to: Date): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_NOVEDAD.equalTo(Expression.string("Aborto")))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter { servicio ->
                            servicio.novedad?.novedad == "Aborto" && servicio.novedad?.fecha!!.time >= from.time && servicio.novedad?.fecha!!.time <= to.time
                        }
                    }.count()
                    .map {
                        Promedio("Abortos", it, desde = from, hasta = to)
                    }.applySchedulers()

    fun totalAbortos(mes: Int, anio: Int): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_NOVEDAD.equalTo(Expression.string("Aborto")))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter { servicio ->
                            val mesCorrecto = if (servicio.novedad?.novedad == "Aborto") {
                                val fechaAborto = servicio.novedad!!.fecha
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = fechaAborto.time
                                val month = cal.get(Calendar.MONTH)
                                val year = cal.get(Calendar.YEAR)
                                month == mes && year == anio
                            } else {
                                false
                            }
                            mesCorrecto
                        }
                    }.count()
                    .map {
                        Promedio("Abortos", it, mes = mes, anio = anio)
                    }.applySchedulers()

    fun totalPartos(from: Date, to: Date): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_PARTO.notNullOrMissing())
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter { servicio ->
                            servicio.parto != null && servicio.parto?.fecha!!.time >= from.time && servicio.parto?.fecha!!.time <= to.time
                        }
                    }.count()
                    .map {
                        Promedio("Partos", it, desde = from, hasta = to)
                    }.applySchedulers()

    fun partosPorMes(mes: Int, anio: Int): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_PARTO.notNullOrMissing())
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()
                                ?.filter { serv ->
                                    val mesCorrecto = if (serv.parto != null) {
                                        val fechaParto = serv.parto!!.fecha
                                        val cal = Calendar.getInstance()
                                        cal.timeInMillis = fechaParto!!.time
                                        val month = cal.get(Calendar.MONTH)
                                        val year = cal.get(Calendar.YEAR)
                                        month == mes && year == anio
                                    } else {
                                        false
                                    }
                                    serv.finalizado == true && mesCorrecto
                                }
                    }.count().map {
                        Promedio("Partos", it, mes = mes, anio = anio)
                    }.applySchedulers()

    fun promedioGananciaPeso(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null, idBovino: String? = null): Single<Float> {
        val (ini, end) = processDates(from, to, month, year)
        var exp = "finca" equalEx farmID andEx ("gananciaPeso" isNullEx false)
        if (idBovino != null) {
            exp = exp andEx ("bovino" equalEx idBovino)
        }
        if (ini != null) {
            exp = exp andEx "fecha".betweenDates(ini, end!!)
        }

        return db.listByExp(exp, Ceba::class)
                .flatMapObservable { it.toObservable() }
                .map { it.gananciaPeso }
                .to(MathObservable::averageFloat)
                .first(0f)
                .applySchedulers()
    }


    fun promedioAlimentacion(tipoAlimento: String, from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null, bovino: String? = null): Maybe<Pair<Int, Int>> {
        val (ini, end) = processDates(from, to, month, year)
        var exp = "idFinca" equalEx farmID andEx ("tipoAlimento" equalEx tipoAlimento)
        if (bovino != null) {
            exp = exp andEx ("bovinos" containsEx bovino)
        }
        if (ini != null) {
            exp = exp andEx ("fecha".betweenDates(ini, end!!))
        }

        return db.listByExp(exp, RegistroAlimentacion::class)
                .flatMapObservable { it.toObservable() }
                .compose {
                    it.map { x ->
                        (x.peso ?: 0).toFloat() / (if (bovino != null) x.bovinos?.size ?: 1 else 1)
                    }
                            .to(MathObservable::averageFloat)
                            .map { x -> x.roundToInt() }
                            .zipWith(it.map { x ->
                                (x.valorTotal ?: 0).toFloat() / (if (bovino != null) x.bovinos?.size
                                        ?: 1 else 1)
                            }
                                    .to(MathObservable::averageFloat)
                                    .map { x -> x.roundToInt() })

                }
                .first(0 to 0)
                .toMaybe()
                .applySchedulers()

    }

    fun promedioLeche(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null, bovino: String? = null): Maybe<Int> {
        var exp = "idFinca" equalEx farmID!!
        val (ini, end) = processDates(from, to, month, year)

        if (bovino != null) {
            exp = exp andEx ("bovino" equalEx bovino)
        }

        if (ini != null) {
            exp = exp andEx "fecha".betweenDates(ini, end!!)
        }

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return db.listByExp(exp, Produccion::class)
                .flatMapObservable { it.toObservable() }
                .groupBy {
                    val key = "${it.bovino}_${format.format(it.fecha)}"
                    key
                }
                .flatMapSingle {
                    it.map { x -> x.litros?.toFloat() ?: 0f }
                            .reduce(0f) { a: Float, v: Float -> a + v }
                }
                .to(MathObservable::averageFloat)
                .map { Math.ceil(it.toDouble()).toInt() }
                .first(0)
                .toMaybe()
                .applySchedulers()

    }

    fun getPromedioLeche(from: Date? = null, to: Date? = null, mes: Int? = null, anio: Int? = null) = promedioLeche(from, to, mes, anio).map {
        Promedio("Producción de Leche", it, desde = from, hasta = to, mes = mes, anio = anio, unidades = "Litros")
    }

    fun promedioLecheTotalYBovino(bovino: String, from: Date? = null, to: Date? = null, mes: Int? = null, anio: Int? = null) =
            promedioLeche(from, to, mes, anio).zipWith(promedioLeche(from, to, mes, anio, bovino))
                    .map {
                        Promedio("Producción de Leche", it.first, bovino, it.second, desde = from, hasta = to, mes = mes, anio = anio, unidades = "Litros")
                    }


    fun getPromedioEdad(to: Date) = promedioEdad(to).map {
        Promedio("Edad en meses", it, unidades = "Meses")
    }

    fun getPromedioAlimentacionPorTipo(tipoAlimento: String, from: Date? = null, to: Date? = null, mes: Int? = null, anio: Int? = null, bovino: String? = null) =
            promedioAlimentacion(tipoAlimento, from, to, mes, anio, bovino).map {
                Promedio("Alimentación con $tipoAlimento", it.first,
                        desde = from,
                        hasta = to,
                        mes = mes,
                        anio = anio,
                        bovino = bovino,
                        valor = it.second,
                        unidades = "Kg",
                        unidadesPrecio = "$"
                )
            }

    fun getPromedioGDP(from: Date? = null, to: Date? = null, mes: Int? = null, anio: Int? = null) = promedioGananciaPeso(from, to, mes, anio).map {
        Promedio("Ganancia de Peso", it, desde = from, hasta = to, mes = mes, anio = anio, unidades = "Gr")
    }

    fun promedioGDPTotalYBovino(bovino: String, from: Date? = null, to: Date? = null, mes: Int? = null, anio: Int? = null) =
            promedioGananciaPeso(from, to, mes, anio).zipWith(promedioGananciaPeso(from, to, mes, anio, bovino))
                    .map {
                        Promedio("Ganancia de peso", it.first, bovino, it.second, desde = from, hasta = to, mes = mes, anio = anio, unidades = "Gr")
                    }

    fun getPromedioDiasVacios(from: Date? = null, to: Date? = null, mes: Int? = null, anio: Int? = null) = promedioDiasVacios(from, to, mes, anio).map {
        Promedio("Dias Vacios", it, unidades = "Días",
                desde = from,
                hasta = to,
                mes = mes,
                anio = anio
        )
    }

    fun getPromedioIntervaloPartos(from: Date? = null, to: Date? = null, mes: Int? = null, anio: Int? = null) =
            promedioIntervaloPartos(from, to, mes, anio).map {
                Promedio("Intervalo partos", it, unidades = "Días",
                        desde = from,
                        hasta = to,
                        mes = mes,
                        anio = anio
                )
            }

    fun promedioIntervaloPartosTotalYBovino(bovino: String, from: Date? = null, to: Date? = null, mes: Int? = null, anio: Int? = null) =
            promedioIntervaloPartos(from, to, mes, anio).zipWith(intervaloPartosBovino(bovino, from, to, mes, anio))
                    .map {
                        Promedio("Intervalo entre Partos", it.first, bovino, it.second, unidades = "Días",
                                desde = from,
                                hasta = to,
                                mes = mes,
                                anio = anio)
                    }


    fun promedioDiasVaciosTotalYBovino(bovino: String, from: Date? = null, to: Date? = null, mes: Int? = null, anio: Int? = null) =
            promedioDiasVacios(from, to, mes, anio)
                    .zipWith(diasVaciosBovino(bovino, from, to, mes, anio))
                    .map {
                        Promedio("Días Vacios", it.first, bovino, it.second, unidades = "Días",
                                desde = from,
                                hasta = to,
                                mes = mes,
                                anio = anio)
                    }

    fun getAllCows(): Single<List<Bovino>> = db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
            .applySchedulers()

    fun getBovineById(idBovino: String) = db.oneById(idBovino, Bovino::class).applySchedulers()


    fun validatePlan(): Boolean = userSession.validatePlanDate().first

}