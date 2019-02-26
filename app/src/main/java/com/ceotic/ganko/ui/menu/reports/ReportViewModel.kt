package com.ceotic.ganko.ui.menu.reports

import com.ceotic.ganko.data.db.CouchRx
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.data.preferences.UserSession
import com.ceotic.ganko.util.*
import com.couchbase.lite.ArrayExpression
import com.couchbase.lite.ArrayFunction
import com.couchbase.lite.Expression
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.toObservable
import java.util.*
import java.util.concurrent.TimeUnit

class ReportViewModel(private val db: CouchRx,
                      private val session:UserSession) {

    private fun reporteFuturosPartos(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        val q = ("finca" equalEx session.farmID
                andEx ("genero" equalEx "Hembra")
                andEx ("servicios[0].finalizado" equalEx false)
                andEx ("servicios[0].posFechaParto".betweenDates(ini!!, end!!))
                andEx ("retirado" equalEx false))
        return db.listByExp(q, Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map {
                    val srv = it.servicios!![0]
                    listOf(it.codigo!!, it.nombre!!, srv.fecha!!.toStringFormat(),
                            srv.posFechaParto!!.toStringFormat())
                }
                .toList()
                .applySchedulers()

    }


    private fun reporteSecado(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        val q = ("finca" equalEx session.farmID
                andEx ("genero" equalEx "Hembra")
                andEx ("servicios[0].finalizado" equalEx false)
                andEx ("servicios[0].posFechaParto" isNullEx false)
                andEx ("retirado" equalEx false))

        return db.listByExp(q, Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map { it to (it.servicios!![0].posFechaParto!!.time - 5184000000) }
                .filter { it.second in iniMilis..endMilis }
                .map {
                    val srv = it.first.servicios!![0]
                    listOf(it.first.codigo!!, it.first.nombre!!,
                            srv.fecha!!.toStringFormat(), Date(it.second).toStringFormat())
                }
                .toList()
                .applySchedulers()
    }

    private fun reportePreparacion(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        val q = ("finca" equalEx session.farmID
                andEx ("genero" equalEx "Hembra")
                andEx ("servicios[0].finalizado" equalEx false)
                andEx ("servicios[0].posFechaParto" isNullEx false)
                andEx ("retirado" equalEx false))

        return db.listByExp(q, Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map { it to (it.servicios!![0].posFechaParto!!.time - 2592000000) }
                .filter { it.second in iniMilis..endMilis }
                .map {
                    val srv = it.first.servicios!![0]
                    listOf(it.first.codigo!!, it.first.nombre!!,
                            Date(it.second).toStringFormat(),
                            srv.posFechaParto?.toStringFormat() ?: "")
                }
                .toList()
                .applySchedulers()
    }

    private fun reporteDiasVacios(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (_, end) = processDates(from, to, month, year)
        val endMilis = end!!.time
        val currentMilis = Date().time
        val cMilis = if (currentMilis < endMilis) currentMilis else endMilis

        val q = ("finca" equalEx session.farmID
                andEx ("genero" equalEx "Hembra")
                andEx ("retirado" equalEx false)
                andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(1))))

        return db.listByExp(q, Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map {
                    val srvs = it.servicios ?: emptyList()
                    val last = srvs.indexOfFirst { i ->
                        val milis = i.fecha!!.time
                        milis < cMilis && (i.parto != null || i.novedad != null)
                    }

                    val lastSrv = srvs.indexOfFirst { i ->
                        val milis = i.fecha!!.time
                        milis < cMilis && (i.diagnostico == null
                                || (i.diagnostico?.confirmacion ?: false))
                    }

                    val result: MutableList<String> = mutableListOf(it.codigo!!, it.nombre!!,
                            "Sin parto o Aborto", "", "0", "No")

                    if (last == -1 && lastSrv > -1) {
                        result[5] = "Si"
                        result[3] = srvs[lastSrv].fecha!!.toStringFormat()
                    } else if (last >= lastSrv && last != -1) {
                        val sLast = srvs[last]
                        val fromMilis = if (sLast.parto != null) sLast.parto!!.fecha!!.time
                        else sLast.novedad!!.fecha.time

                        val milis = cMilis - fromMilis
                        val days = milis.toDouble() / 86400000

                        result[2] = Date(fromMilis).toStringFormat()
                        result[3] = sLast.fecha!!.toStringFormat()
                        result[4] = "${Math.ceil(days)}"
                    } else if (last < lastSrv && last != -1) {
                        val s = srvs[lastSrv]
                        val toMilis = if (s.diagnostico != null && s.diagnostico!!.confirmacion) s.fecha!!.time
                        else cMilis

                        val sLast = srvs[last]
                        val fromMilis = if (sLast.parto != null) sLast.parto!!.fecha!!.time
                        else sLast.novedad!!.fecha.time


                        val milis = toMilis - fromMilis
                        val days = milis.toDouble() / 86400000

                        result[2] = Date(fromMilis).toStringFormat()
                        result[3] = sLast.fecha!!.toStringFormat()
                        result[4] = "${Math.ceil(days)}"
                    }
                    result.toList()
                }
                .toList()
                .applySchedulers()
    }

    private fun reportePartosAtendidos(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini?.time ?: 0
        val endMilis = end?.time ?: 0

        val q = ("finca" equalEx session.farmID
                andEx ("genero" equalEx "Hembra")
                andEx ("retirado" equalEx false)
                andEx ArrayFunction.length(Expression.property("servicios")).greaterThan(Expression.intValue(0))
                andEx ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios"))
                .satisfies(
                        VAR_PARTO.notNullOrMissing()
                                andEx (VAR_FECHA_PARTO.between(Expression.date(ini!!), Expression.date(end!!)))))

        return db.listByExp(q, Bovino::class)
                .flatMapObservable { it.toObservable() }
                .flatMap { bovino ->
                    bovino.servicios!!.toObservable()
                            .filter { serv ->
                                val milis = serv.parto?.fecha?.time ?: 0
                                serv.parto != null && milis <= endMilis && milis >= iniMilis
                            }
                            .map {
                                val parto = it.parto!!
                                listOf(bovino.codigo!!, bovino.nombre!!, parto.fecha!!.toStringFormat(), parto.sexoCria, parto.estadoCria)
                            }
                }.toList().applySchedulers()
    }

    private fun reporteAbortos(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        val endMilis = end!!.time
        val iniMilis = ini!!.time

        val q = ("finca" equalEx session.farmID
                andEx ("genero" equalEx "Hembra")
                andEx ("retirado" equalEx false)
                andEx ArrayFunction.length(Expression.property("servicios")).greaterThan(Expression.intValue(0))
                andEx ArrayExpression.any(VAR_SERV).`in`(Expression.property("servicios"))
                .satisfies(
                        VAR_NOVEDAD.equalTo(Expression.string("Aborto"))
                                andEx (VAR_FECHA_NOVEDAD.between(Expression.date(ini), Expression.date(end)))))

        return db.listByExp(q, Bovino::class)
                .flatMapObservable { it.toObservable() }
                .flatMap { bvn ->
                    bvn.servicios!!.toObservable()
                            .filter {
                                val milis = it.novedad?.fecha?.time ?: 0
                                it.novedad != null && it.novedad!!.novedad == "Aborto" && milis <= endMilis && milis >= iniMilis
                            }
                            .map {
                                listOf(bvn.codigo!!, bvn.nombre!!, it.fecha!!.toStringFormat(), it.novedad!!.fecha.toStringFormat())
                            }
                }.toList()
                .applySchedulers()
    }

    private fun reporteTresServicios(): Single<List<List<String>>> =
            db.listByExp("finca" equalEx session.farmID
                    andEx ("genero" equalEx "Hembra")
                    andEx ("retirado" equalEx false)
                    andEx (ArrayFunction.length(Expression.property("servicios")).greaterThanOrEqualTo(Expression.value(3)))
                    andEx ("servicios[0].diagnostico.confirmacion" equalEx false)
                    andEx ("servicios[1].diagnostico.confirmacion" equalEx false)
                    andEx ("servicios[2].diagnostico.confirmacion" equalEx false)
                    , Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .flatMapSingle { bvn ->
                        bvn.servicios!!.toObservable()
                                .filter { it.parto != null }
                                .map {
                                    val nowDate = Date().time
                                    val milis = nowDate.toDouble() - it.parto!!.fecha!!.time
                                    val days = Math.ceil(milis / 86400000)

                                    listOf(bvn.codigo!!, bvn.nombre!!,
                                            bvn.servicios!![0].fecha!!.toStringFormat(),
                                            "$days"
                                    )
                                }
                                .first(listOf(bvn.codigo!!, bvn.nombre!!,
                                        bvn.servicios!![0].fecha!!.toStringFormat(), "0"))
                    }
                    .toList()
                    .applySchedulers()


    private fun reporteCelos(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        val q = ("finca" equalEx session.farmID
                andEx ("genero" equalEx "Hembra")
                andEx ("retirado" equalEx false)
                andEx (ArrayFunction.length(Expression.property("celos")).greaterThanOrEqualTo(Expression.value(1)))
                andEx ArrayExpression.any(ArrayExpression.variable("c")).`in`(Expression.property("celos"))
                .satisfies(ArrayExpression.variable("c").between(Expression.date(ini), Expression.date(end))))

        return db.listByExp(q, Bovino::class)
                .flatMapObservable { it.toObservable() }
                .flatMapSingle { bvn ->
                    bvn.celos!!.toObservable()
                            .filter { it.time in iniMilis..endMilis }
                            .map { listOf(bvn.codigo!!, bvn.nombre!!, it.toStringFormat()) }
                            .first(listOf())
                }
                .filter { it.isNotEmpty() }
                .toList().applySchedulers()
    }

    private fun reporteResumen(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (_, end) = processDates(from, to, month, year)
        val endMilis = end!!.time
        val currMilis = Date().time

        //Filter Srv
        fun filterSrv(bvn: Bovino): Single<Triple<Servicio?, Servicio?, Servicio?>> {
            val srvs = (bvn.servicios ?: emptyList()).toObservable()
            val last = srvs.filter { it.fecha!!.time <= endMilis }
                    .map { Item(it) }
                    .first(Item(null))
            val lastPart = srvs.filter { it.fecha!!.time <= endMilis && it.parto != null }
                    .map { Item(it) }
                    .first(Item(null))
            val novelPart = srvs.filter { it.fecha!!.time <= endMilis && (it.parto != null || it.novedad != null) }
                    .map { Item(it) }
                    .first(Item(null))

            return Singles.zip(last, lastPart, novelPart)
                    .map { Triple(it.first.value, it.second.value, it.third.value) }
        }

        // Filter Zeal
        fun filterZeal(bvn: Bovino): Single<Item<Date>> = (bvn.celos ?: emptyList()).toObservable()
                .filter { it.time <= endMilis }
                .map { Item(it) }
                .first(Item(null))

        // Filter Milk
        fun filterMilk(bvn: Bovino): Single<Item<Produccion>> = db.listByExp("bovino" equalEx bvn._id!!,
                Produccion::class, 1, orderBy = arrayOf("fecha".orderEx(ASCENDING)))
                .flatMapObservable { it.toObservable() }
                .map { Item(it) }
                .first(Item(null))

        return db.listByExp("finca" equalEx session.farmID
                andEx ("genero" equalEx "Hembra")
                andEx ("retirado" equalEx false), Bovino::class)
                .flatMapObservable { it.toObservable() }
                .flatMapSingle { bvn ->
                    Singles.zip(filterSrv(bvn), filterZeal(bvn), filterMilk(bvn))
                            .map { (srvs, zeal, milk) ->
                                val (last, part, novel) = srvs

                                val cMilis = if (currMilis < endMilis) currMilis else endMilis
                                val lastMilis = last?.fecha?.time ?: 0
                                val partMilis = part?.fecha?.time ?: 0
                                val novelMilis = novel?.fecha?.time ?: 0
                                val zealMilis = zeal.value?.time ?: 0

                                val (lastNovelMilis, lastNovel) = if (partMilis > novelMilis) partMilis to part
                                else novelMilis to novel

                                var type = ""
                                var toroPajilla = ""
                                var confirm = ""
                                var openDays = 0
                                var secado = ""
                                var nextPart = ""
                                var lastSrv: Servicio? = null

                                when {
                                    lastMilis == lastNovelMilis && lastMilis > zealMilis -> lastSrv = lastNovel
                                    lastMilis == lastNovelMilis && lastMilis < zealMilis -> {
                                        lastSrv = lastNovel
                                        type = "C"
                                    }
                                    lastMilis > lastNovelMilis && lastMilis > zealMilis -> lastSrv = last
                                    lastMilis < lastNovelMilis && lastNovelMilis > zealMilis -> lastSrv = lastNovel
                                    zealMilis > lastMilis && zealMilis > lastNovelMilis -> {
                                        lastSrv = if (lastMilis > lastNovelMilis) last
                                        else lastNovel
                                        type = "C"
                                    }
                                    zealMilis == 0L && lastMilis == 0L && lastNovelMilis == 0L -> {
                                    }
                                    else -> {
                                        lastSrv = lastNovel
                                        type = "C"
                                    }
                                }

                                if (lastSrv != null) {
                                    if (type != "C") {
                                        type = if (lastSrv.empadre == "Motan Natural") "M" else "I"
                                    }
                                    toroPajilla = if (lastSrv.pajilla != null) lastSrv.pajilla!!
                                    else lastSrv.codigoToro!!
                                    confirm = if (lastSrv.diagnostico != null) lastSrv.fecha!!.toStringFormat()
                                    else ""

                                    if (lastSrv.posFechaParto != null) {
                                        secado = Date(lastSrv.posFechaParto!!.time - 5184000000).toStringFormat()
                                        if (lastMilis > lastNovelMilis) {
                                            nextPart = lastSrv.posFechaParto!!.toStringFormat()
                                        }
                                    }
                                }

                                // Open Days
                                if (novel != null) {
                                    var rMilis = cMilis
                                    if (lastMilis > lastNovelMilis && last != null && last.diagnostico != null && last.diagnostico!!.confirmacion) {
                                        rMilis = if (lastMilis < cMilis) lastMilis
                                        else cMilis
                                    }
                                    val pMilis = if (novel.parto != null) novel.parto!!.fecha!!.time
                                    else novel.novedad!!.fecha.time
                                    openDays = Math.ceil((rMilis.toDouble() - pMilis) / 86400000).toInt()
                                }

                                // Produccion
                                val milkMilis = milk.value?.fecha?.time ?: 0
                                val days = if (milkMilis != 0L) Math.ceil((cMilis.toDouble() - milkMilis) / 86400000).toInt()
                                else 0

                                // Plan
                                val plan = if (type == "C") {
                                    Date(zeal.value!!.time + 2073600000).toStringFormat()
                                } else if (lastMilis <= lastNovelMilis && lastNovelMilis != 0L) {
                                    val planMilis = if (lastNovel!!.parto != null) lastNovel.parto!!.fecha!!.time
                                    else lastNovel.novedad!!.fecha.time
                                    Date(planMilis + 5184000000).toStringFormat()
                                } else ""


                                listOf(bvn.codigo!!, bvn.nombre!!, part?.fecha?.toStringFormat()
                                        ?: "",
                                        type, toroPajilla, confirm, "$days", "$openDays", secado,
                                        nextPart, plan)
                            }
                }.toList()
                .applySchedulers()
    }

    private fun reporteConsolidado(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        val q = ("idFarm" equalEx session.farmID
                andEx ("fecha".betweenDates(ini!!, end!!)))

        return db.listByExp(q, SalidaLeche::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
                .flatMapObservable { it.toObservable() }
                .map {
                    listOf(it.type!!, it.fecha!!.toStringFormat(),
                            it.operacion!!, "${it.numeroLitros}", "${it.valorLitro}",
                            "${it.totalLitros}")
                }.toList().applySchedulers()
    }

    private fun reportesLeche(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        val q = ("idFinca" equalEx session.farmID
                andEx "fecha".betweenDates(ini!!, end!!))

        return db.listByExp(q, Produccion::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
                .flatMapObservable { it.toObservable() }
                .groupBy { it.bovino }
                .flatMap { gp ->
                    gp.toList().flatMapObservable { ml ->
                        db.oneById(gp.key!!, Bovino::class).flatMapObservable { bvn ->
                            ml.toObservable()
                                    .map { listOf(bvn.codigo!!, it.litros!!, it.fecha!!.toStringFormat(), it.jornada!!) }
                        }
                    }
                }
                .toList().applySchedulers()
    }

    private fun reportesDestete(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        val q = ("finca" equalEx session.farmID
                andEx "fechaDestete".betweenDates(ini!!, end!!)
                andEx ("retirado" equalEx false))

        return db.listByExp(q, Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map {
                    listOf(it.codigo?:"", it.nombre?:"", it.fechaNacimiento?.toStringFormat() ?: "", it.fechaDestete?.toStringFormat() ?: "", it.codigoMadre?: "")
                }.toList().applySchedulers()
    }

    private fun reporteGetPraderas(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        val iniMilis = ini!!.time
        val endMilis = end!!.time

        return db.listByExp("idFinca" equalEx session.farmID andEx ("identificador" isNullEx false), Pradera::class)
                .flatMapObservable { it.toObservable() }
                .flatMap { pr ->
                    (pr.mantenimiento ?: mutableListOf()).toObservable()
                            .filter { it.fechaMantenimiento!!.time in iniMilis..endMilis }
                            .map {
                                listOf("${pr.identificador}", pr.tipoGraminea!!, it.fechaMantenimiento!!.toStringFormat(),
                                        it.producto!!, "${it.cantidad}")
                            }
                }
                .toList()
                .applySchedulers()
    }

    private fun reporteOcupacionPraderas(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (_, end) = processDates(from, to, month, year)
        val endMilis = end!!.time

        val nowMilis =  Date().time
        val maxMilis = if(nowMilis < endMilis) nowMilis else endMilis

        return db.listByExp("idFinca" equalEx session.farmID andEx ("identificador" isNullEx false), Pradera::class)
                .flatMapObservable { it.toObservable() }
                .flatMapSingle { pr ->
                    (pr.mantenimiento ?: mutableListOf()).toObservable()
                            .filter { it.fechaMantenimiento!!.time <= endMilis }
                            .toList()
                            .map {
                                it.sortByDescending { m -> m.fechaMantenimiento!!.time }
                                it
                            }
                            .flatMap { m ->
                                db.listByExp("idPradera" equalEx pr.identificador!!.toString()
                                        andEx ("idFarm" equalEx session.farmID)
                                        andEx ("transactionDate" lte end), Movimiento::class, 1,
                                        orderBy = arrayOf("transactionDate" orderEx DESCENDING))
                                        .map {
                                            val manage: Mantenimiento? = if (m.isNotEmpty()) m[0] else null
                                            val movement: Movimiento? = if (it.isNotEmpty()) it[0] else null
                                            val freeMilis = movement?.fechaSalida?.time ?: 0
                                            val free = freeMilis in 1..(endMilis - 1)
                                            val freeDays = if (free) {
                                                Math.ceil((maxMilis.toDouble() - freeMilis) / 86400000)
                                            } else movement?.diasLibres ?: 0

                                            listOf("${pr.identificador}", manage?.graminea ?: "",
                                                    manage?.fechaMantenimiento?.toStringFormat() ?: "",
                                                    "$freeDays", if (free || movement == null) "Libre"
                                            else movement.transactionDate.toStringFormat()
                                            )


                                        }
                            }


                }
               .toList().applySchedulers()
    }


    private fun reporteMovimientos(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        return db.listByExp("idFarm" equalEx session.farmID andEx ("transactionDate".betweenDates(ini!!, end!!)), Movimiento::class)
                .flatMapObservable { it.toObservable() }
                .flatMap {
                    // Single.just(it)
                    it.bovinos!!.toObservable()
                            .flatMapMaybe { id ->
                                db.oneById(id, Bovino::class)
                            }.map { bov ->
                                listOf(bov.codigo!!, it.idPradera!!, it.transactionDate!!.toStringFormat())
                            }
                }.toList().applySchedulers()
    }

    private fun reporteAlimentacion(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        return db.listByExp("idFinca" equalEx session.farmID andEx ("fecha".betweenDates(ini!!, end!!)), RegistroAlimentacion::class)
                .flatMapObservable { it.toObservable() }
                .filter { it.bovinos != null }
                .flatMap {
                    it.bovinos!!.toObservable()
                            .flatMapMaybe { id ->
                                db.oneById(id, Bovino::class)
                            }.map { bov ->
                                listOf(bov.codigo!!, it.tipoAlimento!!, it.valorkg!!.toString() + " Kg", it.valorTotal!!.toString())
                            }
                }
                .toList().applySchedulers()
    }

    private fun reporteInventario(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        return db.listByExp("finca" equalEx session.farmID andEx ("fechaNacimiento".betweenDates(ini!!, end!!)), Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map {
                    var partos: String = ""
                    if (it.partos == null) partos = "0" else partos = it.partos.toString()
                    listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.color!!, it.raza!!, partos, it.codigoMadre
                            ?: "", it.codigoPadre ?: "")
                }.toList().applySchedulers()
    }


    private fun reporteTernerasEnEstaca(): Single<List<List<String>>> {
        val now = Date().time
        val end = Date(now - 259200000)
        val ini = Date(now - 12960000000)

        return db.listByExp("finca" equalEx session.farmID andEx ("fechaNacimiento".betweenDates(ini, end) andEx ("genero" equalEx "Hembra")), Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map {
                    listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre
                            ?: "", it.codigoPadre ?: "")
                }.toList().applySchedulers()
    }

    private fun reporteTernerasDestetas(): Single<List<List<String>>>{

        val now = Date().time
        val end = Date(now - 15552000000)
        val ini = Date(now - 31104000000)

        return db.listByExp("finca" equalEx session.farmID
                andEx ("fechaNacimiento".betweenDates(ini, end)
                andEx ("genero" equalEx "Hembra")), Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map {
                    listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre
                            ?: "", it.codigoPadre ?: "")
                }.toList().applySchedulers()

    }

    private fun reporteTerneraslevante(): Single<List<List<String>>> {
        val now = Date().time
        val end = Date(now - 31104000000)
        val ini = Date(now - 46656000000)

        return db.listByExp("finca" equalEx session.farmID andEx ("fechaNacimiento".betweenDates(ini, end) andEx ("genero" equalEx "Hembra")), Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map {
                    listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!, it.codigoMadre
                            ?: "", it.codigoPadre ?: "")
                }.toList().applySchedulers()
    }

    private fun reporteNovillasVientre(): Single<List<List<String>>>{
        val now = Date().time
        val end = Date(now - 46656000000)
        val ini = Date(now - 51840000000)

        return db.listByExp("finca" equalEx session.farmID andEx ("fechaNacimiento".betweenDates(ini, end) andEx ("genero" equalEx "Hembra")), Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map {
                    listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!)
                }.toList().applySchedulers()
    }

    private fun reporteVacas(): Single<List<List<String>>> =
            db.listByExp("finca" equalEx session.farmID andEx ("genero" equalEx "Hembra"), Bovino::class)
                    .flatMapObservable { it.toObservable() }
                    .filter {
                        val cal = Calendar.getInstance()
                        val dias = cal.get(Calendar.MONTH) + 20
                        dias <= it.fechaNacimiento!!.time && 1 <= it.partos!!
                    }
                    .map {
                        listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), it.proposito!!, it.raza!!)
                    }.toList().applySchedulers()


    private fun reporteSalida(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        return db.listByExp("finca" equalEx session.farmID andEx ("fechaSalida").betweenDates(ini!!, end!!), Bovino::class)
                .flatMapObservable { it.toObservable() }
                .map {
                    listOf(it.codigo!!, it.nombre!!, it.fechaSalida!!.toStringFormat(), it.motivoSalida!!)
                }.toList().applySchedulers()

    }

    private fun reporteVacunas(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        return db.listByExp("idFinca" equalEx session.farmID
                andEx "fecha".betweenDates(ini!!, end!!)
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
    }

    private fun reporteSanidad(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        return db.listByExp("idFinca" equalEx session.farmID andEx "fecha".betweenDates(ini!!, end!!), Sanidad::class
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
    }

    private fun reporteManejo(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        return db.listByExp("idFinca" equalEx session.farmID andEx "fecha".betweenDates(ini!!, end!!), RegistroManejo::class,
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
    }

    private fun reportePajillas(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        return db.listByExp("idFarm" equalEx session.farmID andEx ("fecha".betweenDates(ini!!, end!!)), Straw::class)
                .flatMapObservable { it.toObservable() }
                .map {
                    listOf(it.idStraw!!, it.layette!!, it.breed!!, it.purpose!!, it.bull!!, it.origin!!)
                }.toList().applySchedulers()
    }

    private fun reporteGananciaPeso(from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        val (ini, end) = processDates(from, to, month, year)
        return db.listByExp("finca" equalEx session.farmID, Bovino::class)
                .flatMapObservable { it.toObservable() }
                .flatMapSingle {
                    db.listByExp("fecha".betweenDates(ini!!, end!!) andEx ("bovino" equalEx it._id!!) andEx (("eliminado" equalEx false ) orEx ("eliminado" isNullEx true)), Ceba::class, orderBy = arrayOf("fecha" orderEx DESCENDING))
                            .map { cebaList ->
                                var gananciaPeso =0f
                                if (cebaList.size == 1) {
                                    gananciaPeso = cebaList[0].gananciaPeso!!
                                } else if(cebaList.size > 1) {
                                    val cebaMayor = cebaList[0]
                                    val cebaMenor = cebaList.last()
                                    val dif = cebaMayor.fecha!!.time - cebaMenor.fecha!!.time
                                    val dias = Math.ceil(dif.toDouble() / 86400000)
                                    gananciaPeso =((cebaMayor.peso!! - cebaMenor.peso!!) * 1000 / dias).toFloat()
                                }
                                listOf(it.codigo!!, it.nombre!!, it.fechaNacimiento!!.toStringFormat(), "$gananciaPeso Gr", it.proposito!!)
                            }
                }.toList().applySchedulers()
    }

    fun getReport(name:String,from: Date? = null, to: Date? = null, month: Int? = null, year: Int? = null): Single<List<List<String>>> {
        return when(name){
            "Partos futuros" -> reporteFuturosPartos(from, to, month, year)
            "Secado" -> reporteSecado(from, to, month, year)
            "Preparación" -> reportePreparacion(from, to, month, year)
            "Días abiertos" -> reporteDiasVacios(from, to, month, year)
            "Partos atendidos" -> reportePartosAtendidos(from, to, month, year)
            "Abortos" -> reporteAbortos(from, to, month, year)
            "Tres servicios" -> reporteTresServicios()
            "Celos" -> reporteCelos(from, to, month, year)
            "Consolidado de leche" -> reporteConsolidado(from, to, month, year)
            "Reporte de leche" -> reportesLeche(from, to, month, year)
            "Destetos" ->reportesDestete(from, to, month, year)
            "Ganancia diaria de peso" -> reporteGananciaPeso(from, to, month, year)
            "Praderas" -> reporteGetPraderas(from, to, month, year)
            "Ocupación de praderas" -> reporteOcupacionPraderas(from, to, month, year)
            "Animales en pradera" ->  reporteMovimientos(from, to, month, year)
            "Alimentación" -> reporteAlimentacion(from, to, month, year)
            "Inventario" -> reporteInventario(from, to, month, year)
            "Terneras en estaca" -> reporteTernerasEnEstaca()
            "Terneras destetas" ->  reporteTernerasDestetas()
            "Novillas de levante" -> reporteTerneraslevante()
            "Novillas vientre" ->  reporteNovillasVientre()
            "Vacas" -> reporteVacas()
            "Salida" -> reporteSalida(from, to, month, year)
            "Vacunas" -> reporteVacunas(from, to, month, year)
            "Sanidad" -> reporteSanidad(from, to, month, year)
            "Manejo" -> reporteManejo(from, to, month, year)
            "Pajillas" -> reportePajillas(from, to, month, year)
            else -> reporteResumen(from, to, month, year)
        }

    }



    companion object {
        private val VAR_SERV = ArrayExpression.variable("servicio")
        private val VAR_NOVEDAD = ArrayExpression.variable("servicio.novedad.novedad")
        private val VAR_PARTO = ArrayExpression.variable("servicio.parto")
        private val VAR_FECHA_PARTO = ArrayExpression.variable("servicio.parto.fecha")
        private val VAR_FECHA_NOVEDAD = ArrayExpression.variable("servicio.novedad.fecha")

    }

}

class Item<T>(val value: T? = null)