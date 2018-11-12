package com.ceotic.ganko.ui.bovine.health

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.ceotic.ganko.R
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.adapters.HealthBovineAdapter
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_list_health_bovine.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class HealthBvnActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: HealthBvnViewModel by lazy { buildViewModel<HealthBvnViewModel>(factory) }
    val dis: LifeDisposable = LifeDisposable(this)

    val idBovino:String by lazy{ intent.extras.getString(EXTRA_ID) }

    @Inject
    lateinit var adapter: HealthBovineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_health_bovine)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Sanidad")
        recyclerListHealthBovine.adapter = adapter


    }

    override fun onResume() {
        super.onResume()

        recyclerListHealthBovine.adapter= adapter
        recyclerListHealthBovine.layoutManager = LinearLayoutManager(this)

        dis add viewModel.getHealthBovine(idBovino)
                .subscribeBy(
                        onSuccess = {
                            if(it.isEmpty()) emptyHealthText.visibility = View.VISIBLE else emptyHealthText.visibility = View.GONE
                            adapter.health = it
                        },
                        onError = {
                            toast(it.message!!)
                        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }

}
