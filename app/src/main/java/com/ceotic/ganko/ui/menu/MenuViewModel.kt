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
                null, null, arrayOf(order))
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
                db.listObsByExp(exp, RegistroManejo::class)
            }
            .applySchedulers()

    fun updateManage(registroManejo: RegistroManejo): Single<Unit> = db.update(registroManejo._id!!, registroManejo).applySchedulers()

    fun getNextManages(from: Date, to: Date): Observable<List<RegistroManejo>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroManejo::class).applySchedulers()

    fun getPendingManages(from: Date): Observable<List<RegistroManejo>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".lte(from)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroManejo::class).applySchedulers()

    fun getManagesByDosisUno(idDosisUno: String): Single<List<RegistroManejo>> =
            db.listByExp("idFinca" equalEx farmID andEx ("idAplicacionUno" equalEx idDosisUno), RegistroManejo::class).applySchedulers()

    //endregion

    fun getNotifications(from: Date, to: Date): Single<List<Alarm>> =
            db.listByExpNoType("idFinca" equalEx farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx NOT_APPLIED), Alarm::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    //region ReportesReproductivo
    private val VAR_SERV = ArrayExpression.variable("servicio")
    private val VAR_NOVEDAD = ArrayExpression.variable("servicio.novedad.novedad")
    private val VAR_PARTO = ArrayExpression.variable("servicio.parto")
    private val VAR_EMPADRE = ArrayExpression.variable("servicio.empadre")
    private val VAR_FECHA_PARTO = ArrayExpression.variable("servicio.parto.fecha")


    fun totalServicios(): Single<Promedio> =
            db.listByExp("finca" equalEx farmID
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()
                    }
                    .count().map {
                        Promedio("Total Servicios", it)
                    }.applySchedulers()

    fun totalServiciosEfectivos(): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter {
                            it.diagnostico?.confirmacion ?: false
                        }
                    }
                    .count().map {
                        Promedio("Total Servicios Efectivos", it)
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

    fun promedioIntervaloPartos(): Maybe<Float> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(2)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_PARTO.notNullOrMissing())
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable().flatMapMaybe { bovino ->
                            bovino.servicios?.toObservable()?.filter { it.parto != null && it.parto?.intervalo != 0 && it.parto?.intervalo != null }?.firstElement()?.map {
                                it.parto!!.intervalo
                            }
                        }
                    }.toList()
                    .flatMapMaybe {
                        val tot = it.size
                        it.toObservable().reduce { t1: Int, t2: Int -> t2 + t1 }.map {
                            it.toFloat() / tot.toFloat()
                        }
                    }.defaultIfEmpty(0f).applySchedulers()

    fun intervaloPartosBovino(idBovino: String): Maybe<Int> =
            db.oneById(idBovino, Bovino::class)
                    .flatMap { bovino ->
                        bovino.servicios?.toObservable()?.filter { it.parto != null }?.firstElement()
                                ?.map {
                                    it.parto!!.intervalo
                                }
                    }.defaultIfEmpty(0).applySchedulers()

    fun promedioDiasVacios(): Maybe<Float> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_PARTO.notNullOrMissing())
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .flatMapMaybe { bovino ->
                        bovino.servicios?.toObservable()?.filter { it.parto != null }?.firstElement()
                                ?.map {
                                    val ultimoServicio = bovino.servicios!![0]
                                    val ultimoParto = it
                                    val dif = if (ultimoServicio.diagnostico?.confirmacion!! && ultimoServicio.parto == null) ultimoServicio.fecha!!.time - ultimoParto.fecha!!.time else Date().time - ultimoParto.fecha!!.time
                                    return@map TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                                }
                    }.toList()
                    .flatMapMaybe {
                        val tot = it.size
                        it.toObservable().reduce { t1: Long, t2: Long -> t2 + t1 }.map {
                            it.toFloat() / tot.toFloat()
                        }
                    }.defaultIfEmpty(0f).applySchedulers()

    fun diasVaciosBovino(idBovino: String): Maybe<Long> =
            db.oneById(idBovino, Bovino::class).flatMap { bovino ->
                bovino.servicios?.toObservable()?.filter { it.parto != null }?.firstElement()
                        ?.map {
                            val ultimoServicio = bovino.servicios!![0]
                            val ultimoParto = it
                            val dif = if (ultimoServicio.diagnostico?.confirmacion!! && ultimoServicio.parto == null) ultimoServicio.fecha!!.time - ultimoParto.fecha!!.time else Date().time - ultimoParto.fecha!!.time
                            return@map TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                        }
            }.defaultIfEmpty(0)
                    .applySchedulers()

    fun totalAbortos(): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_NOVEDAD.equalTo(Expression.string("Aborto")))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter { it.novedad?.novedad == "Aborto" }
                    }.count()
                    .map {
                        Promedio("Total Abortos", it)
                    }.applySchedulers()

    fun totalPartos(): Single<Promedio> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_PARTO.notNullOrMissing())
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()
                    }.flatMap {
                        it.servicios?.toObservable()?.filter { it.parto != null }
                    }.count()
                    .map {
                        Promedio("Total Partos", it)
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
                                        cal.timeInMillis = fechaParto.time
                                        val month = cal.get(Calendar.MONTH)
                                        val year = cal.get(Calendar.YEAR)
                                        month == mes && year == anio
                                    } else {
                                        false
                                    }
                                    serv.finalizado == true && mesCorrecto
                                }
                    }.count().map {
                        Promedio("Partos por mes", it, mes = mes, anio = anio)
                    }.applySchedulers()

    //region reportes

    //region reporte Alimentacion
    fun reporteAlimentacion(from: Date, to: Date): Single<List<List<String>>> =
            db.listByExp("idFinca" equalEx farmID andEx ("fecha".betweenDates(from, to)), RegistroAlimentacion::class)
                    .flatMapObservable { it.toObservable() }
                    .map {
                        var stringBovines = ""
                        for (bovino in it.bovinos!!) {
                            stringBovines += " $bovino"
                        }
                        listOf(stringBovines, it.valorkg!!.toString(), it.tipoAlimento!!, it.valorTotal!!.toString())
                    }.toList().applySchedulers()


    fun reporteAlimentacion(mes: Int, anio: Int): Single<List<List<String>>> =
            db.listByExp("idFinca" equalEx farmID, RegistroAlimentacion::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = it.fecha!!.time
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        month == mes && year == anio
                    }
                    .map {
                        var stringBovines = ""
                        for (bovino in it.bovinos!!) {
                            stringBovines += " $bovino"
                        }
                        listOf(stringBovines, it.tipoAlimento!!, it.peso!!.toString(), it.valorTotal!!.toString())
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

    fun reportesLeche(mes: Int, anio: Int): Single<List<List<String>>> =
            db.listByExp("idFinca" equalEx farmID, Produccion::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = it.fecha!!.time
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        month == mes && year == anio
                    }.map {
                        listOf(it.bovino!!, it.litros!!.toString(), it.fecha!!.toStringFormat(), it.jornada!!)
                    }.toList().applySchedulers()


    fun reportesLeche(from: Date, to: Date): Single<List<List<String>>> =
            db.listByExp("idFinca" equalEx farmID andEx ("fecha".betweenDates(from, to)), Produccion::class)
                    .flatMapObservable { it.toObservable() }
                    .map {
                        listOf(it.bovino!!, it.litros!!.toString(), it.fecha!!.toStringFormat(), it.jornada!!)
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
            db.listByExp("idFinca" equalEx farmID andEx ("available" equalEx false), Pradera::class)
                    .flatMapObservable { it.toObservable() }
                    .map {
                        var ultimo = it.mantenimiento!!.last()
                        listOf(it.identificador!!.toString(), it.tipoGraminea!!, ultimo.fechaMantenimiento!!.toStringFormat(), it.fechaOcupacion!!.toStringFormat())
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
    fun reporteGananciaPeso(bovino: Bovino, from: Date, to: Date): Single<List<List<String>>> =
            db.listByExp("fecha".betweenDates(from, to) andEx ("bovino" equalEx bovino.codigo!!), Ceba::class)
                    .flatMapObservable { it.toObservable() }
                    .map { listOf(it.bovino!!, bovino.nombre!!, bovino.fechaNacimiento!!.toStringFormat(), it.gananciaPeso!!.toString(), bovino.proposito!!) }.toList().applySchedulers()

   /* fun reporteGananciaPeso(bovino: Bovino, mes: Int, anio: Int): Observable<Pair<Bovino,gdp:Float>> =
            db.listByExp(("bovino" equalEx bovino.codigo!!), orderBy = arrayOf("fecha" orderEx DESCENDING), kClass = Ceba::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = it.fecha!!.time
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        month == mes && year == anio
                    }.toList()
                    .flatMap {
                        var difdays = ((it[it.size - 1].fecha.time - it[0].fecha.time) / (1000 * 60 * 60 * 24))
                        var gdp = (it[it.size - 1].gananciaPeso - it[0].gananciaPeso)
                        bovino to gdp
                    }.applySchedulers()
*/
    //endregion

    //region Entradas

    //region Reporte Inventario
    fun reporteInventario(from: Date, to: Date): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID andEx ("fechaNacimiento".betweenDates(from, to)), Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .map {
                        var partos: String=""
                        if (it.partos == null) partos = "0" else partos = it.partos.toString()
                        listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.color!!, it.raza!!, partos, it.codigoMadre ?: "", it.codigoPadre ?:"")
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
                        val partos: String=""
                        if (it.partos == null) '0' else it.partos.toString()
                        listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.color!!, it.raza!!, partos,it.codigoMadre ?: "", it.codigoPadre ?:"")
                    }.toList().applySchedulers()

    //endregion

    //region Reporte Terneras en estaca
    fun reporteTernerasEnEstaca(from: Date, to: Date): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID andEx ("fechaNacimiento".betweenDates(from, to) andEx ("genero" equalEx "Hembra")), Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val cal = Calendar.getInstance()
                        val meses = cal.get(Calendar.MONTH) + 5
                        val dias = cal.get(Calendar.DAY_OF_YEAR) + 3
                        dias <= it.fechaNacimiento!!.time && meses <= it.fechaNacimiento!!.time
                    }
                    .map {
                        listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!,it.codigoMadre ?: "", it.codigoPadre ?:"")
                    }.toList().applySchedulers()


    fun reporteTernerasEnEstaca(mes: Int, anio: Int): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = it.fechaNacimiento!!.time
                        val meses = cal.get(Calendar.MONTH) + 5
                        val dias = cal.get(Calendar.DAY_OF_YEAR) + 3
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        it.fechaNacimiento!!.time in dias..meses && month == mes && anio == year
                    }
                    .map {
                        listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre ?: "", it.codigoPadre ?:"")
                    }.toList().applySchedulers()


    //endregion

    //region terneras Destetas

    fun reporteTernerasDestetas(from: Date, to: Date): Single<List<List<String>>> =
            db.listByExp("finca"  equalEx farmID andEx ("destete" equalEx true) andEx ("fechaNacimiento".betweenDates(from, to) andEx ("genero" equalEx "Hembra")), Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val cal = Calendar.getInstance()
                        val meses = cal.get(Calendar.MONTH) + 12
                        val dias = cal.get(Calendar.MONTH) + 6
                        dias <= it.fechaNacimiento!!.time && meses <= it.fechaNacimiento!!.time
                    }
                    .map {
                        listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre ?: "", it.codigoPadre ?:"")
                    }.toList().applySchedulers()


    fun reporteTernerasDestetas(mes: Int, anio: Int): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = it.fechaNacimiento!!.time
                        val meses = cal.get(Calendar.MONTH) + 12
                        val dias = cal.get(Calendar.MONTH) + 6
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        it.fechaNacimiento!!.time in dias..meses && month == mes && anio == year
                    }
                    .map {
                        listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre ?: "", it.codigoPadre ?:"")
                    }.toList().applySchedulers()

    //endregion

    //region novillas levante
    fun reporteTerneraslevante(from: Date, to: Date): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID andEx ("fechaNacimiento".betweenDates(from, to) andEx ("genero" equalEx "Hembra")), Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val cal = Calendar.getInstance()
                        val meses = cal.get(Calendar.MONTH) + 18
                        val dias = cal.get(Calendar.MONTH) + 12
                        dias <= it.fechaNacimiento!!.time && meses <= it.fechaNacimiento!!.time
                    }
                    .map {
                        listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre ?: "", it.codigoPadre ?:"")
                    }.toList().applySchedulers()


    fun reporteTerneraslevante(mes: Int, anio: Int): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = it.fechaNacimiento!!.time
                        val meses = cal.get(Calendar.MONTH) + 18
                        val dias = cal.get(Calendar.MONTH) + 12
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        it.fechaNacimiento!!.time in dias..meses && month == mes && anio == year
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
                        val cal = Calendar.getInstance()
                        val meses = cal.get(Calendar.MONTH) + 20
                        val dias = cal.get(Calendar.MONTH) + 18
                        dias <= it.fechaNacimiento!!.time && meses <= it.fechaNacimiento!!.time
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
                        val meses = cal.get(Calendar.MONTH) + 20
                        val dias = cal.get(Calendar.MONTH) + 18
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        it.fechaNacimiento!!.time in dias..meses && month == mes && anio == year
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
                    listOf(it.codigo!!, it.nombre!! ,serv.posFechaParto!!.toStringFormat(),serv.fecha!!.toStringFormat(), serv.posFechaParto!!.add(Calendar.DATE, -60)!!.toStringFormat())
                }.toList().applySchedulers()
    }

    //endregion

    //region reportes preparacion
    fun reportePreparacion(mes: Int, anio: Int)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         : Single<List<List<String>>> =
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
                        listOf(it.codigo!!, it.nombre!!,serv.posFechaParto!!.toStringFormat(), serv.fecha!!.toStringFormat(), serv.posFechaParto!!.add(Calendar.DATE, -30)!!.toStringFormat())
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
                    listOf(it.codigo!!, it.nombre!!, serv.fecha!!.toStringFormat(), serv.posFechaParto!!.add(Calendar.DATE, -30)!!.toStringFormat())
                }.toList().applySchedulers()
    }

    //endregion

    //region reporte dias vacios
    fun reporteDiasVacios(): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_PARTO.notNullOrMissing())
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .flatMapMaybe { bovino ->
                        bovino.servicios?.toObservable()?.filter { it.parto != null }?.firstElement()
                                ?.map {
                                    val ultimoServicio = bovino.servicios!![0]
                                    val ultimoParto = it
                                    val dif = if (ultimoServicio.diagnostico?.confirmacion!! && ultimoServicio.parto == null) ultimoServicio.fecha!!.time - ultimoParto.parto!!.fecha.time else Date().time - ultimoParto.parto!!.fecha.time
                                    val enServicio = ultimoServicio.finalizado?.not() ?: false
                                    val diasVacios = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                                    var enSer = if(enServicio) "Si" else "No"
                                    //ReporteDiasVacios(bovino.codigo!!, bovino.nombre, ultimoParto.fecha!!, ultimoServicio.fecha!!, diasVacios, enServicio)
                                    listOf(bovino.codigo!!, bovino.nombre!!, ultimoServicio.parto!!.fecha.toStringFormat(), ultimoServicio.fecha!!.toStringFormat(), diasVacios.toString(), enSer)
                                }
                    }.toList().applySchedulers()
    //endregion

    //region reporte pajillas

    fun reportePajillas(from:Date,to:Date): Single<List<List<String>>> =
            db.listByExp("idFarm" equalEx farmID andEx ("fecha".betweenDates(from, to)), Straw::class)
                    .flatMapObservable { it.toObservable()}
                    .map {
                        listOf(it.idStraw!!,it.layette!!,it.breed!!,it.purpose!!,it.bull!!,it.origin!!)
                    }.toList().applySchedulers()


    fun reportePajillas(mes:Int, anio:Int): Single<List<List<String>>> =
            db.listByExp("idFarm" equalEx farmID, Straw::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val fecha = it.fecha!!
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = fecha.time
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        month == mes && year == anio}
                    .map {
                        listOf(it.idStraw!!,it.layette!!,it.breed!!,it.purpose!!,it.bull!!,it.origin!!)
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
                                    listOf(bovino.codigo!!, bovino.nombre!!, parto.fecha.toStringFormat(), parto.sexoCria, parto.estadoCria)
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
                                        cal.timeInMillis = fechaParto.time
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
                                    listOf(bovino.codigo!!, bovino.nombre!!, parto.fecha.toStringFormat(), parto.sexoCria, parto.estadoCria)
                                }
                    }.toList().applySchedulers()
    //endregion

    //region reporte abortos
    fun reporteAbortos(from: Date, to: Date): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_NOVEDAD.equalTo(Expression.string("Aborto")))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_NOVEDAD.between(Expression.date(from), Expression.date(to)))
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .flatMap { bovino ->
                        bovino.servicios!!.toObservable()
                                .filter { it.novedad?.novedad == "Aborto" /* && it.novedad?.fecha?.after(from) ?: false && it.novedad?.fecha?.before(to) ?: false*/ }
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
                                    val ultimoServicio = bovino.servicios!![0]
                                    val today = Date()
                                    val ultimoParto = bovino.servicios!!.find { it.parto != null }
                                    val dif = if (ultimoParto != null) today.time - ultimoParto.fecha!!.time else 0
                                    val diasVacios = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                                    //ReporteTresServicios(bovino.codigo!!, bovino.nombre, ultimoServicio.fecha!!, diasVacios)
                                    listOf(bovino.codigo!!, bovino.nombre!!, ultimoServicio.fecha!!.toStringFormat(), diasVacios.toString())
                                }
                    }
                    .toList().applySchedulers()
    //endregion

    //region reporte celos
    fun reporteCelos(mes:Int, anio:Int): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID
                    andEx (ArrayFunction.length(Expression.property("celos")).greaterThanOrEqualTo(Expression.value(1)))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()}
                    .filter{

                        val fechacelos= it.celos!![0]
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = fechacelos.time
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                         month == mes && year == anio}
                                .map { bovino ->
                                    // ReporteCelos(bovino.codigo!!, bovino.nombre, bovino.celos!![0])
                                    listOf(bovino.codigo!!, bovino.nombre!!, bovino.celos!![0].toStringFormat())
                                }

                    .toList().applySchedulers()


    fun reporteCelos(from:Date, to:Date): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID

                    andEx (ArrayFunction.length(Expression.property("celos")).greaterThanOrEqualTo(Expression.value(1)))
                    , Bovino::class)
                    .flatMapObservable {
                        it.toObservable()}
                    .filter{

                        val fechacelos= it.celos!![0]
                        fechacelos.after(from) && fechacelos.before(to) }
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
                        listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre?:"", it.codigoPadre?:"")
                    }.toList().applySchedulers()

    //endregion


    fun reporteSalida(from:Date, to:Date):Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID andEx ("fechaSalida").betweenDates(from,to), Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .map {
                        listOf(it.codigo!!, it.nombre!!, it.fechaSalida!!.toStringFormat(),it.motivoSalida!!)
                    }.toList().applySchedulers()


    fun reporteSalida(mes:Int, anio:Int): Single<List<List<String>>> =
            db.listByExp("finca" equalEx farmID , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter{
                        val fechaSalida= it.fechaSalida
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = fechaSalida!!.time
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        month == mes && year == anio}
                    .map{
                            listOf(it.codigo!!, it.nombre!!, it.fechaSalida!!.toStringFormat(), it.motivoSalida!!)
                        }.toList().applySchedulers()


    fun reporteMovimientos(from:Date,to:Date): Single<List<List<String>>> =
        db.listByExp("idFarm" equalEx farmID andEx ("transactionDate".betweenDates(from,to)), Movimiento::class)
                .flatMapObservable { it.toObservable() }
                .map {
                   // Single.just(it)
                    var bovinos:String= ""
                     for (bovino in it.bovinos){ bovinos+= bovino
                     }
                    listOf(bovinos, it.idPradera!!, it.transactionDate!!.toStringFormat())
                }.toList().applySchedulers()


    fun reporteMovimientos(mes:Int,anio:Int): Single<List<List<String>>> =
            db.listByExp("idFarm" equalEx farmID , Movimiento::class)
                    .flatMapObservable { it.toObservable() }
                    .filter{
                        val fechaMovimiento= it.transactionDate
                        val cal = Calendar.getInstance()
                        cal.timeInMillis = fechaMovimiento!!.time
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        month == mes && year == anio
                    }
                    .map {
                        // Single.just(it)
                        var bovinos:String= ""
                        for (bovino in it.bovinos){ bovinos+= bovino
                        }
                        listOf(bovinos, it.idPradera!!, it.transactionDate!!.toStringFormat())
                    }.toList().applySchedulers()

    //endregion


    fun promedioGananciaPeso(from: Date, to: Date) =
            db.groupedListByExp("fecha".betweenDates(from, to) andEx ("finca" equalEx farmID), Ceba::class, Expression.property("bovino"))
                    .flatMapObservable {
                        it.toObservable().map { ceba ->
                            Log.d("CEBA", ceba.toString())
                            ceba.gananciaPeso ?: 0f
                        }
                    }
                    .toList()
                    .flatMapMaybe { lista ->
                        val tot = lista.size
                        lista.toObservable().reduce { t1: Float, t2: Float -> t1 + t2 }
                                .map {
                                    it / tot.toFloat()
                                }
                    }.defaultIfEmpty(0f)
                    .applySchedulers()

    fun promedioGananciaPesoBovino(bovino: String, from: Date, to: Date) =
            db.groupedListByExp("fecha".betweenDates(from, to) andEx ("finca" equalEx farmID) andEx ("bovino" equalEx bovino), Ceba::class, Expression.property("bovino"))
                    .flatMapObservable {
                        it.toObservable().map { ceba ->
                            Log.d("CEBA", ceba.toString())
                            ceba.gananciaPeso ?: 0f
                        }
                    }
                    .toList()
                    .flatMapMaybe { lista ->
                        val tot = lista.size
                        lista.toObservable().reduce { t1: Float, t2: Float -> t1 + t2 }
                                .map {
                                    it / tot.toFloat()
                                }
                    }.defaultIfEmpty(0f)
                    .applySchedulers()

    fun promedioGananciaPeso(mes: Int, anio: Int): Maybe<Float> {
        val calendar: Calendar = Calendar.getInstance()
        val from: Date = calendar.apply { set(anio, mes, 1) }.time
        val to: Date = calendar.apply { set(anio, mes + 1, 1) }.time
        return db.groupedListByExp("fecha".betweenDates(from, to) andEx ("finca" equalEx farmID), Ceba::class, Expression.property("bovino"))
                .flatMapObservable { it.toObservable() }
                .map { ceba ->
                    Log.d("CEBA", ceba.toString())
                    ceba.gananciaPeso ?: 0f
                }.toList().flatMapMaybe { lista ->
                    val tot = lista.size
                    lista.toObservable().reduce { t1: Float, t2: Float -> t1 + t2 }
                            .map {
                                it / tot.toFloat()
                            }
                }.defaultIfEmpty(0f).applySchedulers()
    }


    fun promedioGananciaPesoBovino(bovino: String, mes: Int, anio: Int): Maybe<Float> {
        val calendar: Calendar = Calendar.getInstance()
        val from: Date = calendar.apply { set(anio, mes, 1) }.time
        val to: Date = calendar.apply { set(anio, mes + 1, 1) }.time
        return db.groupedListByExp("fecha".betweenDates(from, to) andEx ("finca" equalEx farmID) andEx ("bovino" equalEx bovino), Ceba::class, Expression.property("bovino"))
                .flatMapObservable { it.toObservable() }
                .map { ceba ->
                    Log.d("CEBA", ceba.toString())
                    ceba.gananciaPeso ?: 0f
                }.toList().flatMapMaybe { lista ->
                    val tot = lista.size
                    lista.toObservable().reduce { t1: Float, t2: Float -> t1 + t2 }
                            .map {
                                it / tot.toFloat()
                            }
                }.defaultIfEmpty(0f).applySchedulers()
    }

    fun promedioLeche(from: Date, to: Date): Maybe<Int> =
            db.listByExp("idFinca" equalEx farmID!! andEx ("fecha".betweenDates(from, to)), Produccion::class)
                    .flatMapObservable {
                        it.toObservable().map {
                            it.litros!!.toInt()
                        }
                    }
                    .toList()
                    .flatMapMaybe {
                        val tot = it.size
                        it.toObservable().reduce { t1: Int, t2: Int -> t1 + t2 }.map { sum ->
                            sum / tot
                        }
                    }.defaultIfEmpty(0).applySchedulers()

    fun promedioLecheBovino(bovino: String, from: Date, to: Date): Maybe<Int> =
            db.listByExp("idFinca" equalEx farmID!! andEx ("fecha".betweenDates(from, to)) andEx ("bovino" equalEx bovino), Produccion::class)
                    .flatMapObservable {
                        it.toObservable().map {
                            it.litros!!.toInt()
                        }
                    }
                    .toList()
                    .flatMapMaybe {
                        val tot = it.size
                        it.toObservable().reduce { t1: Int, t2: Int -> t1 + t2 }.map { sum ->
                            sum / tot
                        }
                    }.defaultIfEmpty(0).applySchedulers()

    fun promedioLeche(mes: Int, anio: Int): Maybe<Float> {
        val calendar: Calendar = Calendar.getInstance()
        val from: Date = calendar.apply { set(anio, mes, 1) }.time
        val to: Date = calendar.apply { set(anio, mes + 1, 1) }.time
        return db.listByExp("idFinca" equalEx farmID!! andEx ("fecha".betweenDates(from, to)), Produccion::class)
                .flatMapObservable {
                    it.toObservable().map {
                        it.litros!!.toInt()
                    }
                }
                .toList()
                .flatMapMaybe {
                    val tot = it.size
                    it.toObservable().reduce { t1: Int, t2: Int -> t1 + t2 }.map { sum ->
                        sum.toFloat() / tot.toFloat()
                    }
                }.defaultIfEmpty(0f).applySchedulers()
    }

    fun promedioLecheBovino(bovino: String, mes: Int, anio: Int): Maybe<Float> {
        val calendar: Calendar = Calendar.getInstance()
        val from: Date = calendar.apply { set(anio, mes, 1) }.time
        val to: Date = calendar.apply { set(anio, mes + 1, 1) }.time
        return db.listByExp("idFinca" equalEx farmID!! andEx ("fecha".betweenDates(from, to)) andEx ("bovino" equalEx bovino), Produccion::class)
                .flatMapObservable {
                    it.toObservable().map {
                        it.litros!!.toInt()
                    }
                }
                .toList()
                .flatMapMaybe {
                    val tot = it.size
                    it.toObservable().reduce { t1: Int, t2: Int -> t1 + t2 }.map { sum ->
                        sum.toFloat() / tot.toFloat()
                    }
                }.defaultIfEmpty(0f).applySchedulers()
    }

    fun getPromedioLeche(mes: Int, anio: Int) = promedioLeche(mes, anio).map {
        Promedio("Producción de Leche", it, mes = mes, anio = anio)
    }

    fun getPromedioLeche(from: Date, to: Date) = promedioLeche(from, to).map {
        Promedio("Producción de Leche", it, desde = from, hasta = to)
    }

    fun getPromedioGDP(mes: Int, anio: Int) = promedioGananciaPeso(mes, anio).map {
        Promedio("Ganancia de Peso", it, mes = mes, anio = anio)
    }

    fun getPromedioGDP(from: Date, to: Date) = promedioGananciaPeso(from, to).map {
        Promedio("Ganancia de Peso", it, desde = from, hasta = to)
    }

    fun getPromedioDiasVacios() = promedioDiasVacios().map {
        Promedio("Dias Vacios", it)
    }

    fun getPromedioIntervaloPartos() = promedioIntervaloPartos().map {
        Promedio("Intervalo partos", it)
    }

    fun promedioLecheTotalYBovino(bovino: String, mes: Int, anio: Int) = promedioLeche(mes, anio).zipWith(promedioLecheBovino(bovino, mes, anio))
            .map {
                Promedio("Producción de Leche", it.first, bovino, it.second, mes = mes, anio = anio)
            }

    fun promedioLecheTotalYBovino(bovino: String, from: Date, to: Date) = promedioLeche(from, to).zipWith(promedioLecheBovino(bovino, from, to))
            .map {
                Promedio("Producción de Leche", it.first, bovino, it.second, desde = from, hasta = to)
            }

    fun promedioGananciaPesoTotalYBovino(bovino: String, mes: Int, anio: Int) = promedioGananciaPeso(mes, anio).zipWith(promedioGananciaPesoBovino(bovino, mes, anio))
            .map {
                Promedio("Ganancia de peso", it.first, bovino, it.second, mes = mes, anio = anio)
            }

    fun promedioGananciaPesoTotalYBovino(bovino: String, from: Date, to: Date) = promedioGananciaPeso(from, to).zipWith(promedioGananciaPesoBovino(bovino, from, to))
            .map {
                Promedio("Ganancia de peso", it.first, bovino, it.second, desde = from, hasta = to)
            }

    fun promedioDiasVaciosTotalYBovino(bovino: String) = promedioDiasVacios().zipWith(diasVaciosBovino(bovino))
            .map {
                Promedio("Días Vacios", it.first, bovino, it.second)
            }

    fun promedioIntervaloPartosTotalYBovino(bovino: String) = promedioIntervaloPartos().zipWith(intervaloPartosBovino(bovino))
            .map {
                Promedio("Intervalo entre Partos", it.first, bovino, it.second)
            }


    fun getAllCows(): Single<List<Bovino>> = db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
            .applySchedulers()

    fun getBovineById(idBovino: String) = db.oneById(idBovino,Bovino::class).applySchedulers()



    fun validatePlan():Boolean = userSession.validatePlanDate().first

}