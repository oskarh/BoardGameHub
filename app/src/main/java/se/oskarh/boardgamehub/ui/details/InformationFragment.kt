package se.oskarh.boardgamehub.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.board_game_type_dialog.view.*
import kotlinx.android.synthetic.main.complexity_dialog.view.*
import kotlinx.android.synthetic.main.information_page.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_ALL_EXPANSIONS
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAMEGEEK_BOARDGAME_OPEN
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAMEGEEK_LINK_OPEN
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAME_DESCRIPTION_EXPAND
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAME_SHARE
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_BOARD_GAME_TYPE
import se.oskarh.boardgamehub.analytics.EVENT_DIALOG_WEIGHT
import se.oskarh.boardgamehub.analytics.EVENT_FAMILY_OPEN
import se.oskarh.boardgamehub.analytics.EVENT_PROPERTY_BOARDGAME_ID
import se.oskarh.boardgamehub.analytics.ScreenType
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.api.LinkType
import se.oskarh.boardgamehub.api.PollType
import se.oskarh.boardgamehub.databinding.InformationPageBinding
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.boardgamemeta.PropertyType
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.ui.common.LanguageDependencyDialog
import se.oskarh.boardgamehub.ui.common.LazyLoadableFragment
import se.oskarh.boardgamehub.ui.common.PropertyDialog
import se.oskarh.boardgamehub.ui.common.RecommendedAgeDialog
import se.oskarh.boardgamehub.ui.family.BoardGameFamilyActivity
import se.oskarh.boardgamehub.ui.list.BoardGameListActivity
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.BOARDGAME_WEIGHT_URL
import se.oskarh.boardgamehub.util.COULD_NOT_PARSE_DEFAULT
import se.oskarh.boardgamehub.util.KEY_BOARDGAME_FAMILY_ID
import se.oskarh.boardgamehub.util.KEY_BOARDGAME_LIST
import se.oskarh.boardgamehub.util.KEY_DETAILS_ID
import se.oskarh.boardgamehub.util.KEY_DETAILS_SOURCE
import se.oskarh.boardgamehub.util.KEY_DETAILS_TITLE
import se.oskarh.boardgamehub.util.KEY_IS_RANKED_LIST
import se.oskarh.boardgamehub.util.KEY_SCREEN
import se.oskarh.boardgamehub.util.KEY_TITLE
import se.oskarh.boardgamehub.util.MAXIMUM_VISIBLE_EXPANSIONS
import se.oskarh.boardgamehub.util.MINIMUM_REQUIRED_VOTES
import se.oskarh.boardgamehub.util.RankFormatter
import se.oskarh.boardgamehub.util.UNCATEGORIZED_BOARDGAME
import se.oskarh.boardgamehub.util.extension.addChips
import se.oskarh.boardgamehub.util.extension.addRankChips
import se.oskarh.boardgamehub.util.extension.gone
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.italicize
import se.oskarh.boardgamehub.util.extension.rotateBy
import se.oskarh.boardgamehub.util.extension.setImageDrawableCompat
import se.oskarh.boardgamehub.util.extension.shareBoardGameIntent
import se.oskarh.boardgamehub.util.extension.showSnackbar
import se.oskarh.boardgamehub.util.extension.showTapTarget
import se.oskarh.boardgamehub.util.extension.startActivity
import se.oskarh.boardgamehub.util.extension.startAnimation
import se.oskarh.boardgamehub.util.extension.underline
import se.oskarh.boardgamehub.util.extension.visible
import se.oskarh.boardgamehub.util.extension.visibleIf
import javax.inject.Inject

class InformationFragment : LazyLoadableFragment() {

    private lateinit var binding: InformationPageBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var detailsViewModel: DetailsViewModel

    private var isDescriptionExpanded = false

