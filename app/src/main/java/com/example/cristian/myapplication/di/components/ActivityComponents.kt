package com.example.cristian.myapplication.di.components

import com.example.cristian.myapplication.di.ActivityScope
import com.example.cristian.myapplication.di.modules.AddGroupModule
import com.example.cristian.myapplication.di.modules.MeadowModule
import com.example.cristian.myapplication.di.modules.ReproductiveModule
import com.example.cristian.myapplication.di.modules.SelectModule
import com.example.cristian.myapplication.ui.account.LoginActivity
import com.example.cristian.myapplication.ui.bovine.AddBovineActivity
import com.example.cristian.myapplication.ui.bovine.DetailBovineActivity
import com.example.cristian.myapplication.ui.bovine.RemoveBovineActivity
import com.example.cristian.myapplication.ui.bovine.ceba.AddCebaBvnActivity
import com.example.cristian.myapplication.ui.bovine.ceba.CebaBvnActivity
import com.example.cristian.myapplication.ui.bovine.feed.FeedBvnActivity
import com.example.cristian.myapplication.ui.bovine.health.HealthBvnActivity
import com.example.cristian.myapplication.ui.bovine.manage.ManageBvnActivity
import com.example.cristian.myapplication.ui.bovine.milk.AddMilkBvnActivity
import com.example.cristian.myapplication.ui.bovine.milk.MilkBvnActivity
import com.example.cristian.myapplication.ui.bovine.movement.MovementBvnActivity
import com.example.cristian.myapplication.ui.bovine.reproductive.ReproductiveBvnActivity
import com.example.cristian.myapplication.ui.bovine.reproductive.add.AddBirthActivity
import com.example.cristian.myapplication.ui.bovine.reproductive.add.AddDiagnosisActivity
import com.example.cristian.myapplication.ui.bovine.reproductive.add.AddServiceActivity
import com.example.cristian.myapplication.ui.bovine.reproductive.add.AddZealActivity
import com.example.cristian.myapplication.ui.bovine.vaccination.VaccinationBvnActivity
import com.example.cristian.myapplication.ui.farms.AddFarmActivity
import com.example.cristian.myapplication.ui.farms.FarmActivity
import com.example.cristian.myapplication.ui.groups.AddGroupActivity
import com.example.cristian.myapplication.ui.groups.BovineSelectedActivity
import com.example.cristian.myapplication.ui.groups.SaveGroupActivity
import com.example.cristian.myapplication.ui.groups.SelectActivity
import com.example.cristian.myapplication.ui.menu.MenuActivity
import com.example.cristian.myapplication.ui.menu.meadow.ManageMeadowActivity
import com.example.cristian.myapplication.ui.menu.meadow.aforo.AddAforoActivity
import com.example.cristian.myapplication.ui.menu.meadow.mantenimiento.AddMantenimientoActivity
import com.example.cristian.myapplication.ui.menu.straw.StrawAddActivity
import com.example.cristian.myapplication.ui.menu.vaccines.AddVaccineActivity
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
}