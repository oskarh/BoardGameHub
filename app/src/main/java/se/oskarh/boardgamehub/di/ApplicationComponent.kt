package se.oskarh.boardgamehub.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import se.oskarh.boardgamehub.repository.ImportCollectionService
import se.oskarh.boardgamehub.ui.common.PropertyDialog
import se.oskarh.boardgamehub.ui.details.BoardGameDetailsActivity
import se.oskarh.boardgamehub.ui.details.CommentsFragment
import se.oskarh.boardgamehub.ui.details.DetailsFragment
import se.oskarh.boardgamehub.ui.details.InformationFragment
import se.oskarh.boardgamehub.ui.details.VideosFragment
import se.oskarh.boardgamehub.ui.family.BoardGameFamilyActivity
import se.oskarh.boardgamehub.ui.feed.FeedFragment
import se.oskarh.boardgamehub.ui.list.BoardGameListFragment
import se.oskarh.boardgamehub.ui.main.MainActivity
import se.oskarh.boardgamehub.ui.settings.SettingsActivity
import se.oskarh.boardgamehub.ui.settings.SettingsFragment
import se.oskarh.boardgamehub.ui.video.VideoPlayerActivity
import se.oskarh.boardgamehub.ui.videolist.VideoListFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, ViewModelModule::class, RoomModule::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun applicationContext(applicationContext: Context): Builder

        fun build(): ApplicationComponent
    }

    fun inject(mainActivity: MainActivity)

    fun inject(detailsFragment: DetailsFragment)

    fun inject(feedFragment: FeedFragment)

    fun inject(settingsActivity: SettingsActivity)

    fun inject(commentsFragment: CommentsFragment)

    fun inject(informationFragment: InformationFragment)

    fun inject(videosFragment: VideosFragment)

    fun inject(videoPlayerActivity: VideoPlayerActivity)

    fun inject(boardGameListFragment: BoardGameListFragment)

    fun inject(videoListFragment: VideoListFragment)

    fun inject(settingsFragment: SettingsFragment)

    fun inject(propertyDialog: PropertyDialog)

    fun inject(detailsActivity: BoardGameDetailsActivity)

    fun inject(boardGameFamilyActivity: BoardGameFamilyActivity)

    fun inject(importCollectionService: ImportCollectionService)
}