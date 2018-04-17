package com.example.cristian.myapplication.ui.bovine

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.R.id.*
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.ActivityBovineProfileBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.example.cristian.myapplication.util.subscribeByAction
import com.jakewharton.rxbinding2.view.clicks
import org.jetbrains.anko.toast
import javax.inject.Inject

class DetailBovineActivity : AppCompatActivity() , Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: BovineViewModel by lazy { buildViewModel<BovineViewModel>(factory) }
    lateinit var binding: ActivityBovineProfileBinding

    lateinit var nav: BovineNavigation

    val dis: LifeDisposable = LifeDisposable(this)

    val bovine: Bovino by lazy { intent.extras.getParcelable<Bovino>(EXTRA_BOVINE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bovine_profile)

    }

    override fun onResume() {
        super.onResume()

        dis add binding.btnMilkProfileActivity.clicks()
                .subscribeByAction(
                        onNext = {
                            nav.navigateToMilkBvnActivity(bovine._id!!)
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )

           dis add binding.btnReproduciveProfileActivity.clicks()
                .subscribeByAction(
                        onNext = {
                            nav.navigateToReproductiveBvnActivity(bovine._id!!)
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )

           dis add binding.btnMeatProfileActivity.clicks()
                .subscribeByAction(
                        onNext = {
                            nav.navigateToCebaBvnActivity(bovine._id!!)
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )
           dis add binding.btnFeedingProfileActivity.clicks()
                .subscribeByAction(
                        onNext = {
                            nav.navigateToFeedBvnActivity(bovine._id!!)
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )

           dis add binding.btnManagementProfileActivity.clicks()
                .subscribeByAction(
                        onNext = {
                            nav.navigateToManageBvnActivity(bovine._id!!)
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )

           dis add binding.btnMovementProfileActivity.clicks()
                .subscribeByAction(
                        onNext = {
                            nav.navigateToMovementsBvnActivity(bovine._id!!)
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )

           dis add binding.btnVaccinesProfileActivity.clicks()
                .subscribeByAction(
                        onNext = {
                            nav.navigateToVaccinationBvnActivity(bovine._id!!)
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )
           dis add binding.btnHealthProfileActivity.clicks()
                .subscribeByAction(
                        onNext = {
                            nav.navigateToHealthBvnActivity(bovine._id!!)
                        },
                        onError = {
                            toast(it.message!!)
                        },
                        onHttpError = {
                            toast(it)
                        }
                )
    }


    companion object {
        val EXTRA_BOVINE = "idBovine"
    }
}
