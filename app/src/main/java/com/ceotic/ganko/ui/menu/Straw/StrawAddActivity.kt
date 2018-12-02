package com.ceotic.ganko.ui.menu.straw

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Straw
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.ceotic.ganko.util.fixColor
import com.ceotic.ganko.util.validateForm
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.activity_add_straw.*
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject

class StrawAddActivity : AppCompatActivity(), Injectable {


    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: StrawViewModel by lazy { buildViewModel<StrawViewModel>(factory) }
    val menuViewModel: MenuViewModel by lazy { buildViewModel<MenuViewModel>(factory) }
    private val farmId by lazy { menuViewModel.getFarmId() }
    val dis: LifeDisposable = LifeDisposable(this)

    val idBovino: String by lazy { intent.extras.getString(StrawAddActivity.EXTRA_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_straw)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.add_straw)
        fixColor(9)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        dis add btnAdd.clicks()
                .flatMap {
                    validateForm(R.string.empty_fields, strawId.text.toString(), layette.text.toString(),
                            bull.text.toString(), origin.text.toString(), value.text.toString(), breed.text.toString())
                }
                .flatMapSingle {
                    viewModel.addStraw(
                            Straw(null, null, null, farmId, Date(), it[0], it[1], it[5], spinner.selectedItem.toString(), it[2], it[3], it[4], Straw.UNUSED_STRAW))
                }
                .doOnError { toast(R.string.straw_exist) }
                .retry()
                .subscribe {
                    toast("Pajilla agregada exitosamente")
                    finish()
                }

        dis add btnCancelFeed.clicks()
                .subscribe {
                    finish()
                }

    }

    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}
