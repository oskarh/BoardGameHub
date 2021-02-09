package se.oskarh.boardgamehub.ui.settings.customizeui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.customize_ui_fragment.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_IS_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_ARTISTS_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_CATEGORIES_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_COMPILATIONS_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_DESCRIPTION_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_DESIGNERS_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_EXPANSIONS_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_FAMILY_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_IMPLEMENTATIONS_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_MECHANICS_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_PUBLISHERS_VISIBLE
import se.oskarh.boardgamehub.analytics.EVENT_SETTINGS_TYPES_VISIBLE
import se.oskarh.boardgamehub.databinding.CustomizeUiFragmentBinding
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.extension.showSnackbar
import se.oskarh.boardgamehub.util.extension.toInt

class CustomizeUiFragment : Fragment() {

    private lateinit var binding: CustomizeUiFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = CustomizeUiFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUi()
    }

    private fun initUi() {
        description_switch.setupPropertySwitch(AppPreferences.isDescriptionVisible, R.string.description) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_DESCRIPTION_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isDescriptionVisible = isChecked
        }
        type_switch.setupPropertySwitch(AppPreferences.isTypesVisible, R.string.types) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_TYPES_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isTypesVisible = isChecked
        }
        categories_switch.setupPropertySwitch(AppPreferences.isCategoriesVisible, R.string.categories) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_CATEGORIES_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isCategoriesVisible = isChecked
        }
        mechanics_switch.setupPropertySwitch(AppPreferences.isMechanicsVisible, R.string.mechanics) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_MECHANICS_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isMechanicsVisible = isChecked
        }
        expansions_switch.setupPropertySwitch(AppPreferences.isExpansionsVisible, R.string.expansions) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_EXPANSIONS_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isExpansionsVisible = isChecked
        }
        compilations_switch.setupPropertySwitch(AppPreferences.isCompilationsVisible, R.string.compilations) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_COMPILATIONS_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isCompilationsVisible = isChecked
        }
        family_switch.setupPropertySwitch(AppPreferences.isFamilyVisible, R.string.boardgame_family) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_FAMILY_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isFamilyVisible = isChecked
        }
        implementation_switch.setupPropertySwitch(AppPreferences.isImplementationVisible, R.string.implementation) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_IMPLEMENTATIONS_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isImplementationVisible = isChecked
        }
        designers_switch.setupPropertySwitch(AppPreferences.isDesignerVisible, R.string.designers) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_DESIGNERS_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isDesignerVisible = isChecked
        }
        artists_switch.setupPropertySwitch(AppPreferences.isArtistVisible, R.string.artists) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_ARTISTS_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isArtistVisible = isChecked
        }
        publisher_switch.setupPropertySwitch(AppPreferences.isPublisherVisible, R.string.publishers) { isChecked ->
            Analytics.logEvent(EVENT_SETTINGS_PUBLISHERS_VISIBLE, EVENT_PROPERTY_IS_VISIBLE to isChecked.toInt())
            AppPreferences.isPublisherVisible = isChecked
        }
    }

    private fun SwitchCompat.setupPropertySwitch(isInitiallyEnabled: Boolean, @StringRes propertyName: Int, onCheckChanged: (Boolean) -> Unit) {
        isChecked = isInitiallyEnabled
        setOnCheckedChangeListener { buttonView, isChecked ->
            onCheckChanged(isChecked)
            val message =
                if(isChecked) {
                    getString(R.string.switch_enabled_message, getString(propertyName))
                } else {
                    getString(R.string.switch_disabled_message, getString(propertyName))
                }
            buttonView.showSnackbar(message)
        }
    }
}