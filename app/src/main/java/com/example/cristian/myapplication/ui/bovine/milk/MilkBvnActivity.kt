package com.example.cristian.myapplication.ui.bovine.milk

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_milk_bovine)
        recyclerListMilkBovine.adapter = milkAdapter
//        idBovino = intent.getStringExtra("idBovino")
        idBovino = "1"
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getMilkProduction(idBovino)
                .subscribeBy {
                    milkAdapter.data = it
                }
        dis add btnGoToAddMilkProduction.clicks()
                .subscribe {
                    startActivity<AddMilkBvnActivity>("idBovino" to idBovino)
                }

    }



}
