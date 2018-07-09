package com.example.cristian.myapplication.ui.menu.straw

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Straw
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.menu.MenuViewModel
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.fixColor
import com.example.cristian.myapplication.util.validateForm
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_add_straw.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class StrawAddActivity : AppCompatActivity(), Injectable{


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
                            Straw(null, null, null, farmId, it[0], it[1], it[5], spinner.selectedItem.toString(), it[2], it[3], it[4], null))
                }.subscribeBy(
                        onComplete = {
                            toast("Completo")
                        },
                        onNext = {
                            toast("Entrada agregada exitosamente $it")
                            finish()

                        },
                        onError = {
                            toast(it.message!!)
                        }
                )

        dis add  btnCancelFeed.clicks()
                .subscribe{
                    finish()
                }

    }
    companion object {
        val EXTRA_ID: String = "idBovino"
    }
}
