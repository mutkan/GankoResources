package com.ceotic.ganko.di.components

import com.ceotic.ganko.di.ActivityScope
import com.ceotic.ganko.di.modules.*
import com.ceotic.ganko.ui.account.LoginActivity
import com.ceotic.ganko.ui.bovine.AddBovineActivity
import com.ceotic.ganko.ui.bovine.DetailBovineActivity
import com.ceotic.ganko.ui.bovine.RemoveBovineActivity
import com.ceotic.ganko.ui.bovine.ceba.AddCebaBvnActivity
import com.ceotic.ganko.ui.bovine.ceba.CebaBvnActivity
import com.ceotic.ganko.ui.bovine.feed.FeedBvnActivity
import com.ceotic.ganko.ui.bovine.health.HealthBvnActivity
import com.ceotic.ganko.ui.bovine.manage.ManageBvnActivity
import com.ceotic.ganko.ui.bovine.milk.AddMilkBvnActivity
import com.ceotic.ganko.ui.bovine.milk.MilkBvnActivity
import com.ceotic.ganko.ui.bovine.movement.MovementBvnActivity
import com.ceotic.ganko.ui.bovine.reproductive.ReproductiveBvnActivity
import com.ceotic.ganko.ui.bovine.reproductive.add.AddBirthActivity
import com.ceotic.ganko.ui.bovine.reproductive.add.AddDiagnosisActivity
import com.ceotic.ganko.ui.bovine.reproductive.add.AddServiceActivity
import com.ceotic.ganko.ui.bovine.reproductive.add.AddZealActivity
import com.ceotic.ganko.ui.bovine.vaccination.VaccinationBvnActivity
import com.ceotic.ganko.ui.farms.AddFarmActivity
import com.ceotic.ganko.ui.farms.FarmActivity
import com.ceotic.ganko.ui.groups.*
import com.ceotic.ganko.ui.menu.MenuActivity
import com.ceotic.ganko.ui.menu.feed.AddFeedActivity
import com.ceotic.ganko.ui.menu.health.AddHealthActivity
import com.ceotic.ganko.ui.menu.health.detail.HealthDetailActivity
import com.ceotic.ganko.ui.menu.management.AddManageActivity
import com.ceotic.ganko.ui.menu.management.detail.ManageDetailActivity
import com.ceotic.ganko.ui.menu.meadow.AddMeadowAlertActivity
import com.ceotic.ganko.ui.menu.meadow.ManageMeadowActivity
import com.ceotic.ganko.ui.menu.meadow.ManageMeadowAlertActivity
import com.ceotic.ganko.ui.menu.meadow.aforo.AddAforoActivity
import com.ceotic.ganko.ui.menu.meadow.mantenimiento.AddMantenimientoActivity
import com.ceotic.ganko.ui.menu.milk.AddMilkActivity
import com.ceotic.ganko.ui.menu.reports.AverageActivity
import com.ceotic.ganko.ui.menu.straw.StrawAddActivity
import com.ceotic.ganko.ui.menu.vaccines.AddVaccineActivity
import com.ceotic.ganko.ui.menu.vaccines.detail.VaccineDetailActivity
import com.ceotic.ganko.ui.search.SearchActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityComponents {

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindLoginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindCebaBvnActivity(): CebaBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddCebaBvnActivity(): AddCebaBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMilkBvnActivity(): MilkBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddMilkBvnActivity(): AddMilkBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindFeedBvnActivity(): FeedBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindHealthBvnActivity(): HealthBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindManageBvnActivity(): ManageBvnActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [FragmentComponent::class])
    abstract fun bindMenuActivity(): MenuActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindFarmActivity(): FarmActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddFarmActivity(): AddFarmActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ReproductiveModule::class])
    abstract fun bindReproductiveBvnActivity(): ReproductiveBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddBovineActivity(): AddBovineActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindRemoveBovineActivity(): RemoveBovineActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindDetailBovineActivity(): DetailBovineActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindVaccinationBvnActivity(): VaccinationBvnActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindMovementBvnActivity(): MovementBvnActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindAddZealActivity(): AddZealActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindAddServiceActivity(): AddServiceActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindAddDiagnosisActivity(): AddDiagnosisActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindAddBirthActivity(): AddBirthActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddStrawActivity(): StrawAddActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddFeedActivity(): AddFeedActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddManageActivity(): AddManageActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [MeadowModule::class])
    abstract fun bindManageMeadowActivity(): ManageMeadowActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddAforoActivity(): AddAforoActivity

    @ActivityScope
    @ContributesAndroidInjector()
    abstract fun bindAddMantenimientoActivity(): AddMantenimientoActivity


    @ActivityScope
    @ContributesAndroidInjector(modules = [SelectModule::class])
    abstract fun bindSelectActivity(): SelectActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [AddGroupModule::class])
    abstract fun bindAddGroupActivity(): AddGroupActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindSaveGroupActivity(): SaveGroupActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindBovineSelectedActivity(): BovineSelectedActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindAddVaccineActivity(): AddVaccineActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindAddMilkActivity(): AddMilkActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindAddHealthActivity(): AddHealthActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindReApplyActivity(): ReApplyActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [VaccinesDetailModule::class])
    abstract fun bindVaccineDetailActivity(): VaccineDetailActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [HealthDetailModule::class])
    abstract fun bindHealthDetailActivity(): HealthDetailActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ManageDetailModule::class])
    abstract fun bindManageDetailActivity(): ManageDetailActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindAddMeadowAlertActivity(): AddMeadowAlertActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindManageMeadowAlertActivity(): ManageMeadowAlertActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindSearchActivity(): SearchActivity

    @ActivityScope
    @ContributesAndroidInjector
    abstract fun bindAverageActivity(): AverageActivity

}