package com.example.cristian.myapplication.ui.menu

import android.arch.lifecycle.ViewModel
import android.graphics.Color
import com.couchbase.lite.ArrayExpression
import com.couchbase.lite.ArrayFunction
import com.couchbase.lite.Expression
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.db.CouchRx
import com.example.cristian.myapplication.data.models.*
import com.example.cristian.myapplication.data.models.ProxStates.Companion.NOT_APPLIED
import com.example.cristian.myapplication.data.preferences.UserSession
import com.example.cristian.myapplication.util.*
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.subjects.PublishSubject
import java.time.Month
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


    fun getNextHealth(from: Date, to: Date): Observable<List<Sanidad>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx ProxStates.NOT_APPLIED), Sanidad::class)
                    .applySchedulers()

    fun getPendingHealth(from: Date): Observable<List<Sanidad>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima" lt from) andEx ("estadoProximo" equalEx ProxStates.NOT_APPLIED), Sanidad::class)
                    .applySchedulers()

    fun updateHealth(id: String, sanidad: Sanidad): Single<Unit> = db.update(sanidad._id!!, sanidad).applySchedulers()

    fun getMilk(idFinca: String): Single<List<SalidaLeche>> =
            db.listByExp("idFarm" equalEx idFinca, SalidaLeche::class)
                    .applySchedulers()

    fun getFeed(): Observable<List<RegistroAlimentacion>> = db.listObsByExp("idFinca" equalEx farmID, RegistroAlimentacion::class)
            .applySchedulers()

    fun getMeadows(idFinca: String): Single<Pair<List<Pradera>, Long>> =
            db.listByExp("idFinca" equalEx idFinca, Pradera::class)
                    .map {
                        Single.just(it)
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

    fun updateGroup(group: Group): Single<Unit> =
            db.update(group._id!!, group).applySchedulers()


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

    fun getVaccinations(): Observable<List<RegistroVacuna>> = db.listObsByExp("idFinca" equalEx farmID, RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    fun getNextVaccines(from: Date, to: Date): Observable<List<RegistroVacuna>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    fun getPendingVaccines(from: Date): Observable<List<RegistroVacuna>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".lte(from)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx ASCENDING)).applySchedulers()

    fun getVaccinesByDosisUno(idDosisUno: String): Single<List<RegistroVacuna>> =
            db.listByExp("idFinca" equalEx farmID andEx ("idDosisUno" equalEx idDosisUno), RegistroVacuna::class, orderBy = arrayOf("fecha" orderEx DESCENDING)).applySchedulers()

    //endregion
    fun getHealthApplied(idDosisUno: String): Single<List<Sanidad>> =
            db.listByExp("idFinca" equalEx farmID andEx ("idDosisUno" equalEx idDosisUno), Sanidad::class).applySchedulers()


    //region Manejo
    fun insertManage(registroManejo: RegistroManejo): Single<String> = db.insertDosisUno(registroManejo).applySchedulers()

    fun getManages(): Observable<List<RegistroManejo>> = db.listObsByExp("idFinca" equalEx farmID, RegistroManejo::class).applySchedulers()

    fun updateManage(registroManejo: RegistroManejo): Single<Unit> = db.update(registroManejo._id!!, registroManejo).applySchedulers()

    fun getNextManages(from: Date, to: Date): Observable<List<RegistroManejo>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".betweenDates(from, to)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroManejo::class).applySchedulers()

    fun getPendingManages(from: Date): Observable<List<RegistroManejo>> =
            db.listObsByExp("idFinca" equalEx farmID andEx ("fechaProxima".lte(from)) andEx ("estadoProximo" equalEx NOT_APPLIED), RegistroManejo::class).applySchedulers()

    fun getManagesByDosisUno(idDosisUno: String): Single<List<RegistroManejo>> =
            db.listByExp("idFinca" equalEx farmID andEx ("idDosisUno" equalEx idDosisUno), RegistroManejo::class).applySchedulers()

    //endregion


    //endregion

    //region Reportes
    private val VAR_CONF = ArrayExpression.variable("servicio.diagnostico.confirmacion")
    private val VAR_SERV = ArrayExpression.variable("servicio")
    private val VAR_ABORTO = ArrayExpression.variable("servicio.novedad.novedad")
    private val VAR_FECHAPOSPARTO = ArrayExpression.variable("servicio.posFechaParto")
    private val VAR_FINALIZADO = ArrayExpression.variable("servicio.finalizado")
    private val VAR_PARTO = ArrayExpression.variable("servicio.parto")

    fun reportePartos(mes: Int, anio: Int): Single<List<ReportePartos>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_CONF.equalTo(Expression.booleanValue(true)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_ABORTO.isNullOrMissing)
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
                        ReportePartos(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!)
                    }.toList().applySchedulers()


    fun reportePartos(from: Date, to: Date): Single<List<ReportePartos>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_CONF.equalTo(Expression.booleanValue(true)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_ABORTO.isNullOrMissing)
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_FECHAPOSPARTO.between(Expression.date(from), Expression.date(to)))
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .map {
                        val serv = it.servicios!![0]
                        ReportePartos(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!)
                    }.toList().applySchedulers()


    fun reporteSecado(mes: Int, anio: Int): Single<List<ReporteSecado>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_CONF.equalTo(Expression.booleanValue(true)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_ABORTO.isNullOrMissing)
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
                        ReporteSecado(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!.add(Calendar.DATE, -60)!!)
                    }.toList().applySchedulers()


    fun reporteSecado(from: Date, to: Date): Single<List<ReportePartos>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_CONF.equalTo(Expression.booleanValue(true)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_ABORTO.isNullOrMissing)
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val serv = it.servicios!![0]
                        val fechaPosParto = serv.posFechaParto!!
                        val fechaSecado = fechaPosParto.add(Calendar.DATE, -60)!!
                        !serv.finalizado!! && fechaSecado.after(from) && fechaSecado.before(to)
                    }
                    .map {
                        val serv = it.servicios!![0]
                        ReportePartos(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!.add(Calendar.DATE, -60)!!)
                    }.toList().applySchedulers()

    fun reportePreparacion(mes: Int, anio: Int): Single<List<ReportePreparacion>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_CONF.equalTo(Expression.booleanValue(true)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_ABORTO.isNullOrMissing)
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
                        ReportePreparacion(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!.add(Calendar.DATE, -30)!!)
                    }.toList().applySchedulers()


    fun reportePreparacion(from: Date, to: Date): Single<List<ReportePreparacion>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_CONF.equalTo(Expression.booleanValue(true)))
                    andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                    .satisfies(VAR_ABORTO.isNullOrMissing)
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val serv = it.servicios!![0]
                        val fechaPosParto = serv.posFechaParto!!
                        val fechaPreparacion = fechaPosParto.add(Calendar.DATE, -30)!!
                        !serv.finalizado!! && fechaPreparacion.after(from) && fechaPreparacion.before(to)
                    }
                    .map {
                        val serv = it.servicios!![0]
                        ReportePreparacion(it.codigo!!, it.nombre, serv.fecha!!, serv.posFechaParto!!.add(Calendar.DATE, -30)!!)
                    }.toList().applySchedulers()

    fun reporteDiasVacios(): Single<List<ReporteDiasVacios>> =
            db.listByExp("finca" equalEx farmID andEx ("genero" equalEx "Hembra")
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .flatMapMaybe { bovino ->
                        bovino.servicios?.toObservable()?.filter { it.parto != null }?.firstElement()
                                ?.map {
                                    val ultimoServicio = bovino.servicios!![0]
                                    val ultimoParto = it
                                    val dif = ultimoServicio.fecha!!.time - ultimoParto.fecha!!.time
                                    val diasVacios = TimeUnit.DAYS.convert(dif, TimeUnit.MILLISECONDS)
                                    ReporteDiasVacios(bovino.codigo!!, bovino.nombre, ultimoParto.fecha!!, ultimoServicio.fecha!!, diasVacios)
                                }
                    }.toList().applySchedulers()

    //endregion


}