    private var isExpansionsExpanded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = InformationPageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        detailsViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(DetailsViewModel::class.java)
        detailsViewModel.detailsResponse.observe(viewLifecycleOwner, Observer { detailsResponse ->
            information_root.visibleIf { detailsResponse is SuccessResponse }
            when (detailsResponse) {
                is SuccessResponse -> setupUi(detailsResponse.data)
            }
        })
        detailsViewModel.favoriteBoardGame.observe(viewLifecycleOwner, Observer { favorite ->
            setFavoriteImage(favorite != null)
        })
        detailsViewModel.addedFavorite.observe(viewLifecycleOwner, Observer { event ->
            if (event.hasUpdate()) {
                favorite_image.showSnackbar(R.string.added_favorite)
                favorite_image.startAnimation(R.anim.heartbeat_animation)
            }
        })
        detailsViewModel.removedFavorite.observe(viewLifecycleOwner, Observer { event ->
            if (event.hasUpdate()) {
                favorite_image.showSnackbar(R.string.removed_favorite)
            }
        })
        favorite_image.setOnClickListener {
            detailsViewModel.toggleFavorite()
        }
    }

    private fun setupUi(details: BoardGame) {
        min_age.text = details.minimumAgeFormatted()
        min_age_root.setOnClickListener {
            details.getPoll(PollType.SUGGESTED_PLAYER_AGE)
                ?.takeIf { it.totalVotes > MINIMUM_REQUIRED_VOTES }
                ?.let { suggestedAgePoll ->
                    RecommendedAgeDialog.newInstance(suggestedAgePoll).show(parentFragmentManager, null)
                } ?: min_age_root.showSnackbar(R.string.minimum_age_missing)
        }
        boardgame_type.setImageResource(details.type.icon)
        boardgame_type.setOnClickListener {
            showBoardGameTypeDialog(details.type)
        }
        language_dependency.setOnClickListener {
            details.getPoll(PollType.LANGUAGE_DEPENDENCE)
                ?.takeIf { it.totalVotes > MINIMUM_REQUIRED_VOTES }
                ?.let {
                    LanguageDependencyDialog.newInstance(it).show(parentFragmentManager, null)
                } ?: language_dependency.showSnackbar(R.string.language_dependency_missing)
        }
        share_image.setOnClickListener {
            Analytics.logEvent(EVENT_BOARDGAME_SHARE, EVENT_PROPERTY_BOARDGAME_ID to details.id)
            startActivity(Intent.createChooser(it.context.shareBoardGameIntent(details.id, details.primaryName()), it.context.getString(R.string.share_title)))
        }
        browser_image.setOnClickListener {
            Analytics.logEvent(EVENT_BOARDGAMEGEEK_BOARDGAME_OPEN)
            startActivity(Intent(Intent.ACTION_VIEW, it.context.getString(R.string.boardgamegeek_game_link_builder, id).toUri()))
        }

        complexity_root.setOnClickListener {
            showWeightDialog()
        }
        complexity_text.text = details.statistics?.complexity
            ?.takeIf { it != "0" }
            ?.toFloatOrNull()
            ?.let { RankFormatter.format(it) }
            ?: COULD_NOT_PARSE_DEFAULT

        if (details.description.isNullOrBlank()) {
            description_text.text = getString(R.string.no_description)
            description_text.italicize()
        } else {
            description_text.text = details.description
        }
        description_root.visibleIf { AppPreferences.isDescriptionVisible }
        description_root.setOnClickListener {
            if (!isDescriptionExpanded) {
                Analytics.logEvent(EVENT_BOARDGAME_DESCRIPTION_EXPAND)
            }
            isDescriptionExpanded = !isDescriptionExpanded
            expand_description.rotateBy(180f)
            if (isDescriptionExpanded) {
                description_text.maxLines = Int.MAX_VALUE
                show_more_text.text = getString(R.string.show_less)
            } else {
                description_text.maxLines = 4
                show_more_text.text = getString(R.string.show_more)
            }
        }

        type_root.visibleIf { AppPreferences.isTypesVisible }
        type_title.text = resources.getQuantityString(R.plurals.type_plurals, details.boardGameTypes().size)
        type_chips.addRankChips(requireContext(), details.boardGameTypes()) {
            // TODO: Nice extension function for this?
            if (it != UNCATEGORIZED_BOARDGAME.first()) {
                PropertyDialog.newInstance(it.friendlyname, it.id, PropertyType.TYPE).show(parentFragmentManager, null)
            }
        }
        if (!AppPreferences.hasShownMinimumAgeOnboarding && (details.getPoll(PollType.SUGGESTED_PLAYER_AGE)?.totalVotes ?: 0) > MINIMUM_REQUIRED_VOTES) {
            activity?.showTapTarget(min_age_image, R.string.onboarding_minimum_age_title, R.string.onboarding_minimum_age_message)
            AppPreferences.hasShownMinimumAgeOnboarding = true
        } else if (!AppPreferences.hasShownPropertyOnboarding && details.boardGameTypes().first() != UNCATEGORIZED_BOARDGAME.first()) {
            activity?.showTapTarget(type_chips.children.first(), R.string.onboarding_property_title, R.string.onboarding_property_message)
            AppPreferences.hasShownPropertyOnboarding = true
        }

        category_root.visibleIf { details.hasLink(LinkType.CATEGORY) && AppPreferences.isCategoriesVisible }
        category_title.text = resources.getQuantityString(R.plurals.category_plurals, details.getLinks(LinkType.CATEGORY).size)
        category_chips.addChips(requireContext(), details.getLinks(LinkType.CATEGORY)) {
            // TODO: Nice extension function for this?
            PropertyDialog.newInstance(it.value, it.id, PropertyType.BOARD_GAME_CATEGORY).show(parentFragmentManager, null)
        }

        mechanics_root.visibleIf { details.hasLink(LinkType.MECHANIC) && AppPreferences.isMechanicsVisible }
        mechanic_title.text = resources.getQuantityString(R.plurals.mechanic_plurals, details.getLinks(LinkType.MECHANIC).size)
        mechanics_chips.addChips(requireContext(), details.getLinks(LinkType.MECHANIC)) {
            // TODO: Nice extension function for this?
            PropertyDialog.newInstance(it.value, it.id, PropertyType.BOARD_GAME_MECHANIC).show(parentFragmentManager, null)
        }

        expansions_root.visibleIf { details.hasLink(LinkType.EXPANSION) && AppPreferences.isExpansionsVisible }
        expansion_title.text = resources.getQuantityString(R.plurals.expansion_plurals, details.getLinks(LinkType.EXPANSION).size)
        expansion_chips.addChips(requireContext(), details.getVisibleExpansions()) {
            startActivity<BoardGameDetailsActivity>(KEY_DETAILS_TITLE to it.value, KEY_DETAILS_ID to it.id)
        }

        expand_expansions_root.visibleIf { details.getNonVisibleExpansions().isNotEmpty() }
        expand_expansions_root.setOnClickListener {
            isExpansionsExpanded = !isExpansionsExpanded
            expand_expansions.rotateBy(180f)
            if (isExpansionsExpanded) {
                expansion_chips.addChips(requireContext(), details.getNonVisibleExpansions(), true) {
                    startActivity<BoardGameDetailsActivity>(KEY_DETAILS_TITLE to it.value, KEY_DETAILS_ID to it.id)
                }
                show_more_expansions_text.text = getString(R.string.show_less)
            } else {
                expansion_chips.removeViews(MAXIMUM_VISIBLE_EXPANSIONS, details.getLinks(LinkType.EXPANSION).size - MAXIMUM_VISIBLE_EXPANSIONS)
                show_more_expansions_text.text = getString(R.string.show_more)
            }
        }

        show_all_expansions_button.visibleIf { details.getLinks(LinkType.EXPANSION).size > 1 }
        show_all_expansions_button.setOnClickListener {
            Analytics.logEvent(EVENT_ALL_EXPANSIONS)
            val expansions = details.getLinks(LinkType.EXPANSION).map { expansion ->
                BoardGame(expansion.id, expansion.value, GameType.BOARDGAME_EXPANSION, 0, true)
            }
            startActivity<BoardGameListActivity>(
                KEY_BOARDGAME_LIST to expansions,
                KEY_IS_RANKED_LIST to false,
                KEY_TITLE to getString(R.string.boardgame_expansions, details.primaryName()),
                KEY_SCREEN to ScreenType.FAMILY.ordinal,
                KEY_DETAILS_SOURCE to DetailsSource.FAMILY.ordinal)
        }

        compilations_root.visibleIf { details.hasLink(LinkType.COMPILATION) && AppPreferences.isCompilationsVisible }
        compilation_title.text = resources.getQuantityString(R.plurals.compilation_plurals, details.getLinks(LinkType.COMPILATION).size)
        compilations_chips.addChips(requireContext(), details.getLinks(LinkType.COMPILATION)) {
            startActivity<BoardGameDetailsActivity>(KEY_DETAILS_TITLE to it.value, KEY_DETAILS_ID to it.id)
        }

        implementation_root.visibleIf { details.hasLink(LinkType.IMPLEMENTATION) && AppPreferences.isImplementationVisible }
        implementation_title.text = resources.getQuantityString(R.plurals.implementation_plurals, details.getLinks(LinkType.IMPLEMENTATION).size)
        implementation_chips.addChips(requireContext(), details.getLinks(LinkType.IMPLEMENTATION)) {
            startActivity<BoardGameDetailsActivity>(KEY_DETAILS_TITLE to it.value, KEY_DETAILS_ID to it.id)
        }

        // TODO: Implement integration
        integration_root.gone()
//        integration_root.visibleIf { details.hasLink(LinkType.INTEGRATION) && AppPreferences.isImplementationVisible }

        family_root.visibleIf { details.hasLink(LinkType.FAMILY) && AppPreferences.isFamilyVisible }
        family_title.text = resources.getQuantityString(R.plurals.family_plurals, details.getLinks(LinkType.FAMILY).size)
        family_chips.addChips(requireContext(), details.getLinks(LinkType.FAMILY)) {
            Analytics.logEvent(EVENT_FAMILY_OPEN)
            // TODO: Change to same key for same property everywhere
            startActivity<BoardGameFamilyActivity>(KEY_TITLE to it.value, KEY_BOARDGAME_FAMILY_ID to it.id)
        }

        designer_root.visibleIf { details.hasLink(LinkType.DESIGNER) && AppPreferences.isDesignerVisible }
        designer_title.text = resources.getQuantityString(R.plurals.designer_plurals, details.getLinks(LinkType.DESIGNER).size)
        designer_chips.addChips(requireContext(), details.getLinks(LinkType.DESIGNER)) {
            // TODO: Nice extension function for this?
            PropertyDialog.newInstance(it.value, it.id, PropertyType.DESIGNER).show(parentFragmentManager, null)
        }

        artist_root.visibleIf { details.hasLink(LinkType.ARTIST) && AppPreferences.isArtistVisible }
        artist_title.text = resources.getQuantityString(R.plurals.artist_plurals, details.getLinks(LinkType.ARTIST).size)
        artist_chips.addChips(requireContext(), details.getLinks(LinkType.ARTIST)) {
            // TODO: Nice extension function for this?
            PropertyDialog.newInstance(it.value, it.id, PropertyType.ARTIST).show(parentFragmentManager, null)
        }

        publisher_root.visibleIf { details.hasLink(LinkType.PUBLISHER) && AppPreferences.isPublisherVisible }
        publisher_title.text = resources.getQuantityString(R.plurals.publisher_plurals, details.getLinks(LinkType.PUBLISHER).size)
        publisher_chips.addChips(requireContext(), details.getLinks(LinkType.PUBLISHER)) {
            // TODO: Nice extension function for this?
            PropertyDialog.newInstance(it.value, it.id, PropertyType.PUBLISHER).show(parentFragmentManager, null)
        }

        information_root.visible()
//        information_root.animateToVisible()
    }

    private fun showBoardGameTypeDialog(gameType: GameType) {
        Analytics.logEvent(EVENT_DIALOG_BOARD_GAME_TYPE)
        BottomSheetDialog(requireActivity()).run {
            val typeView = requireActivity().layoutInflater.inflate(R.layout.board_game_type_dialog, null)
            typeView.board_game_type_title.text = getString(gameType.title)
            setContentView(typeView)
            show()
        }
    }

    private fun showWeightDialog() {
        Analytics.logEvent(EVENT_DIALOG_WEIGHT)
        BottomSheetDialog(requireActivity()).run {
            val weightView = requireActivity().layoutInflater.inflate(R.layout.complexity_dialog, null)
            weightView.boardgame_geek_link.underline()
            weightView.boardgame_geek_link.setOnClickListener {
                Analytics.logEvent(EVENT_BOARDGAMEGEEK_LINK_OPEN)
                startActivity(Intent(Intent.ACTION_VIEW, BOARDGAME_WEIGHT_URL.toUri()))
            }
            setContentView(weightView)
            show()
        }
    }

    private fun setFavoriteImage(isFavorite: Boolean) {
        val favoriteImage = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_not_favorite
        favorite_image.setImageDrawableCompat(favoriteImage)
    }
}