package se.oskarh.boardgamehub.api

import androidx.annotation.StringRes
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.api.model.boardgamegeek.BggBoardGame
import se.oskarh.boardgamehub.util.extension.normalize

enum class SortAction(@StringRes val property: Int) {
    RELEVANCE(R.string.relevance),
    TITLE(R.string.title),
    YEAR_PUBLISHED_ASCENDING(R.string.year_published_ascending),
    YEAR_PUBLISHED_DESCENDING(R.string.year_published_descending);

    fun sort(query: String, games: List<BggBoardGame>) =
        when (this) {
            RELEVANCE -> sortByRelevance(query, games)
            TITLE -> games.sortedWith(compareBy<BggBoardGame> { it.normalizedName() }.thenByDescending { it.yearPublished })
            YEAR_PUBLISHED_ASCENDING -> games.sortedWith(compareBy({ it.yearPublished }, { it.normalizedName() }))
            YEAR_PUBLISHED_DESCENDING -> games.sortedWith(compareByDescending<BggBoardGame> { it.yearPublished }.thenBy { it.normalizedName() })
        }

    private fun sortByRelevance(query: String, games: List<BggBoardGame>) =
        games.groupBy {
                relevanceStrength(query.normalize(), it.normalizedName())
            }
            .toSortedMap()
            .map { entry ->
                entry.value.sortedBy { boardGame ->
                    boardGame.normalizedName().length
                }
            }
            .flatten()

    private fun relevanceStrength(query: String, boardGameTitle: String) =
        when {
            boardGameTitle.startsWith(query) -> 0
            boardGameTitle.contains(query) -> 1
            else -> 2
        }

//    fun comparator(): Comparator<BoardGame> =
//            when (this) {
//                TITLE -> compareBy<BoardGame> { it.name }.thenByDescending { it.yearPublished }
//                YEAR_PUBLISHED_ASCENDING -> compareBy({ it.yearPublished }, { it.name })
//                YEAR_PUBLISHED_DESCENDING -> compareByDescending<BoardGame> { it.yearPublished }.thenBy { it.name }
//                // TODO: Implement relevance
//                RELEVANCE -> compareByDescending<BoardGame> { it.yearPublished }.thenBy { it.name }
//            }
}
