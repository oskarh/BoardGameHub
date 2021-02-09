package se.oskarh.boardgamehub.ui.settings

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.changelog_dialog.view.*
import kotlinx.android.synthetic.main.settings_fragment.*
import se.oskarh.boardgamehub.BuildConfig
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_CHANGELOG_SHOW
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_IS_ENABLED
import se.oskarh.boardgamehub.analytics.EVENT_RATING_CHANGE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_AUTOPLAY_VIDEOS
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_CACHE_CLEAR
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_HISTORY_CLEAR
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_OPEN_IN_YOUTUBE
import se.oskarh.boardgamehub.analytics.EVENT_SORT_CHANGE
import se.oskarh.boardgamehub.analytics.EVENT_TOP_CATEGORY_CHANGE
import se.oskarh.boardgamehub.analytics.EVENT_YOUTUBE_CHANNEL_DEFAULT
import se.oskarh.boardgamehub.api.RatingSource
import se.oskarh.boardgamehub.api.SortAction
import se.oskarh.boardgamehub.api.TopCategory
import se.oskarh.boardgamehub.api.YouTubeChannel
import se.oskarh.boardgamehub.databinding.SettingsFragmentBinding
import se.oskarh.boardgamehub.ui.common.BottomSheetHelper
import se.oskarh.boardgamehub.ui.settings.customizeui.CustomizeUiFragment
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.extension.addFragment
import se.oskarh.boardgamehub.util.extension.gone
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.isYouTubeInstalled
import se.oskarh.boardgamehub.util.extension.showOkCancelDialog
import se.oskarh.boardgamehub.util.extension.showSnackbar
import se.oskarh.boardgamehub.util.extension.toInt
import java.util.Locale
import javax.inject.Inject

class SettingsFragment : Fragment() {

