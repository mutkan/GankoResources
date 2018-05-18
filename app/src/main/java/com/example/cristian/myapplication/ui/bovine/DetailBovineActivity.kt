package com.example.cristian.myapplication.ui.bovine

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.cristian.myapplication.R
import com.example.cristian.myapplication.data.models.Bovino
import com.example.cristian.myapplication.databinding.ActivityBovineProfileBinding
import com.example.cristian.myapplication.di.Injectable
import com.example.cristian.myapplication.ui.bovine.ceba.CebaBvnActivity
import com.example.cristian.myapplication.ui.bovine.feed.FeedBvnActivity
import com.example.cristian.myapplication.ui.bovine.health.HealthBvnActivity
import com.example.cristian.myapplication.ui.bovine.manage.ManageBvnActivity
import com.example.cristian.myapplication.ui.bovine.milk.AddMilkBvnActivity
import com.example.cristian.myapplication.ui.bovine.milk.MilkBvnActivity
import com.example.cristian.myapplication.ui.bovine.movement.MovementBvnActivity
import com.example.cristian.myapplication.ui.bovine.reproductive.ReproductiveBvnActivity
import com.example.cristian.myapplication.ui.bovine.vaccination.VaccinationBvnActivity
import com.example.cristian.myapplication.util.LifeDisposable
import com.example.cristian.myapplication.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_bovine_profile.*
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class DetailBovineActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: BovineViewModel by lazy { buildViewModel<BovineViewModel>(factory) }
    lateinit var binding: ActivityBovineProfileBinding

    val dis: LifeDisposable = LifeDisposable(this)

    val bovine: Bovino by lazy { intent.extras.getParcelable<Bovino>(BOVINE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bovine_profile)
        binding.bovine = bovine
        Picasso.get().load(bovine.imagen!!)
                .into(banner)
    }

    override fun onResume() {
        super.onResume()

        dis add btnAddMilkProfileBovine.clicks()
                .subscribe { startActivity<AddMilkBvnActivity>(EXTRA_ID to bovine._id) }

        dis add btnMilkProfileActivity.clicks()
                .subscribe { startActivity<MilkBvnActivity>(EXTRA_ID to bovine._id) }

        dis add btnReproduciveProfileActivity.clicks()
                .subscribe { startActivity<ReproductiveBvnActivity>(EXTRA_ID to bovine._id) }

        dis add btnMeatProfileActivity.clicks()
                .subscribe { startActivity<CebaBvnActivity>(EXTRA_ID to bovine._id) }

        dis add btnFeedingProfileActivity.clicks()
                .subscribe { startActivity<FeedBvnActivity>(EXTRA_ID to bovine._id) }

        dis add btnManagementProfileActivity.clicks()
                .subscribe { startActivity<ManageBvnActivity>(EXTRA_ID to bovine._id) }

        dis add btnMovementProfileActivity.clicks()
                .subscribe { startActivity<MovementBvnActivity>(EXTRA_ID to bovine._id) }

        dis add btnVaccinesProfileActivity.clicks()
                .subscribe { startActivity<VaccinationBvnActivity>(EXTRA_ID to bovine._id) }

        dis add btnHealthProfileActivity.clicks()
                .subscribe { startActivity<HealthBvnActivity>(EXTRA_ID to bovine._id) }
    }

    companion object {
        val BOVINE: String = "bovino"
        val EXTRA_ID: String = "idBovino"
    }

}
