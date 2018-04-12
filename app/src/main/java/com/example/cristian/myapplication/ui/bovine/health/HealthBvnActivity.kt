package com.example.cristian.myapplication.ui.bovine.health

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.adapters.HealthBovineAdapter
import com.example.cristian.myapplication.ui.bovine.feed.FeedBvnActivity
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.subscribeByShot
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_health_bovine.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class HealthBvnActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: HealthBvnViewModel by lazy { buildViewModel<HealthBvnViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)

    //val idBovine:String by lazy{ intent.extras.getString(FeedBvnActivity.EXTRA_ID) }
    lateinit var idBovine:String
    
    @Inject
    lateinit var adapter: HealthBovineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_health_bovine)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Sanidad")
        recyclerListHealthBovine.adapter = adapter
        idBovine = "1"
    }

    override fun onResume() {
        super.onResume()

        dis add viewModel.getHealthBovine(idBovine)
                .subscribeBy(
                        onSuccess = {
                            adapter.health = it
                        },
                        onError = {
                            toast(it.message!!)
                        }

                )
    }

    companion object {
        val EXTRA_ID:String = "ganko.ui.bovine.feed"
    }
}
