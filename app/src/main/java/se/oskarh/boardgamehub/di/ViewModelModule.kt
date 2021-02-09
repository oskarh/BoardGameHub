package se.oskarh.boardgamehub.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import se.oskarh.boardgamehub.ui.details.DetailsViewModel
import se.oskarh.boardgamehub.ui.details.PropertyViewModel
import se.oskarh.boardgamehub.ui.family.BoardGameFamilyViewModel
import se.oskarh.boardgamehub.ui.feed.FeedViewModel
import se.oskarh.boardgamehub.ui.list.BoardGameListViewModel
import se.oskarh.boardgamehub.ui.main.AppViewModelFactory
import se.oskarh.boardgamehub.ui.main.MainActivityViewModel
import se.oskarh.boardgamehub.ui.settings.SettingsViewModel

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindAppViewModelFactory(viewModelFactory: AppViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FeedViewModel::class)
    abstract fun bindFeedViewModel(feedViewModel: FeedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailsViewModel::class)
    abstract fun bindDetailsViewModel(detailsViewModel: DetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(settingsViewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BoardGameListViewModel::class)
    abstract fun bindBoardGameListViewModel(boardGameListViewModel: BoardGameListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PropertyViewModel::class)
    abstract fun bindPropertyViewModel(propertyViewModel: PropertyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BoardGameFamilyViewModel::class)
    abstract fun bindBoardGameFamilyViewModel(boardGameFamilyViewModel: BoardGameFamilyViewModel): ViewModel
}
