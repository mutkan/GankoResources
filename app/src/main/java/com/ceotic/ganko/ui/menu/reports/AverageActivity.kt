package com.ceotic.ganko.ui.menu.reports


import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Promedio
import com.ceotic.ganko.databinding.ActivityAverageBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.fixColor
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


class AverageActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    lateinit var binding: ActivityAverageBinding
    val promedio: Promedio by lazy {
        intent.getParcelableExtra<Promedio>("promedio") ?: Promedio("null", 0)
    }
    val dis: LifeDisposable = LifeDisposable(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fixColor(11)
        title = "PROMEDIOS"
        binding = DataBindingUtil.setContentView(this, R.layout.activity_average)
        binding.promedio = promedio
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        if (promedio.bovino != null) {
            dis add viewModel.getBovineById(promedio.bovino!!)
                    .subscribeBy(
                            onSuccess = {
                                binding.bovino = it
                            }
                    )
        }
    }


}
