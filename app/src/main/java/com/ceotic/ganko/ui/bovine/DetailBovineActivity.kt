package com.ceotic.ganko.ui.bovine

import android.arch.lifecycle.ViewModelProvider
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ceotic.ganko.R
import com.ceotic.ganko.data.models.Bovino
import com.ceotic.ganko.databinding.ActivityBovineProfileBinding
import com.ceotic.ganko.di.Injectable
import com.ceotic.ganko.ui.bovine.ceba.CebaBvnActivity
import com.ceotic.ganko.ui.bovine.feed.FeedBvnActivity
import com.ceotic.ganko.ui.bovine.health.HealthBvnActivity
import com.ceotic.ganko.ui.bovine.manage.ManageBvnActivity
import com.ceotic.ganko.ui.bovine.milk.AddMilkBvnActivity
import com.ceotic.ganko.ui.bovine.milk.MilkBvnActivity
import com.ceotic.ganko.ui.bovine.movement.MovementBvnActivity
import com.ceotic.ganko.ui.bovine.reproductive.ReproductiveBvnActivity
import com.ceotic.ganko.ui.bovine.vaccination.VaccinationBvnActivity
import com.ceotic.ganko.util.LifeDisposable
import com.ceotic.ganko.util.buildViewModel
import com.jakewharton.rxbinding2.view.clicks
import com.squareup.picasso.Picasso
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_bovine_profile.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class DetailBovineActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: BovineViewModel by lazy { buildViewModel<BovineViewModel>(factory) }
    lateinit var binding: ActivityBovineProfileBinding

    val dis: LifeDisposable = LifeDisposable(this)

    var bovine: Bovino? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bovine_profile)
        setSupportActionBar(toolbarBvnProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_white)

        bovine = intent?.extras?.getParcelable(BOVINE)

    }

    override fun onResume() {
        super.onResume()
        if(bovine != null){
            setupBovine()
        }else{
            val idBovine = intent.extras.getString(EXTRA_ID)
            dis add viewModel.byId(idBovine)
                    .subscribeBy(
                            onSuccess = {
                                bovine = it
                                setupBovine()
                            }
                    )
        }


        dis add btnAddMilkProfileBovine.clicks()
                .subscribe { startActivity<AddMilkBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnMilkProfileActivity.clicks()
                .subscribe { startActivity<MilkBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnReproduciveProfileActivity.clicks()
                .subscribe { startActivity<ReproductiveBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnNextReproductive.clicks()
                .subscribe { startActivity<ReproductiveBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnMeatProfileActivity.clicks()
                .subscribe { startActivity<CebaBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnNextMeat.clicks()
                .subscribe { startActivity<CebaBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnFeedingProfileActivity.clicks()
                .subscribe { startActivity<FeedBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnNextFeeding.clicks()
                .subscribe { startActivity<FeedBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnManagementProfileActivity.clicks()
                .subscribe { startActivity<ManageBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnNextManagement.clicks()
                .subscribe { startActivity<ManageBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnMovementProfileActivity.clicks()
                .subscribe { startActivity<MovementBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnNextMovement.clicks()
                .subscribe { startActivity<MovementBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnVaccinesProfileActivity.clicks()
                .subscribe { startActivity<VaccinationBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnNextVaccines.clicks()
                .subscribe { startActivity<VaccinationBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnHealthProfileActivity.clicks()
                .subscribe { startActivity<HealthBvnActivity>(EXTRA_ID to bovine!!._id) }

        dis add btnNextHealth.clicks()
                .subscribe { startActivity<HealthBvnActivity>(EXTRA_ID to bovine!!._id) }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun setupBovine(){
        binding.bovine = bovine
        binding.sex = bovine!!.genero == "Hembra"

        bovine!!.imagen?.let { imageName ->
            dis add viewModel.getImage(bovine!!._id!!, imageName)
                    .subscribeBy(onSuccess = { file ->
                        Picasso.get().load(file)
                                .into(banner)
                    }, onComplete = {
                        toast("La imagen no se ha sincronizado aun")
                    })

            }
    }

    companion object {
        val BOVINE: String = "bovino"
        val EXTRA_ID: String = "idBovino"
    }

}
