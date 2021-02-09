package se.oskarh.boardgamehub.api

import android.content.Context
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.api.model.BggBoardGame
import java.util.Locale

data class FilterAction(
    val areStandaloneGamesIncluded: Boolean,
    val areExpansionsIncluded: Boolean,
    val areAllYearsIncluded: Boolean,
    val startYear: Int,
    val endYear: Int
) {
    private val gameTypesIncluded = mutableSetOf<GameType>().apply {
        if (areStandaloneGamesIncluded) add(GameType.BOARDGAME)
        if (areExpansionsIncluded) add(GameType.BOARDGAME_EXPANSION)
    }

    fun filter(bggBoardGames: List<BggBoardGame>) =
        bggBoardGames.onEach { bggBoardGame: BggBoardGame ->
            bggBoardGame.isShown =
                (areAllYearsIncluded || bggBoardGame.yearPublished in startYear..endYear) &&
                        bggBoardGame.gameTypes().intersect(gameTypesIncluded).isNotEmpty()
        }

    fun toDescription(context: Context): String {
        val includedGames = gameTypeDescription(context)
        val includedYears = publishedYearsDescription(context)
        return context.getString(R.string.filter_description, includedGames, includedYears)
    }

    private fun gameTypeDescription(context: Context) =
        when {
            areStandaloneGamesIncluded && !areExpansionsIncluded -> context.getString(R.string.standalone_boardgames)
            !areStandaloneGamesIncluded && areExpansionsIncluded -> context.getString(R.string.expansions).toLowerCase(Locale.ENGLISH)
            else -> "${context.getString(R.string.standalone_boardgames)} ${context.getString(R.string.and)} ${context.getString(R.string.expansions).toLowerCase(Locale.ENGLISH)}"
        }

    private fun publishedYearsDescription(context: Context) =
        if (areAllYearsIncluded) {
            context.getString(R.string.published_at_any_time)
        } else {
            context.getString(R.string.published_between, startYear, endYear)
        }
}