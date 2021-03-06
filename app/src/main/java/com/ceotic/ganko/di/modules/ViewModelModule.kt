package com.ceotic.ganko.di.modules

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.ceotic.ganko.di.ViewModelKey
import com.ceotic.ganko.ui.account.AccountViewModel
import com.ceotic.ganko.ui.bovine.BovineViewModel
import com.ceotic.ganko.ui.bovine.ceba.CebaViewModel
import com.ceotic.ganko.ui.bovine.feed.FeedBvnViewModel
import com.ceotic.ganko.ui.bovine.health.HealthBvnViewModel
import com.ceotic.ganko.ui.bovine.manage.ManageBvnViewModel
import com.ceotic.ganko.ui.bovine.milk.MilkBvnViewModel
import com.ceotic.ganko.ui.bovine.movement.MovementBvnViewModel
import com.ceotic.ganko.ui.bovine.reproductive.ReproductiveBvnViewModel
import com.ceotic.ganko.ui.bovine.vaccination.VaccinationBvnViewModel
import com.ceotic.ganko.ui.farms.FarmViewModel
import com.ceotic.ganko.ui.groups.GroupViewModel
import com.ceotic.ganko.ui.menu.feed.FeedViewModel
import com.ceotic.ganko.ui.menu.MenuViewModel
import com.ceotic.ganko.ui.menu.health.HealthViewModel
import com.ceotic.ganko.ui.menu.meadow.MeadowViewModel
import com.ceotic.ganko.ui.menu.milk.MilkViewModel
import com.ceotic.ganko.ui.menu.straw.StrawViewModel
import com.ceotic.ganko.ui.search.SearchViewModel
import com.ceotic.ganko.ui.sync.SyncViewModel
import com.ceotic.ganko.util.AppViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CebaViewModel::class)
    abstract fun bindCebaViewModel(viewModel: CebaViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(viewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FarmViewModel::class)
    abstract fun bindFarmViewModel(viewModel: FarmViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BovineViewModel::class)
    abstract fun bindBovineViewModel(viewModel: BovineViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FeedBvnViewModel::class)
    abstract fun bindFeedBvnViewModel(viewModel: FeedBvnViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ManageBvnViewModel::class)
    abstract fun bindManageBvnViewModel(viewModel: ManageBvnViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovementBvnViewModel::class)
    abstract fun bindMovementBvnViewModel(viewModel: MovementBvnViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MilkBvnViewModel::class)
    abstract fun bindMilkBvnViewModel(viewModel: MilkBvnViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HealthBvnViewModel::class)
    abstract fun bindHealthBvnViewModel(viewModel: HealthBvnViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VaccinationBvnViewModel::class)
    abstract fun bindVaccinationBvnViewModel(viewModel: VaccinationBvnViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MenuViewModel::class)
    abstract fun bindMenuViewModel(viewModel: MenuViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReproductiveBvnViewModel::class)
    abstract fun bindReproductiveBvnViewModel(viewModel: ReproductiveBvnViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FeedViewModel::class)
    abstract fun bindFeedViewModel(viewModel: FeedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StrawViewModel::class)
    abstract fun bindStrawViewModel(viewModel: StrawViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MeadowViewModel::class)
    abstract fun bindMeadowViewModel(viewModel: MeadowViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GroupViewModel::class)
    abstract fun bindGroupViewModel(viewModel: GroupViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MilkViewModel::class)
    abstract fun bindMilkViewModel(viewModel: MilkViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HealthViewModel::class)
    abstract fun bindHealthViewModel(viewModel: HealthViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SyncViewModel::class)
    abstract fun syncViewModel(viewModel: SyncViewModel): ViewModel

}