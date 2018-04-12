package com.example.cristian.myapplication.ui.bovine.milk

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.ListMilkBovineAdapter
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.subscribeByShot
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_milk_bovine.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class MilkBvnActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MilkBvnViewModel by lazy { buildViewModel<MilkBvnViewModel>(factory) }

    @Inject
    lateinit var milkAdapter:ListMilkBovineAdapter

    val dis:LifeDisposable = LifeDisposable(this)
    lateinit var idBovino:String

    var totalLitters:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_milk_bovine)
        recyclerListMilkBovine.adapter = milkAdapter
        recyclerListMilkBovine.layoutManager = LinearLayoutManager(this)
//        idBovino = intent.getStringExtra("idBovino")
        idBovino = "1"
    }

    override fun onResume() {
        super.onResume()

        totalLitters = 0
        dis add viewModel.getMilkProduction(idBovino)
                .subscribeBy(
                        onSuccess = {
                            milkAdapter.data = it
                            for (production in it){
                                totalLitters += production.litros.toInt()
                            }
                            totalListMilkBovine.text = totalLitters.toString()
                            averageListMilkBovine.text = (totalLitters/it.size).toString()
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



}
