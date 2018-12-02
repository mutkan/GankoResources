package com.ceotic.ganko.ui.bovine.milk

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.ceotic.ganko.R
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ListMilkBovineAdapter
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_milk_bovine.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class MilkBvnActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MilkBvnViewModel by lazy { buildViewModel<MilkBvnViewModel>(factory) }

    @Inject
    lateinit var milkAdapter: ListMilkBovineAdapter

    val idBovino:String by lazy { intent.extras.getString(EXTRA_ID) }

    val dis: LifeDisposable = LifeDisposable(this)


    var totalLitters: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_milk_bovine)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Produccion de Leche")
        recyclerListMilkBovine.adapter = milkAdapter
        recyclerListMilkBovine.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getMilkProduction(idBovino)
                .subscribeBy(
                        onSuccess = {
                            totalLitters = 0
                            milkAdapter.data = it
                            for (production in it) {
                                totalLitters += production.litros!!.toInt()
                            }
                            val average = if (it.isNotEmpty()) (totalLitters / it.size).toString()
                            else "0"
                            totalListMilkBovine.text = totalLitters.toString()
                            averageListMilkBovine.text = average
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add btnGoToAddMilkProduction.clicks()
                .subscribe {
                    startActivity<AddMilkBvnActivity>("idBovino" to idBovino)
                }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }


}
