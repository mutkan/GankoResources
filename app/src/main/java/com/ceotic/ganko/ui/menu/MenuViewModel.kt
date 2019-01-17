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

    fun insertMovement(movimiento: Movimiento): Single<String> =
            db.insert(movimiento).applySchedulers()


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

    fun promedioIntervaloPartos(from:Date? = null, to:Date? = null, month: Int? = null, year:Int? = null): Maybe<Float>{
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
                .filter{it.parto != null  && it.parto!!.intervalo != 0}
                .filter{
                        val current = it.parto!!.fecha!!.time
                        it.parto?.intervalo != null && current >= iniMilis && current < endMilis
                }
                .map { it.parto!!.intervalo }
                .to(MathObservable::averageFloat)
                .first(0f)
                .toMaybe()
                .applySchedulers()

    }


    fun intervaloPartosBovino(idBovino: String, from:Date? = null, to:Date? = null, month: Int? = null, year:Int? = null): Maybe<Float> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        return db.oneById(idBovino, Bovino::class)
                .flatMapObservable { (it.servicios ?: emptyList()).toObservable() }
                .filter{it.parto != null  && it.parto!!.intervalo != 0}
                .filter{
                    val current = it.parto!!.fecha!!.time
                    it.parto?.intervalo != null && current >= iniMilis && current < endMilis
                }
                .map { it.parto!!.intervalo }
                .to(MathObservable::averageFloat)
                .first(0f)
                .toMaybe()
                .applySchedulers()

    }

    fun promedioDiasVacios(from:Date? = null, to:Date?=null, month:Int?= null, year:Int?=null):Maybe<Float>{
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        val currDate = Date()
        val currMilis = currDate.time

        return db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                andEx ArrayFunction.length(Expression.property("servicios")).greaterThan(Expression.intValue(0)), Bovino::class)
                .flatMapObservable{ it.toObservable()}
                .map{ it._id to it.servicios}
                .groupBy { it.first }
                .flatMap { gp-> gp.flatMapSingle { processEmptyDays(it.second!!, ini, iniMilis, endMilis, currMilis, currDate) }}
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
                .flatMap {list->
                    if (ini != null && list.size == 0) {
                        x.toObservable()
                                .filter{
                                    val current = it.fecha!!.time
                                    current < iniMilis && (it.diagnostico?.confirmacion == true)
                                }
                                .filter{it.parto != null || it.novedad != null}
                                .map {
                                    val milis = if(currMilis < endMilis) currMilis else endMilis
                                    val curr = if(it.parto != null) it.parto!!.fecha?.time else it.novedad!!.fecha.time
                                    var dif:Double = (milis - (curr ?: 0)).toDouble()
                                    dif = (dif - (dif % 86400000)) / 86400000
                                    Servicio(fecha =  Date(milis), diasVacios = dif.toFloat(), diagnostico = Diagnostico(Date(), true))

                                }
                                .map { mutableListOf(it) }
                                .first(mutableListOf())

                        } else {
                             Single.just(list)
                                     .map{
                                        val cMilis = if(currMilis < endMilis) currMilis else endMilis
                                        val srv = it.indexOfFirst {zz -> zz.novedad !=null || zz.parto != null}

                                        if (srv >= 0 && (srv == 0 || it[0].diagnostico?.confirmacion != true)) {
                                            val preDate = if(it[srv].novedad != null) it [srv].novedad!!.fecha else it[srv].parto!!.fecha
                                            val milis = preDate!!.time
                                            var dif:Double = (cMilis - milis).toDouble()
                                            dif = (dif - (dif % 86400000)) / 86400000
                                            if (dif > 0) {
                                                it.add(0, Servicio(fecha =  currDate, diasVacios = dif.toFloat(), diagnostico = Diagnostico(Date(), true)))
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

        fun diasVaciosBovino(idBovino: String, from:Date? = null, to:Date?=null, month:Int?= null, year:Int?=null):Maybe<Float>{
            val (ini, end) = processDates(from, to, month, year)
            val iniMilis = ini!!.time
            val endMilis = end!!.time

            val currDate = Date()
            val currMilis = currDate.time
            return db.oneById(idBovino, Bovino::class)
                    .map { it.servicios ?: emptyList() }
                    .flatMapSingle { processEmptyDays(it, ini, iniMilis!!, endMilis, currMilis, currDate) }
                    .flatMapObservable{it.toObservable()}
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

        //region reportes

        //region reporte Alimentacion
        fun reporteAlimentacion(from: Date, to: Date): Single<MutableList<List<String?>>> =
                db.listByExp("idFinca" equalEx farmID andEx ("fecha".betweenDates(from, to)), RegistroAlimentacion::class)
                        .flatMapObservable { it.toObservable() }
                        .filter { it.bovinos != null }
                        .flatMap {
                            it.bovinos!!.toObservable()
                                    .flatMapMaybe { id ->
                                        db.oneById(id, Bovino::class)
                                    }.map { bov ->
                                        listOf(bov.codigo, it.tipoAlimento!!, it.valorkg!!.toString() + " Kg", it.valorTotal!!.toString())
                                    }
                        }
                        .toList().applySchedulers()


        fun reporteAlimentacion(mes: Int, anio: Int): Single<MutableList<List<String?>>> =
                db.listByExp("idFinca" equalEx farmID, RegistroAlimentacion::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = it.fecha!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            month == mes && year == anio
                        }
                        .flatMap {
                            it.bovinos!!.toObservable()
                                    .flatMapMaybe { id ->
                                        db.oneById(id, Bovino::class)
                                    }.map { bov ->
                                        listOf(bov.codigo, it.tipoAlimento!!, it.valorkg!!.toString() + " Kg", it.valorTotal!!.toString())
                                    }
                        }.toList().applySchedulers()
        //endregion

        //region reporte sanidad
        fun reporteSanidad(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("idFinca" equalEx farmID andEx "fecha".betweenDates(from, to), Sanidad::class
                        , orderBy = arrayOf("fecha" orderEx DESCENDING))
                        .flatMapObservable {
                            it.toObservable()
                        }.flatMap { sanidad ->
                            sanidad.bovinos?.toObservable()?.flatMapMaybe { db.oneById(it, Bovino::class) }
                                    ?.map {
                                        //ReporteSanidad(it.codigo!!, it.nombre, sanidad.fecha!!, sanidad.evento!!, sanidad.diagnostico!!, sanidad.producto!!)
                                        listOf(it.codigo!!, it.nombre!!, sanidad.fecha!!.toStringFormat(), sanidad.evento!!, sanidad.diagnostico!!, sanidad.producto!!)
                                    }
                        }.toList().applySchedulers()


        fun reporteSanidad(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("idFinca" equalEx farmID, Sanidad::class
                        , orderBy = arrayOf("fecha" orderEx DESCENDING))
                        .flatMapObservable {
                            it.toObservable().filter {
                                val fechaSanidad = it.fecha!!
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = fechaSanidad.time
                                val month = cal.get(Calendar.MONTH)
                                val year = cal.get(Calendar.YEAR)
                                month == mes && year == anio
                            }
                        }.flatMap { sanidad ->
                            sanidad.bovinos?.toObservable()?.flatMapMaybe { db.oneById(it, Bovino::class) }
                                    ?.map {
                                        // ReporteSanidad(it.codigo!!, it.nombre, sanidad.fecha!!, sanidad.evento!!, sanidad.diagnostico!!, sanidad.producto!!)
                                        listOf(it.codigo!!, it.nombre!!, sanidad.fecha!!.toStringFormat(), sanidad.evento!!, sanidad.diagnostico!!, sanidad.producto!!)

                                    }
                        }.toList().applySchedulers()
        //endregion

        // region Reportes Leche

        fun reportesLeche(mes: Int, anio: Int): Single<MutableList<List<String?>>> =
                db.listByExp("idFinca" equalEx farmID, Produccion::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = it.fecha!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            month == mes && year == anio
                        }.flatMapMaybe {
                            db.oneById(it.bovino!!, Bovino::class)
                                    .map { bov ->
                                        listOf(bov.codigo, it.litros!!.toString(), it.fecha!!.toStringFormat(), it.jornada!!)
                                    }
                        }.toList().applySchedulers()


        fun reportesLeche(from: Date, to: Date): Single<MutableList<List<String?>>> =
                db.listByExp("idFinca" equalEx farmID andEx ("fecha".betweenDates(from, to)), Produccion::class)
                        .flatMapObservable { it.toObservable() }
                        .flatMapMaybe {
                            db.oneById(it.bovino!!, Bovino::class)
                                    .map { bov ->
                                        listOf(bov.codigo, it.litros!!.toString(), it.fecha!!.toStringFormat(), it.jornada!!)
                                    }
                        }.toList().applySchedulers()


        fun reporteConsolidado(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("idFarm" equalEx farmID!!, SalidaLeche::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = it.fecha!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            month == mes && year == anio
                        }.map {

                            listOf(it.type!!, it.fecha!!.toStringFormat(), it.operacion!!, it.numeroLitros!!.toString(), it.valorLitro.toString(), (it.valorLitro!! * it.numeroLitros!!).toString()!!)
                        }.toList().applySchedulers()

        fun reporteConsolidado(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("idFarm" equalEx farmID!! andEx ("fecha".betweenDates(from, to)), SalidaLeche::class)
                        .flatMapObservable { it.toObservable() }
                        .map {
                            listOf(it.type!!, it.fecha!!.toStringFormat(), it.operacion!!, it.numeroLitros!!.toString(), it.valorLitro.toString(), (it.valorLitro!! * it.numeroLitros!!).toString()!!)
                        }.toList().applySchedulers()

        //endregion

        //region Reporte Vacunas
        fun reporteVacunas(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("idFinca" equalEx farmID
                        andEx "fecha".betweenDates(from, to)
                        , RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
                        .flatMapObservable {
                            it.toObservable()
                        }.flatMap { reg ->
                            reg.bovinos?.toObservable()?.flatMapMaybe { db.oneById(it, Bovino::class) }
                                    ?.map {
                                        //  ReporteVacunas(it.codigo!!, it.nombre, reg.fecha!!, reg.nombre!!)
                                        listOf(it.codigo!!, it.nombre!!, reg.fecha!!.toStringFormat(), reg.nombre!!)
                                    }
                        }.toList().applySchedulers()

        fun reporteVacunas(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("idFinca" equalEx farmID, RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
                        .flatMapObservable {
                            it.toObservable().filter {
                                val fechaVacuna = it.fecha!!
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = fechaVacuna.time
                                val month = cal.get(Calendar.MONTH)
                                val year = cal.get(Calendar.YEAR)
                                month == mes && year == anio
                            }
                        }.flatMap { reg ->
                            reg.bovinos?.toObservable()?.flatMapMaybe { db.oneById(it, Bovino::class) }
                                    ?.map {
                                        // ReporteVacunas(it.codigo!!, it.nombre, reg.fecha!!, reg.nombre!!)
                                        listOf(it.codigo!!, it.nombre!!, reg.fecha!!.toStringFormat(), reg.nombre!!)
                                    }
                        }.toList().applySchedulers()

        //endregion

        //region Reportes reproductivo


        fun getUltimoParto(idBovino: String): Single<List<Parto>> =
                db.listByExp("bovino" equalEx idBovino, Parto::class)
                        .applySchedulers()


        fun reportesDestete(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID!! andEx ("fechaDestete".betweenDates(from, to)), Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.fechaDestete!!.toStringFormat(), it.codigoMadre!!, it.codigoPadre!!)
                        }.toList().applySchedulers()

        fun reportesDestete(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID!!, Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = it.fechaDestete!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            month == mes && year == anio
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.fechaDestete!!.toStringFormat(), it.codigoMadre!!, it.codigoPadre!!)
                        }.toList().applySchedulers()
        //endregion

        //region Reporte Praderas
        fun reporteGetPraderas(): Single<List<List<String>>> =
                db.listByExp("idFinca" equalEx farmID, Pradera::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {

                            it.mantenimiento!!.size != 0
                        }
                        .map {
                            var graminea: String = ""

                            if (it.tipoGraminea == null) {
                                graminea = "No aplicada"
                            } else {
                                graminea = it.tipoGraminea!!
                            }
                            var ultimo = it.mantenimiento!![0]
                            listOf(it.identificador!!.toString(), graminea, ultimo.fechaMantenimiento!!.toStringFormat(), ultimo.producto!!, ultimo.cantidad.toString())
                        }
                        .toList().applySchedulers()

        fun reporteOcupacionPraderas(): Single<List<List<String>>> =
                db.listByExp("idFinca" equalEx farmID andEx ("identificador" isNullEx false) , Pradera::class)
                        .flatMapObservable { it.toObservable() }
                        .map {
                            if(it.available == false){

                                val ultimo = it.mantenimiento?.lastOrNull()
                                listOf(it.identificador?.toString() ?:"" , it.tipoGraminea?: "", ultimo?.fechaMantenimiento?.toStringFormat() ?: "", it.fechaOcupacion?.toStringFormat() ?: "")
                            }else{
                                listOf(it.identificador!!.toString(), "", "", "Libre")
                            }

                        }
                        .toList().applySchedulers()


        //endregion

        //region reporte manejo
        fun reporteManejo(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("idFinca" equalEx farmID andEx "fecha".betweenDates(from, to), RegistroManejo::class,
                        orderBy = arrayOf("fecha" orderEx DESCENDING))
                        .flatMapObservable {
                            it.toObservable()
                        }.flatMap { manejo ->
                            manejo.bovinos?.toObservable()?.flatMapMaybe { db.oneById(it, Bovino::class) }
                                    ?.map {
                                        // ReporteManejo(it.codigo!!, it.nombre, manejo.fecha!!, manejo.tipo!!, manejo.tratamiento!!, manejo.producto!!)
                                        listOf(it.codigo!!, it.nombre!!, manejo.fecha!!.toStringFormat(), manejo.tipo!!, manejo.tratamiento!!, manejo.producto!!)
                                    }
                        }.toList().applySchedulers()

        fun reporteManejo(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("idFinca" equalEx farmID, RegistroManejo::class,
                        orderBy = arrayOf("fecha" orderEx DESCENDING))
                        .flatMapObservable {
                            it.toObservable().filter {
                                val fechaManejo = it.fecha!!
                                val cal = Calendar.getInstance()
                                cal.timeInMillis = fechaManejo.time
                                val month = cal.get(Calendar.MONTH)
                                val year = cal.get(Calendar.YEAR)
                                month == mes && year == anio
                            }
                        }.flatMap { manejo ->
                            manejo.bovinos?.toObservable()?.flatMapMaybe { db.oneById(it, Bovino::class) }
                                    ?.map {
                                        //  ReporteManejo(it.codigo!!, it.nombre, manejo.fecha!!, manejo.tipo!!, manejo.tratamiento!!, manejo.producto!!)
                                        listOf(it.codigo!!, it.nombre!!, manejo.fecha!!.toStringFormat(), manejo.tipo!!, manejo.tratamiento!!, manejo.producto!!)
                                    }
                        }.toList().applySchedulers()
        //endregion

        // region Reporte Ganancia de peso
        fun reporteGananciaPeso(from: Date, to: Date): Single<MutableList<List<String?>>> =
                db.listByExp("finca" equalEx farmID, Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .flatMapMaybe {
                            db.listByExp("fecha".betweenDates(from, to) andEx ("bovino" equalEx it._id!!), Ceba::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
                                    .filter { it.isNotEmpty() }
                                    .map { cebaList ->
                                        var gananciaPeso = 1f
                                        var cebaMayor = Ceba()
                                        var cebaMenor = Ceba()
                                        if (cebaList.size == 1) {
                                            gananciaPeso = cebaList[0].gananciaPeso!!
                                        } else {
                                            for (v1 in 0 until cebaList.size) {
                                                if (cebaList[v1].eliminado == null || cebaList[v1].eliminado == false) cebaMayor = cebaList[v1]
                                            }
                                            for (v2 in (cebaList.size - 1) downTo 0) {
                                                if (cebaList[v2].eliminado == null || cebaList[v2].eliminado == false) cebaMenor = cebaList[v2]
                                            }
                                            var dif = cebaMayor.fecha!!.time - cebaMenor.fecha!!.time
                                            var dias = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                                            dias = if (dias == 0.toLong()) 1 else dias
                                            gananciaPeso = (cebaMayor.peso!! - cebaMenor.peso!!) / dias
                                        }
                                        listOf(it.codigo, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), gananciaPeso.toString(), it.proposito!!)
                                    }
                        }.toList().applySchedulers()

        fun reporteGananciaPeso(mes: Int, anio: Int): Single<MutableList<List<String?>>> =
                db.listByExp("finca" equalEx farmID, Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .flatMapMaybe {
                            db.listByExp(("bovino" equalEx it._id!!), Ceba::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
                                    .filter { it.isNotEmpty() }
                                    .flatMapObservable { it.toObservable() }
                                    .filter {
                                        val cal = Calendar.getInstance()
                                        cal.timeInMillis = it.fecha!!.time
                                        val month = cal.get(Calendar.MONTH)
                                        val year = cal.get(Calendar.YEAR)
                                        month == mes && year == anio
                                    }
                                    .toList()
                                    .filter { it.isNotEmpty() }
                                    .map { cebaList ->
                                        var gananciaPeso = 1f
                                        var cebaMayor = Ceba()
                                        var cebaMenor = Ceba()
                                        if (cebaList.size == 1) {
                                            gananciaPeso = cebaList[0].gananciaPeso!!
                                        } else {
                                            for (v1 in 0 until cebaList.size) {
                                                if (cebaList[v1].eliminado == null || cebaList[v1].eliminado == false) cebaMayor = cebaList[v1]
                                            }
                                            for (v2 in (cebaList.size - 1) downTo 0) {
                                                if (cebaList[v2].eliminado == null || cebaList[v2].eliminado == false) cebaMenor = cebaList[v2]
                                            }
                                            var dif = cebaMayor.fecha!!.time - cebaMenor.fecha!!.time
                                            var dias = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                                            dias = if (dias == 0.toLong()) 1 else dias
                                            gananciaPeso = (cebaMayor.peso!! - cebaMenor.peso!!) / dias
                                        }
                                        listOf(it.codigo, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), gananciaPeso.toString(), it.proposito!!)
                                    }
                        }.toList().applySchedulers()

        //endregion

        //region Entradas

        //region Reporte Inventario
        fun reporteInventario(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("fechaNacimiento".betweenDates(from, to)), Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .map {
                            var partos: String = ""
                            if (it.partos == null) partos = "0" else partos = it.partos.toString()
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.color!!, it.raza!!, partos, it.codigoMadre
                                    ?: "", it.codigoPadre ?: "")
                        }.toList().applySchedulers()

        fun reporteInventario(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID, Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = it.fechaNacimiento!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            month == mes && year == anio
                        }
                        .map {
                            val partos: String = ""
                            if (it.partos == null) '0' else it.partos.toString()
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.color!!, it.raza!!, partos, it.codigoMadre
                                    ?: "", it.codigoPadre ?: "")
                        }.toList().applySchedulers()

        //endregion

        //region Reporte Terneras en estaca
        fun reporteTernerasEnEstaca(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("fechaNacimiento".betweenDates(from, to) andEx ("genero" equalEx "Hembra")), Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val tresdias: Long = 3 * 24 * 60 * 60
                            val cincomeses: Long = 5 * 30 * 24 * 60 * 60
                            val dif = (Date().time - it.fechaNacimiento!!.time) / 1000
                            dif in tresdias..cincomeses
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre
                                    ?: "", it.codigoPadre ?: "")
                        }.toList().applySchedulers()


        fun reporteTernerasEnEstaca(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = it.fechaNacimiento!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            val tresdias: Long = 3 * 24 * 60 * 60
                            val cincomeses: Long = 5 * 30 * 24 * 60 * 60
                            val dif = (Date().time - it.fechaNacimiento!!.time) / 1000
                            dif in tresdias..cincomeses && month == mes && anio == year
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre
                                    ?: "", it.codigoPadre ?: "")
                        }.toList().applySchedulers()


        //endregion

        //region terneras Destetas

        fun reporteTernerasDestetas(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID, Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val seismeses: Long = 15552000
                            val docemeses: Long = 31104000
                            val difInferior = (from.time - it.fechaNacimiento!!.time) / 1000
                            val difSuperior = (to.time - it.fechaNacimiento!!.time) / 1000
                            difInferior in seismeses..docemeses || difSuperior in seismeses..docemeses
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre
                                    ?: "", it.codigoPadre ?: "")
                        }.toList().applySchedulers()


        fun reporteTernerasDestetas(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID, Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val seismeses: Long = 15552000
                            val docemeses: Long = 31104000
                            val fechaInferior = Date(Calendar.getInstance().get(Calendar.YEAR) - 1900, mes, 1)
                            val fechaSuperior = fechaInferior.add(Calendar.MONTH, 1)
                            val difInferior = (fechaInferior.time - it.fechaNacimiento!!.time) / 1000
                            val difSuperior = (fechaSuperior!!.time - it.fechaNacimiento!!.time) / 1000
                            difInferior in seismeses..docemeses || difSuperior in seismeses..docemeses
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre
                                    ?: "", it.codigoPadre ?: "")
                        }.toList().applySchedulers()

        //endregion

        //region novillas levante
        fun reporteTerneraslevante(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("fechaNacimiento".betweenDates(from, to) andEx ("genero" equalEx "Hembra")), Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val docemese: Long = 12 * 30 * 24 * 60 * 60
                            val diesiochomeses: Long = 18 * 30 * 24 * 60 * 60
                            val dif = (Date().time - it.fechaNacimiento!!.time) / 1000
                            dif in docemese..diesiochomeses
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre
                                    ?: "", it.codigoPadre ?: "")
                        }.toList().applySchedulers()


        fun reporteTerneraslevante(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = it.fechaNacimiento!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            val cal1 = Calendar.getInstance()
                            val docemese: Long = 12 * 30 * 24 * 60 * 60
                            val diesiochomeses: Long = 18 * 30 * 24 * 60 * 60
                            val dif = (Date().time - it.fechaNacimiento!!.time) / 1000
                            dif in docemese..diesiochomeses
                            it.fechaNacimiento!!.time in docemese..diesiochomeses && month == mes && anio == year
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!)
                        }.toList().applySchedulers()
        //endregion

        //region novillas vientre
        fun reporteNovillasVientre(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("fechaNacimiento".betweenDates(from, to) andEx ("genero" equalEx "Hembra")), Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val veintemesesmese: Long = 20 * 30 * 24 * 60 * 60
                            val diesiochomeses: Long = 18 * 30 * 24 * 60 * 60
                            val dif = (Date().time - it.fechaNacimiento!!.time) / 1000
                            dif in diesiochomeses..veintemesesmese
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!)
                        }.toList().applySchedulers()


        fun reporteNovillasVientre(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = it.fechaNacimiento!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            val cal1 = Calendar.getInstance()
                            val veintemesesmese: Long = 20 * 30 * 24 * 60 * 60
                            val diesiochomeses: Long = 18 * 30 * 24 * 60 * 60
                            val dif = (Date().time - it.fechaNacimiento!!.time) / 1000
                            dif in diesiochomeses..veintemesesmese && month == mes && anio == year
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!)
                        }.toList().applySchedulers()
        //endregion


        //region reporte futuros partos

        fun reporteFuturosPartos(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID
                        andEx ("servicios[0].finalizado" equalEx false)
                        andEx (Expression.property("servicios[0].posFechaParto").notNullOrMissing())
                        , Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val serv = it.servicios!![0]
                            val fechaPosParto = serv.posFechaParto!!
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = fechaPosParto.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            !serv.finalizado!! && month == mes && year == anio
                        }.map {
                            val serv = it.servicios!![0]
                            //ReporteFuturosPartos(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!)
                            listOf(it.codigo!!, it.nombre!!, serv.fecha!!.toStringFormat(), serv.posFechaParto!!.toStringFormat())
                        }.toList().applySchedulers()


        fun reporteFuturosPartos(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID
                        andEx ("servicios[0].finalizado" equalEx false)
                        andEx ("servicios[0].posFechaParto".betweenDates(from, to))
                        , Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .map {
                            val serv = it.servicios!![0]
                            //ReporteFuturosPartos(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!)
                            listOf(it.codigo!!, it.nombre!!, serv.fecha!!.toStringFormat(), serv.posFechaParto!!.toStringFormat())
                        }.toList().applySchedulers()

        //endregion

        //region reporte secado

        fun reporteSecado(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID
                        andEx ("servicios[0].finalizado" equalEx false)
                        andEx (Expression.property("servicios[0].posFechaParto").notNullOrMissing())
                        , Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val serv = it.servicios!![0]
                            val fechaPosParto = serv.posFechaParto!!
                            val fechaSecado = fechaPosParto.add(Calendar.DATE, -60)!!
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = fechaSecado.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            !serv.finalizado!! && month == mes && year == anio
                        }.map {
                            val serv = it.servicios!![0]
                            //ReporteSecado(it.codigo!!, it.nombre!!, serv.fecha!!, serv.posFechaParto!!.add(Calendar.DATE, -60)!!)
                            listOf(it.codigo!!, it.nombre!!, serv.fecha!!.toStringFormat(), serv.posFechaParto!!.add(Calendar.DATE, -60)!!.toStringFormat())
                        }.toList().applySchedulers()


        fun reporteSecado(from: Date, to: Date): Single<List<List<String>>> {
            val fromParto = from.add(Calendar.DATE, 60)
            val toParto = to.add(Calendar.DATE, 60)
            return db.listByExp("finca" equalEx farmID
                    andEx ("servicios[0].finalizado" equalEx false)
                    andEx ("servicios[0].posFechaParto".betweenDates(fromParto!!, toParto!!))
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .map {
                        val serv = it.servicios!![0]
                        //ReporteFuturosPartos(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!.add(Calendar.DATE, -60)!!)
                        listOf(it.codigo!!, it.nombre!!, serv.fecha!!.toStringFormat(), serv.posFechaParto!!.add(Calendar.DATE, -60)!!.toStringFormat())
                    }.toList().applySchedulers()
        }

        //endregion

        //region reportes preparacion
        fun reportePreparacion(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID
                        andEx ("servicios[0].finalizado" equalEx false)
                        andEx (Expression.property("servicios[0].posFechaParto").notNullOrMissing())
                        , Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val serv = it.servicios!![0]
                            val fechaPosParto = serv.posFechaParto!!
                            val fechaPreparacion = fechaPosParto.add(Calendar.DATE, -30)!!
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = fechaPreparacion.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            !serv.finalizado!! && month == mes && year == anio
                        }.map {
                            val serv = it.servicios!![0]
                            //ReportePreparacion(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!.add(Calendar.DATE, -30)!!)
                            listOf(it.codigo!!, it.nombre!!, serv.posFechaParto!!.toStringFormat(), serv.posFechaParto!!.add(Calendar.DATE, -30)!!.toStringFormat())
                        }.toList().applySchedulers()


        fun reportePreparacion(from: Date, to: Date): Single<List<List<String>>> {
            val fromParto = from.add(Calendar.DATE, 30)
            val toParto = to.add(Calendar.DATE, 30)
            return db.listByExp("finca" equalEx farmID
                    andEx ("servicios[0].finalizado" equalEx false)
                    andEx ("servicios[0].posFechaParto".betweenDates(fromParto!!, toParto!!))
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .map {
                        val serv = it.servicios!![0]
                        //ReportePreparacion(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!.add(Calendar.DATE, -30)!!)
                        listOf(it.codigo!!, it.nombre!!, serv.posFechaParto!!.toStringFormat(), serv.posFechaParto!!.add(Calendar.DATE, -30)!!.toStringFormat())
                    }.toList().applySchedulers()
        }

        //endregion

        //region reporte dias vacios
        fun reporteDiasVacios(): Single<MutableList<List<String?>>> {
            return db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx ("retirado" equalEx false)
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter { it.servicios != null }
                    .toList()
                    .flatMapObservable { it.toObservable() }
                    .flatMapSingle { bovino ->
                        var ultimoEvento: Date? = null
                        val enSer = bovino.servicios!!.find {
                            it.finalizado == false
                        }
                        bovino.servicios!!.toObservable()
                                .filter { it.finalizado == true && it.diagnostico?.confirmacion == true }
                                .toList()
                                .map { servicios ->
                                    if (servicios.isNotEmpty()) {
                                        servicios.first()
                                    } else Servicio()
                                }.map { ultimoServicio ->
                                    if (ultimoServicio.finalizado != null) {
                                        ultimoEvento = ultimoServicio.parto?.fecha ?: ultimoServicio.novedad?.fecha
                                        var diasVacios: Long
                                        diasVacios = if (enSer != null) {
                                            val dif = enSer.fecha!!.time - ultimoEvento!!.time
                                            TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                                        } else {
                                            val dif = Date().time - ultimoEvento!!.time
                                            TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                                        }
                                        //ReporteDiasVacios(bovino.codigo!!, bovino.nombre, ultimoParto.fecha!!, ultimoServicio.fecha!!, diasVacios, enServicio)

                                        listOf(bovino.codigo, bovino.nombre,
                                                ultimoEvento!!.toStringFormat(),
                                                if (enSer != null) enSer.fecha!!.toStringFormat() else ultimoServicio.fecha!!.toStringFormat(),
                                                diasVacios.toString(), if (enSer != null) "Si" else "No")
                                    } else {
                                        if (enSer != null) {
                                            listOf(bovino.codigo, bovino.nombre,
                                                    "Sin parto o Aborto",
                                                    enSer.fecha!!.toStringFormat(), 0.toString(), "Si")
                                        } else {
                                            listOf(bovino.codigo, bovino.nombre,
                                                    if (ultimoEvento != null) ultimoEvento!!.toStringFormat() else "Sin parto o Aborto",
                                                    ultimoServicio.fecha?.toStringFormat(), 0.toString(), "No")
                                        }
                                    }
                                }
                    }.toList().applySchedulers()
        }
        //endregion

        //region reporte pajillas

        fun reportePajillas(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("idFarm" equalEx farmID andEx ("fecha".betweenDates(from, to)), Straw::class)
                        .flatMapObservable { it.toObservable() }
                        .map {
                            listOf(it.idStraw!!, it.layette!!, it.breed!!, it.purpose!!, it.bull!!, it.origin!!)
                        }.toList().applySchedulers()


        fun reportePajillas(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("idFarm" equalEx farmID, Straw::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val fecha = it.fecha!!
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = fecha.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            month == mes && year == anio
                        }
                        .map {
                            listOf(it.idStraw!!, it.layette!!, it.breed!!, it.purpose!!, it.bull!!, it.origin!!)
                        }.toList().applySchedulers()

        //endregion reporte

        //region partos atendidos
        fun reportePartosAtendidos(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                        andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                        .satisfies(VAR_PARTO.notNullOrMissing())
                        andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                        .satisfies(VAR_FECHA_PARTO.between(Expression.date(from), Expression.date(to)))
                        , Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .flatMap { bovino ->
                            bovino.servicios!!.toObservable()
                                    .filter { it.parto?.fecha?.after(from) ?: false && it.parto?.fecha?.before(to) ?: false }
                                    .map {
                                        val parto = it.parto!!
                                        //ReportePartosAtendidos(bovino.codigo!!, bovino.nombre, parto.fecha, parto.sexoCria, parto.estadoCria)
                                        listOf(bovino.codigo!!, bovino.nombre!!, parto.fecha!!.toStringFormat(), parto.sexoCria, parto.estadoCria)
                                    }
                        }.toList().applySchedulers()

        fun reportePartosAtendidos(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                        andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                        .satisfies(VAR_PARTO.notNullOrMissing())
                        , Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .flatMap { bovino ->
                            bovino.servicios!!.toObservable()
                                    .filter { serv ->
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
                                        serv.finalizado!! && mesCorrecto
                                    }
                                    .map {
                                        val parto = it.parto!!
                                        //ReportePartosAtendidos(bovino.codigo!!, bovino.nombre, parto.fecha, parto.sexoCria, parto.estadoCria)
                                        listOf(bovino.codigo!!, bovino.nombre!!, parto.fecha!!.toStringFormat(), parto.sexoCria, parto.estadoCria)
                                    }
                        }.toList().applySchedulers()
        //endregion

        //region reporte abortos
        fun reporteAbortos(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                        andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                        .satisfies(VAR_NOVEDAD.equalTo(Expression.string("Aborto")))
                        , Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .flatMap { bovino ->
                            bovino.servicios!!.toObservable()
                                    .filter { it.novedad != null }
                                    .filter {
                                        from.time < it.novedad?.fecha!!.time && it.novedad?.fecha!!.time < to.time /* && it.novedad?.fecha?.after(from) ?: false && it.novedad?.fecha?.before(to) ?: false*/
                                    }
                                    .map { servicio ->
                                        val novedad = servicio.novedad!!
                                        //ReporteAbortos(bovino.codigo!!, bovino.nombre, servicio.fecha!!, novedad.fecha)
                                        listOf(bovino.codigo!!, bovino.nombre!!, servicio.fecha!!.toStringFormat(), novedad.fecha.toStringFormat())
                                    }
                        }.toList().applySchedulers()

        fun reporteAbortos(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                        andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                        .satisfies(VAR_NOVEDAD.equalTo(Expression.string("Aborto")))
                        , Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .flatMap { bovino ->
                            bovino.servicios!!.toObservable()
                                    .filter { serv ->
                                        val mesCorrecto = if (serv.novedad != null) {
                                            val fechaNovedad = serv.novedad!!.fecha
                                            val cal = Calendar.getInstance()
                                            cal.timeInMillis = fechaNovedad.time
                                            val month = cal.get(Calendar.MONTH)
                                            val year = cal.get(Calendar.YEAR)
                                            month == mes && year == anio
                                        } else {
                                            false
                                        }
                                        serv.novedad?.novedad == "Aborto" && mesCorrecto
                                    }
                                    .map { servicio ->
                                        val novedad = servicio.novedad!!
                                        //ReporteAbortos(bovino.codigo!!, bovino.nombre, servicio.fecha!!, novedad.fecha)
                                        listOf(bovino.codigo!!, bovino.nombre!!, servicio.fecha!!.toStringFormat(), novedad.fecha.toStringFormat())
                                    }
                        }.toList().applySchedulers()
        //endregion

        //region reporte Tres servicios
        fun reporteTresServicios(): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                        andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(3)))
                        , Bovino::class)
                        .flatMapObservable {
                            it.toObservable()
                                    .filter { bovino ->
                                        val servicios = bovino.servicios!!
                                        servicios[0].diagnostico?.confirmacion?.not() ?: false && servicios[1].diagnostico?.confirmacion?.not() ?: false && servicios[2].diagnostico?.confirmacion?.not() ?: false
                                    }.map { bovino ->
                                        var fecha = bovino.servicios!![0].fecha
                                        var ultimoServicio = Servicio()
                                        var ultimoParto: Date? = bovino.servicios!![0].parto?.fecha
                                        for (servicio in bovino.servicios!!) {
                                            if (servicio.fecha!!.time >= fecha!!.time) ultimoServicio = servicio
                                            if (servicio.parto != null) {
                                                if (ultimoParto == null) {
                                                    ultimoParto = servicio.parto!!.fecha
                                                } else {
                                                    if (servicio.parto!!.fecha!! > ultimoParto) ultimoParto = servicio.parto!!.fecha
                                                }
                                            }
                                        }
                                        val today = Date()
                                        val dif = if (ultimoParto != null) today.time - ultimoParto.time else today.time - ultimoServicio.fecha!!.time
                                        val diasVacios = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                                        //ReporteTresServicios(bovino.codigo!!, bovino.nombre, ultimoServicio.fecha!!, diasVacios)
                                        listOf(bovino.codigo!!, bovino.nombre!!, ultimoServicio.fecha!!.toStringFormat(), diasVacios.toString())
                                    }
                        }
                        .toList().applySchedulers()
        //endregion

        //region reporte celos
        fun reporteCelos(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID
                        andEx (ArrayFunction.length(Expression.property("celos")).greaterThanOrEqualTo(Expression.value(1)))
                        , Bovino::class)
                        .flatMapObservable {
                            it.toObservable()
                        }
                        .filter {

                            val fechacelos = it.celos!![0]
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = fechacelos.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            month == mes && year == anio
                        }
                        .map { bovino ->
                            // ReporteCelos(bovino.codigo!!, bovino.nombre, bovino.celos!![0])
                            listOf(bovino.codigo!!, bovino.nombre!!, bovino.celos!![0].toStringFormat())
                        }

                        .toList().applySchedulers()


        fun reporteCelos(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID

                        andEx (ArrayFunction.length(Expression.property("celos")).greaterThanOrEqualTo(Expression.value(1)))
                        , Bovino::class)
                        .flatMapObservable {
                            it.toObservable()
                        }
                        .filter {

                            val fechacelos = it.celos!![0]
                            from.time < fechacelos.time && fechacelos.time < to.time
                        }
                        .map { bovino ->
                            // ReporteCelos(bovino.codigo!!, bovino.nombre, bovino.celos!![0])
                            listOf(bovino.codigo!!, bovino.nombre!!, bovino.celos!![0].toStringFormat())
                        }

                        .toList().applySchedulers()

        //endregion

        //region Reporte vacas
        fun reporteVacas(): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val cal = Calendar.getInstance()
                            val dias = cal.get(Calendar.MONTH) + 20
                            dias <= it.fechaNacimiento!!.time && 1 <= it.partos!!
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!)
                        }.toList().applySchedulers()

        //endregion


        fun reporteSalida(from: Date, to: Date): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID andEx ("fechaSalida").betweenDates(from, to), Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaSalida!!.toStringFormat(), it.motivoSalida!!)
                        }.toList().applySchedulers()


        fun reporteSalida(mes: Int, anio: Int): Single<List<List<String>>> =
                db.listByExp("finca" equalEx farmID, Bovino::class)
                        .flatMapObservable { it.toObservable() }
                        .filter { it.fechaSalida != null }
                        .filter {
                            val fechaSalida = it.fechaSalida
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = fechaSalida!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            month == mes && year == anio
                        }
                        .map {
                            listOf(it.codigo!!, it.nombre!!, it.fechaSalida!!.toStringFormat(), it.motivoSalida!!)
                        }.toList().applySchedulers()


        fun reporteMovimientos(from: Date, to: Date): Single<MutableList<List<String?>>> =
                db.listByExp("idFarm" equalEx farmID andEx ("transactionDate".betweenDates(from, to)), Movimiento::class)
                        .flatMapObservable { it.toObservable() }
                        .flatMap {
                            // Single.just(it)
                            it.bovinos!!.toObservable()
                                    .flatMapMaybe { id ->
                                        db.oneById(id, Bovino::class)
                                    }.map { bov ->
                                        listOf(bov.codigo, it.idPradera!!, it.transactionDate!!.toStringFormat())
                                    }
                        }.toList().applySchedulers()


        fun reporteMovimientos(mes: Int, anio: Int): Single<MutableList<List<String?>>> =
                db.listByExp("idFarm" equalEx farmID, Movimiento::class)
                        .flatMapObservable { it.toObservable() }
                        .filter {
                            val fechaMovimiento = it.transactionDate
                            val cal = Calendar.getInstance()
                            cal.timeInMillis = fechaMovimiento!!.time
                            val month = cal.get(Calendar.MONTH)
                            val year = cal.get(Calendar.YEAR)
                            month == mes && year == anio
                        }
                        .flatMap {
                            // Single.just(it)
                            it.bovinos!!.toObservable()
                                    .flatMapMaybe { id ->
                                        db.oneById(id, Bovino::class)
                                    }.map { bov ->
                                        listOf(bov.codigo, it.idPradera!!, it.transactionDate!!.toStringFormat())
                                    }

                        }.toList().applySchedulers()

        //endregion

        fun processDates(from: Date?, to: Date?, month: Int?, year: Int?): Pair<Date?, Date?> = when {
            month != null -> {
                val calendar: Calendar = Calendar.getInstance()
                val fromDate: Date = calendar.apply { set(year!!, month, 1,0,0,0) }.time
                val m = if (month < 12) month + 1 else 1
                val a = if (m == 1) year!! + 1 else year!!
                val toDate: Date = calendar.apply {
                    set(a, m, 1)
                    add(Calendar.DATE, -1)
                }.time
                fromDate to toDate
            }
            from != null -> from to to
            else -> Pair<Date?, Date?>(null, null)
        }

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
                        it.map { x -> (x.peso ?: 0).toFloat() / (if(bovino != null) x.bovinos?.size ?: 1 else 1 ) }
                                .to(MathObservable::averageFloat)
                                .map { x -> x.roundToInt() }
                                .zipWith(it.map { x -> (x.valorTotal ?: 0).toFloat() / (if(bovino != null) x.bovinos?.size ?: 1 else 1 ) }
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
                    .groupBy { "${it.bovino}_${format.format(it.fecha)}" }
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
                    anio =  anio
            )
        }

        fun getPromedioIntervaloPartos(from:Date? = null, to:Date?=null, mes:Int? = null, anio:Int? = null) =
                promedioIntervaloPartos(from, to, mes, anio).map {
            Promedio("Intervalo partos", it, unidades = "Días",
                    desde = from,
                    hasta = to,
                    mes = mes,
                    anio = anio
                    )
        }

    fun promedioIntervaloPartosTotalYBovino(bovino: String, from:Date? = null, to:Date?=null, mes:Int? = null, anio:Int? = null) =
            promedioIntervaloPartos(from, to, mes, anio).zipWith(intervaloPartosBovino(bovino,from, to, mes ,anio))
            .map {
                Promedio("Intervalo entre Partos", it.first, bovino, it.second, unidades = "Días",
                        desde = from,
                        hasta = to,
                        mes = mes,
                        anio = anio)
            }


        fun promedioDiasVaciosTotalYBovino(bovino: String, from: Date? = null, to: Date? = null, mes: Int? = null, anio: Int? = null) =
                promedioDiasVacios(from, to, mes, anio)
                        .zipWith(diasVaciosBovino(bovino, from , to, mes ,anio))
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