    private lateinit var binding: SettingsFragmentBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)
        settingsViewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)
        initUi()
    }

    private fun initUi() {
        selected_filter.text = AppPreferences.selectedFilter.toDescription(requireView().context)
        filter_root.setOnClickListener {
            BottomSheetHelper.showBottomFilter(requireView().context) {
                selected_filter.text = AppPreferences.selectedFilter.toDescription(requireView().context)
            }
        }
        selected_rating.text = getString(AppPreferences.selectedRating.description)
        rating_root.setOnClickListener {
            changeRatingSource(activity) {
                selected_rating.text = getString(it.description)
            }
        }
        selected_sort.text = getString(R.string.sort_games_by, getString(AppPreferences.selectedSort.property).toLowerCase(Locale.ENGLISH))
        sort_root.setOnClickListener {
            changeSort(activity) {
                selected_sort.text = getString(R.string.sort_games_by, getString(it.property).toLowerCase(Locale.ENGLISH))
            }
        }
        clear_history_root.setOnClickListener {
            activity?.showOkCancelDialog(getString(R.string.delete_search_title), getString(R.string.delete_search_message)) {
                Analytics.logEvent(EVENT_SETTINGS_HISTORY_CLEAR)
                settingsViewModel.clearSearchHistory()
                it.showSnackbar(R.string.clear_search_history_done)
            }
        }
        clear_cache_root.setOnClickListener {
            activity?.showOkCancelDialog(getString(R.string.delete_cache_title), getString(R.string.delete_cache_message)) {
                Analytics.logEvent(EVENT_SETTINGS_CACHE_CLEAR)
                settingsViewModel.clearCache()
                it.showSnackbar(R.string.clear_cache_done)
            }
        }
        top_category_description.text = getString(R.string.showing_top_category, getString(AppPreferences.selectedTopCategory.categoryName).toLowerCase(Locale.ENGLISH))
        top_category_root.setOnClickListener {
            changeTopCategory(activity) { topCategory ->
                top_category_description.text = getString(R.string.showing_top_category, getString(topCategory.categoryName).toLowerCase(Locale.ENGLISH))
            }
        }
        customize_ui_root.setOnClickListener {
            requireActivity().addFragment(CustomizeUiFragment(), R.id.settings_content)
        }
        selected_youtube_channel.text = getString(R.string.showing_videos_from, AppPreferences.enabledYouTubeChannel.channelName)
        autoplay_switch.isChecked = AppPreferences.shouldAutoPlayVideos
        autoplay_switch.setOnCheckedChangeListener { button, isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_AUTOPLAY_VIDEOS, EVENT_PROPERTY_IS_ENABLED to isChecked.toInt())
            AppPreferences.shouldAutoPlayVideos = isChecked
            val message = if(isChecked) R.string.autplay_enabled else R.string.autplay_disabled
            button.showSnackbar(message)
        }
        open_in_youtube_switch.isChecked = AppPreferences.shouldOpenVideosInYouTube
        open_in_youtube_switch.setOnCheckedChangeListener { button, isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_OPEN_IN_YOUTUBE, EVENT_PROPERTY_IS_ENABLED to isChecked.toInt())
            AppPreferences.shouldOpenVideosInYouTube = isChecked
            autoplay_switch.isEnabled = !isChecked
            val message = if(isChecked) R.string.open_in_youtube_message else R.string.open_in_boardgamehub_message
            button.showSnackbar(message)
        }
        open_in_youtube_switch.gone() // Remove this!
        if (!requireView().context.isYouTubeInstalled()) {
            open_in_youtube_switch.isEnabled = false
            AppPreferences.shouldOpenVideosInYouTube = false
        }

        youtube_channel_root.setOnClickListener {
            changeYouTubeChannel(activity) { youTubeChannel ->
                selected_youtube_channel.text = getString(R.string.showing_videos_from, youTubeChannel.channelName)
            }
        }
    }

    companion object {

        fun changeRatingSource(activity: Activity?, onSelected: (RatingSource) -> Unit = {}) {
            activity?.let {
                val ratingDescriptions = RatingSource.values()
                    .map { activity.getString(it.description) }
                    .toTypedArray()

                AlertDialog.Builder(activity, R.style.AlertDialogTheme).run {
                    setTitle(activity.getString(R.string.average_title))
                    setSingleChoiceItems(ratingDescriptions, AppPreferences.selectedRating.ordinal) { dialog, index ->
                        Analytics.logEvent(EVENT_RATING_CHANGE)
                        AppPreferences.selectedRating = RatingSource.values()[index]
                        onSelected(RatingSource.values()[index])
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }
        }

        fun changeSort(activity: Activity?, onSelected: (SortAction) -> Unit = {}) {
            activity?.let {
                val channelNames = SortAction.values()
                    .map { activity.getString(it.property) }
                    .toTypedArray()

                AlertDialog.Builder(activity, R.style.AlertDialogTheme).run {
                    setTitle(activity.getString(R.string.sort_title))
                    setSingleChoiceItems(channelNames, AppPreferences.selectedSort.ordinal) { dialog, index ->
                        Analytics.logEvent(EVENT_SORT_CHANGE)
                        AppPreferences.selectedSort = SortAction.values()[index]
                        onSelected(SortAction.values()[index])
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }
        }

        fun changeTopCategory(activity: Activity?, onSelected: (TopCategory) -> Unit) {
            activity?.let {
                val channelNames = TopCategory.values()
                    .map { activity.getString(it.categoryName) }
                    .toTypedArray()

                AlertDialog.Builder(activity, R.style.AlertDialogTheme).run {
                    setTitle(activity.getString(R.string.choose_category))
                    setSingleChoiceItems(channelNames, AppPreferences.selectedTopCategory.ordinal) { dialog, index ->
                        Analytics.logEvent(EVENT_TOP_CATEGORY_CHANGE)
                        if (TopCategory.values()[index] != AppPreferences.selectedTopCategory) {
                            AppPreferences.selectedTopCategory = TopCategory.values()[index]
                            onSelected(TopCategory.values()[index])
                        }
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }
        }

        fun changeYouTubeChannel(activity: Activity?, onSelected: (YouTubeChannel) -> Unit) {
            val channelNames = YouTubeChannel.values()
                .map { it.channelName }
                .toTypedArray()

            activity?.let {
                AlertDialog.Builder(activity, R.style.AlertDialogTheme).run {
                    setTitle(activity.getString(R.string.choose_youtube_channel))
                    setSingleChoiceItems(channelNames, AppPreferences.enabledYouTubeChannel.ordinal) { dialog, index ->
                        Analytics.logEvent(EVENT_YOUTUBE_CHANNEL_DEFAULT)
                        AppPreferences.enabledYouTubeChannel = YouTubeChannel.values()[index]
                        onSelected(YouTubeChannel.values()[index])
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }
        }

        fun showChangelog(activity: Activity) {
            Analytics.logEvent(EVENT_CHANGELOG_SHOW)
            BottomSheetDialog(activity).run {
                val changelogView = activity.layoutInflater.inflate(R.layout.changelog_dialog, null)
                changelogView.update_title.text = activity.getString(R.string.app_updated, BuildConfig.VERSION_NAME)
                setContentView(changelogView)
                show()
            }
        }
    }
}