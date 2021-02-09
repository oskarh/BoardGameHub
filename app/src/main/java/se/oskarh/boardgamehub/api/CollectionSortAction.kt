package se.oskarh.boardgamehub.api

import androidx.annotation.StringRes
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.db.boardgame.BoardGame

enum class CollectionSortAction(@StringRes val property: Int) {
    TITLE(R.string.title),
    USER_AVERAGE(R.string.user_average_0_votes),
    BOARDGAMEGEEK_AVERAGE(R.string.geek_rating),
    COMPLEXITY(R.string.complexity),
    PUBLISHED_YEAR_ASCENDING(R.string.year_published_ascending),
    PUBLISHED_YEAR_DESCENDING(R.string.year_published_descending),
    MINIMUM_AGE(R.string.minimum_age),
    PLAY_TIME(R.string.play_time),
    VOTED_BEST(R.string.voted_best);

    fun sort(votedBestPlayerCount: Int, games: List<BoardGame>) = when (this) {
        TITLE -> games.sortedBy { it.normalizedName }
        USER_AVERAGE -> games.sortedWith(compareByDescending<BoardGame> { it.parsedUserRating() }.thenBy { it.normalizedName })
        BOARDGAMEGEEK_AVERAGE -> games.sortedWith(compareByDescending<BoardGame> { it.parsedGeekRating() }.thenBy { it.normalizedName })
        COMPLEXITY -> games.sortedWith(compareBy<BoardGame> { it.parsedComplexity() }.thenBy { it.normalizedName })
        PUBLISHED_YEAR_ASCENDING -> games.sortedWith(compareBy<BoardGame> { it.yearPublished }.thenBy { it.normalizedName })
        PUBLISHED_YEAR_DESCENDING -> games.sortedWith(compareByDescending<BoardGame> { it.yearPublished }.thenBy { it.normalizedName })
        MINIMUM_AGE -> games.sortedWith(compareBy<BoardGame> { it.parsedMinimumAge() }.thenBy { it.normalizedName })
        PLAY_TIME -> games.sortedWith(compareBy<BoardGame> { boardGame ->
            boardGame.playingTime.takeUnless { playingTime ->
                playingTime == null || playingTime <= 0
            } ?: Int.MAX_VALUE
        }.thenBy { it.normalizedName })
        VOTED_BEST -> games.sortedWith(compareByDescending<BoardGame> { it.recommendedForPlayersAverage(votedBestPlayerCount) }.thenBy { it.normalizedName })
    }
}
