package com.ceotic.ganko.ui.bovine.movement

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.R
import com.ceotic.ganko.databinding.ActivityListMovementBovineBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.ListMovementBovineAdapter
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_movement_bovine.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class MovementBvnActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MovementBvnViewModel by lazy { buildViewModel<MovementBvnViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)
    lateinit var binding: ActivityListMovementBovineBinding
    val isEmpty: ObservableBoolean = ObservableBoolean(false)
    val idBovino:String by lazy{ intent.extras.getString(EXTRA_ID) }
    @Inject
    lateinit var adapter: ListMovementBovineAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_movement_bovine)
        binding.isEmpty = isEmpty
        recyclerListMovementBovine.adapter = adapter
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
        supportActionBar?.setTitle("Movimiento")

    }

    override fun onResume() {
        super.onResume()

        dis add  viewModel.getMovementBovine(idBovino)
                .subscribeBy(
                        onSuccess = {
                            adapter.data = it.asReversed()
                            isEmpty.set(it.isEmpty())
                        },
                        onError = {
                            toast(it.message!!)
                        }
                )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}
