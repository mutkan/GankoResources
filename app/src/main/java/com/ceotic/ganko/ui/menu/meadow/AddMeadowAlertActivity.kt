package com.ceotic.ganko.ui.menu.meadow

import android.arch.lifecycle.ViewModelProvider
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.*
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.util.*
import com.ceotic.ganko.work.NotificationWork
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_meadow_alert.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AddMeadowAlertActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MeadowViewModel by lazy { buildViewModel<MeadowViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    val idMeadow: String by lazy { intent.getStringExtra(MEADOWID) }
    var meadow = Pradera()
    var meadowAlarm = MeadowAlarm()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meadow_alert)
        supportActionBar?.title = "Agregar Alarma"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.prairie_primary)))
        window.statusBarColor = ContextCompat.getColor(this, R.color.prairie_dark)


    }

    override fun onResume() {
        super.onResume()
        dis add viewModel.getMeadow(idMeadow)
                .subscribe { meadow = it }

        dis add btnCancelMeadowAlarm.clicks()
                .subscribe {
                    alert {
                        title = "¿Desea Salir?"
                        yesButton { finish() }
                        noButton { }
                    }.show()
                }

        dis add btnAddMeadowAlarm.clicks()
                .flatMap { validateForm(R.string.empty_fields,meadowAlarmDate.text()) }
                .flatMapSingle {

                    var actualDate = meadowAlarmDate.text().toLong()
                    if(actualDate==0.toLong()) {
                        actualDate = 1.toLong()
                        toast("El valor no puede ser 0, se fijo en 1")
                    }
                    val time: Long = when (meadowAlarmTime.selectedItem.toString()) {
                        "Hora(s)" -> actualDate
                        "Día(s)" -> actualDate * 24
                        "Mes(es)" -> actualDate * 24 * 30
                        else -> actualDate * 24 * 365
                    }

                    val notifyTime: Long = when (time) {
                        in 0..6 -> time
                        in 7..12 -> time - 1
                        in 13..36 -> time - 3
                        else -> time - 24
                    }
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = Date().time + (time*3600000) + 600000
                    val date = calendar.time

                    meadowAlarm = MeadowAlarm(null, null, null, idMeadow,
                            "Acciones pendientes en Pradera " + meadow.identificador, //El ultimo valor como identificador se usa en la web
                            when (meadowAlarmType.selectedItem.toString()) {
                                "Ocupacion" -> "La pradera ${meadow.identificador} ya puede ser ocupada"
                                "Descanso" -> "La pradera ${meadow.identificador} ya debe ser desocupada"
                                else -> "La pradera ${meadow.identificador} debe ser fertilizada"
                            }, false, date, meadowAlarmType.selectedItem.toString())

                    val alarm = Alarm(
                            titulo = meadowAlarm.title,
                            descripcion = meadowAlarm.description,
                            alarma = when (meadowAlarmType.selectedItem.toString()) {
                                "Ocupacion" -> ALARM_MEADOW_OCUPATION
                                "Descanso" -> ALARM_MEADOW_REST
                                else -> ALARM_MEADOW_FERTILIZATION
                            },
                            fechaProxima = calendar.time,
                            type = TYPE_ALARM,
                            activa = true,
                            reference = "${meadow.identificador}"
                    )
                    viewModel.insertAlarm(alarm, notifyTime)
                            .flatMap { viewModel.addMeadowAlert(meadowAlarm) }
                            .map {
                                it to notifyTime
                            }

                }.subscribeBy {
                    toast("Alerta agregada exitosamente")
                    finish()
                }
    }

    companion object {
        const val MEADOWID = "meadow"
    }
}
