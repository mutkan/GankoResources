package com.ceotic.ganko.ui.menu.reports

import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.*
import com.couchbase.lite.ArrayExpression
import com.couchbase.lite.ArrayFunction
import com.couchbase.lite.Expression
import hu.akarnokd.rxjava2.math.MathObservable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.zipWith
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class AverageViewModel(private val session: UserSession, private val db: CouchRx) {

    private fun promedioGananciaPeso(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null, idBovino: String? = null): Single<Float> {
        val (ini, end) = processDates(from, to, month, year)
        var exp = "finca" equalEx session.farmID andEx ("gananciaPeso" isNullEx false)
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

    private fun promedioLeche(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null, bovino: String? = null): Maybe<Int> {
        var exp = "idFinca" equalEx session.farmID
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

    private fun promedioAlimentacion(tipoAlimento: String, from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null, bovino: String? = null): Maybe<Pair<Int, Int>> {
        val (ini, end) = processDates(from, to, month, year)
        var exp = "idFinca" equalEx session.farmID andEx ("tipoAlimento" equalEx tipoAlimento)
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

    private fun promedioDiasVacios(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Maybe<Float> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        val currDate = Date()
        val currMilis = currDate.time

        return db.listByExp("finca" equalEx session.farmID andEx ("genero" equalEx "Hembra")
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

    private fun promedioDiasVaciosBovino(idBovino: String, from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Maybe<Float> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        val currDate = Date()
        val currMilis = currDate.time
        return db.oneById(idBovino, Bovino::class)
                .map { it.servicios ?: emptyList() }
                .flatMapSingle { processEmptyDays(it, ini, iniMilis, endMilis, currMilis, currDate) }
                .flatMapObservable { it.toObservable() }
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

    private fun promedioEdad(date: Date) =
            db.listByExp("finca" equalEx session.farmID andEx ("fechaNacimiento" lte date), Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .map {
                        val dif = date.time - it.fechaNacimiento!!.time
                        val months = Math.floor(dif.toDouble() / 2592000000)
                        if(months < 0)  0 else months.toInt()
                    }
                    .to(MathObservable::averageFloat)
                    .first(0f)
                    .toMaybe()
                    .applySchedulers()

    fun totalAbortos(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<Promedio> {

        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        return db.listByExp("finca" equalEx session.farmID andEx ("genero" equalEx "Hembra")
                andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                .satisfies(VAR_NOVEDAD.equalTo(Expression.string("Aborto")))
                , Bovino::class)
                .flatMapObservable {
                    it.toObservable()
                }.flatMap {
                    it.servicios?.toObservable()?.filter { servicio ->
                        servicio.novedad?.novedad == "Aborto" && servicio.novedad?.fecha!!.time >= iniMilis && servicio.novedad?.fecha!!.time <= endMilis
                    }
                }.count()
                .map {
                    Promedio("Abortos", it, desde = from, hasta = to, mes = month, anio = year)
                }.applySchedulers()
    }


    fun totalPartos(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<Promedio> {
        val (ini, end) = processDates(from, to, month, year)

        val iniMilis = ini!!.time
        val endMilis = end!!.time

        return db.listByExp("finca" equalEx session.farmID andEx ("genero" equalEx "Hembra")
                andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                andEx (ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios")))
                .satisfies(VAR_PARTO.notNullOrMissing())
                , Bovino::class)
                .flatMapObservable {
                    it.toObservable()
                }.flatMap {
                    it.servicios?.toObservable()?.filter { servicio ->
                        servicio.parto != null && servicio.parto?.fecha!!.time >= iniMilis && servicio.parto?.fecha!!.time <= endMilis
                    }
                }.count()
                .map {
                    Promedio("Partos", it, desde = from, hasta = to, mes = month, anio = year)
                }.applySchedulers()
    }

    fun totalServicios(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<Promedio> {
        val (ini, end) = processDates(from, to, month, year)

        val iniMilis = ini!!.time
        val endMilis = end!!.time

        return db.listByExp("finca" equalEx session.farmID
                andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                , Bovino::class)
                .flatMapObservable {
                    it.toObservable()
                }.flatMap {
                    it.servicios?.toObservable()?.filter { servicio ->
                        servicio.fecha!!.time in iniMilis..endMilis
                    }
                }
                .count().map {
                    Promedio("Servicios", it, desde = from, hasta = to, mes = month, anio = year)
                }.applySchedulers()
    }

    fun totalServiciosEfectivos(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<Promedio> {
        val (ini, end) = processDates(from, to, month, year)

        val iniMilis = ini!!.time
        val endMilis = end!!.time

        return db.listByExp("finca" equalEx session.farmID andEx ("genero" equalEx "Hembra")
                andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1)))
                , Bovino::class)
                .flatMapObservable {
                    it.toObservable()
                }.flatMap {
                    it.servicios?.toObservable()?.filter { servicio ->
                        (servicio.diagnostico?.confirmacion
                                ?: false) && servicio.fecha!!.time >= iniMilis && servicio.fecha!!.time <= endMilis
                    }
                }
                .count().map {
                    Promedio("Servicios Efectivos", it, desde = from, hasta = to, mes = month, anio = year)
                }.applySchedulers()
    }

    private fun promedioIntervaloPartos(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Maybe<Float> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        return db.listByExp("finca" equalEx session.farmID
                andEx ("genero" equalEx "Hembra")
                andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.intValue(2)))
                andEx ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios"))
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


    private fun intervaloPartosBovino(idBovino: String, from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Maybe<Float> {
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
                    .zipWith(promedioDiasVaciosBovino(bovino, from, to, mes, anio))
                    .map {
                        Promedio("Días Vacios", it.first, bovino, it.second, unidades = "Días",
                                desde = from,
                                hasta = to,
                                mes = mes,
                                anio = anio)
                    }


    companion object {
        private val VAR_SERV = ArrayExpression.variable("servicio")
        private val VAR_NOVEDAD = ArrayExpression.variable("servicio.novedad.novedad")
        private val VAR_PARTO = ArrayExpression.variable("servicio.parto")
        private val VAR_FECHA_PARTO = ArrayExpression.variable("servicio.parto.fecha")
    }

}