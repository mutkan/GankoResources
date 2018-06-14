package com.example.cristian.myapplication.ui.menu.meadow.aforo

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Aforo
import com.example.cristian.myapplication.data.models.Pradera
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.menu.meadow.MeadowViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.text
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_aforo.*
import org.jetbrains.anko.*
import java.util.*
import javax.inject.Inject

class AddAforoActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewmodel: MeadowViewModel by lazy { buildViewModel<MeadowViewModel>(factory) }

    val meadow: Pradera by lazy { intent.getParcelableExtra<Pradera>(MEADOW) }
    val dis = LifeDisposable(this)
    var quantity = 0
    var average = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_aforo)
        title = "Agregar Aforo"
    }

    override fun onResume() {
        super.onResume()
        dis add btnOk.clicks()
                .subscribe {
                    if (sampleQuantity.text().toInt() == 0 || sampleQuantity.text() == "") toast(R.string.empty_fields)
                    else {
                        generateFields(sampleQuantity.text().toInt())
                        btnOk.visibility = View.GONE
                    }
                }
        dis add btnCancel.clicks()
                .subscribe {
                    alert {
                        title = "¿Desea cancelar la operacion?"
                        yesButton { finish() }
                        noButton { }
                    }.show()
                }

        dis add btnAdd.clicks()
                .subscribe { saveAforo() }
    }

    fun saveAforo() {
        val list = mutableListOf<Float>()
        average = 0f
        for (i in 0 until quantity) {
            val tv = samplesGrid.getChildAt(i) as EditText
            if (tv.text.toString() != "") {
                val value = tv.text.toString().toFloat()
                average += value
                list.add(value)
            }
        }
        average /= quantity
        var aforoTotal = if (meadow.tamanoEnHectareas!!) average * meadow.tamano!! * 10000
        else average * meadow.tamano!!
        alert {
            title = "¿Desea guardar estos valores?"
            val view = LinearLayout(this@AddAforoActivity)
            view.orientation = LinearLayout.VERTICAL
            val tv = TextView(this@AddAforoActivity)
            tv.text = "Promedio: ${average} Kg/m²"
            tv.textSize = 20f
            tv.gravity = Gravity.CENTER
            val tv2 = TextView(this@AddAforoActivity)
            tv2.text = "Aforo: $aforoTotal Kg"
            tv2.textSize = 20f
            tv2.gravity = Gravity.CENTER
            view.addView(tv)
            view.addView(tv2)
            view.gravity = Gravity.CENTER
            customView{
                this.addView(view, null)
            }
            yesButton {
                val afo = Aforo(list, average, Date(), aforoTotal)
                meadow.aforo!!.add(afo)
                dis add viewmodel.updateMeadow(meadow)
                        .subscribeBy{
                            toast("Datos guardados correctamente")
                            finish()
                        }
            }
            noButton { }
        }.show()

    }

    fun generateFields(quantity: Int) {
        this.quantity = quantity
        for (i in 1..quantity) {
            samplesGrid.addView(provideEditText())
        }
    }

    fun provideEditText(): EditText {
        val tv = EditText(this)
        tv.gravity = Gravity.CENTER
        tv.hint = "" + 0
        tv.width = 300
        tv.inputType = InputType.TYPE_CLASS_NUMBER
        return tv
    }

    override fun onBackPressed() {
        alert {
            title = "¿Desea cancelar la operacion?"
            yesButton { finish() }
            noButton { }
        }.show()
        super.onBackPressed()
    }
    companion object {
        const val MEADOW = "meadow"
    }
}
