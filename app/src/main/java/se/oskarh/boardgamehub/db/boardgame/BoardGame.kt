package se.oskarh.boardgamehub.db.boardgame

import android.os.Parcelable
import androidx.core.net.toUri
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import kotlinx.android.parcel.Parcelize
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.api.LinkType
import se.oskarh.boardgamehub.api.PollType
import se.oskarh.boardgamehub.api.RatingSource
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.CACHE_AGING_BOARDGAMES
import se.oskarh.boardgamehub.util.CACHE_NEW_BOARDGAMES
import se.oskarh.boardgamehub.util.CACHE_OLD_BOARDGAMES
import se.oskarh.boardgamehub.util.COULD_NOT_PARSE_DEFAULT
import se.oskarh.boardgamehub.util.MAXIMUM_VISIBLE_EXPANSIONS
import se.oskarh.boardgamehub.util.PRIMARY
import se.oskarh.boardgamehub.util.UNCATEGORIZED_BOARDGAME
import se.oskarh.boardgamehub.util.YOUTUBE_AUTHORITY
import se.oskarh.boardgamehub.util.extension.formatRating
import se.oskarh.boardgamehub.util.extension.normalize
import se.oskarh.boardgamehub.util.extension.primaryName
import timber.log.Timber
import java.util.Calendar
import java.util.Locale
import kotlin.math.max

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Parcelize
@Entity(indices = [Index("id"), Index("lastViewedTimestamp")], tableName = "boardgame_table")
data class BoardGame(
    @PrimaryKey
    var id: Int,
    var names: List<Name>,
    var type: GameType,
    var yearPublished: Int,
    // TODO: Can I refactor out isShown to a better solution?
    val isShown: Boolean,
    var description: String? = null,
    var imageUrl: String? = null,
    var thumbnailUrl: String? = null,
    var minPlayers: Int? = null,
    var maxPlayers: Int? = null,
    var minAge: Int? = null,
    var playingTime: Int? = null,
    var minPlayTime: Int? = null,
    var maxPlayTime: Int? = null,
    var statistics: Statistics? = null,
    var videos: List<Video> = emptyList(),
    var comments: List<Comment> = emptyList(),
    var links: List<Link> = emptyList(),
    var polls: List<Poll> = emptyList(),
    var normalizedName: String = names.primaryName().normalize(),
    var lastViewedTimestamp: Long = 0,
    var created: Long = System.currentTimeMillis(),
    var lastSync: Long = System.currentTimeMillis()
) : Parcelable {

    constructor(id: Int,
                name: String,
                type: GameType,
                yearPublished: Int,
                isShown: Boolean,
                description: String? = null,
                imageUrl: String? = null,
                thumbnailUrl: String? = null) :
            this(id, listOf(Name(PRIMARY, name)), type, yearPublished, isShown, description, imageUrl, thumbnailUrl)

    fun toLiteBoardGame() = BoardGame(id, names, type, yearPublished, isShown)

    fun isCacheStale(currentTimestamp: Long = System.currentTimeMillis()) =
        currentTimestamp - (lastSync + cacheDuration()) > 0

    private fun cacheDuration(): Long {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return when {
            yearPublished >= currentYear -> CACHE_NEW_BOARDGAMES
            yearPublished >= currentYear - 5 -> CACHE_AGING_BOARDGAMES
            else -> CACHE_OLD_BOARDGAMES
        }
    }

    fun primaryName() = names.primaryName()

    fun hasLink(linkType: LinkType) = getLinks(linkType).isNotEmpty()

    fun getLinks(linkType: LinkType) =
        links.filter { link ->
            link.linkType == linkType
        }.sortedBy { link ->
            link.value
        }

    fun getPoll(pollType: PollType) =
        polls.firstOrNull { poll ->
            poll.pollType == pollType
        }

    fun getVisibleExpansions() = getLinks(LinkType.EXPANSION).take(MAXIMUM_VISIBLE_EXPANSIONS)

    fun getNonVisibleExpansions() = getLinks(LinkType.EXPANSION).drop(MAXIMUM_VISIBLE_EXPANSIONS)

    private fun hasFetchedComments() = statistics?.numberOfComments == comments.size

    fun hasComments() = hasFetchedComments() && comments.isNotEmpty()

    fun hasNoComments() = hasFetchedComments() && comments.isEmpty()

    fun boardGameTypes(): List<Rank> =
        statistics?.ranks?.filterNot {
            it.id == 1
        }?.ifEmpty {
            UNCATEGORIZED_BOARDGAME
        }?.sortedBy {
            it.friendlyname
        } ?: UNCATEGORIZED_BOARDGAME

    fun minimumAgeFormatted() = if (minAge != 0) "$minAge+" else COULD_NOT_PARSE_DEFAULT

    fun formattedRating() = statistics?.formattedRating() ?: COULD_NOT_PARSE_DEFAULT

    fun parsedUserRating() = statistics?.userAverage?.toFloatOrNull() ?: 0f

    fun parsedGeekRating() = statistics?.bayesAverage?.toFloatOrNull() ?: 0f

    fun parsedComplexity() = statistics?.complexity?.toFloatOrNull() ?: Float.MAX_VALUE

    fun parsedMinimumAge() = minAge.takeUnless { minimumAge ->
        minimumAge == null || minimumAge <= 0
    } ?: Int.MAX_VALUE

    fun recommendedForPlayersAverage(playerCount: Int): Float {
        val recommendations: Results? = getPoll(PollType.SUGGESTED_NUMBER_OF_PLAYERS)?.results?.firstOrNull { it.numberOfPlayers == playerCount.toString() }
        val recommendationSum = recommendations?.results?.sumBy { result ->
            Timber.d("Recommendations 2 players for ${primaryName()} is ${result.value} | ${result.numberOfVotes} | ${result.recommendationValue()}")
            result.recommendationValue()
        } ?: 0
        val totalNumberVotes = max(8, recommendations?.results?.sumBy {
            it.numberOfVotes
        }.takeUnless { it == 0 } ?: 1)
        Timber.d("Recommendation value for ${primaryName()} | ${recommendationSum} | ${totalNumberVotes} | average = ${recommendationSum.toFloat() / totalNumberVotes}")
        return recommendationSum.toFloat() / totalNumberVotes
    }

    fun playersFormatted() =
        if (minPlayers == maxPlayers) {
            minPlayers.takeUnless { it == null || it == 0 }?.toString() ?: COULD_NOT_PARSE_DEFAULT
        } else {
            "$minPlayers - $maxPlayers"
        }

    fun playingTimeFormatted() =
        if (minPlayTime == maxPlayTime) {
            minPlayTime.takeUnless { it == null || it == 0 }?.toString() ?: COULD_NOT_PARSE_DEFAULT
        } else {
            "$minPlayTime - $maxPlayTime"
        }

    fun hasStatistics() = statistics != null

    fun hasPlayers() = playersFormatted() != COULD_NOT_PARSE_DEFAULT

    fun hasPlayingTime() = playingTimeFormatted() != COULD_NOT_PARSE_DEFAULT

    fun playerRange(): IntRange {
        val startPlayerRange = minPlayers ?: maxPlayers ?: 0
        val endPlayerRange = maxPlayers ?: minPlayers ?: 0
        return startPlayerRange..endPlayerRange
    }

    @Parcelize
    data class Comment(
        val rating: String,
        val username: String,
        val text: String
    ) : Parcelable

    @Parcelize
    data class Video(
        @PrimaryKey
        val id: Int,
        var title: String,
        var category: String,
        var language: String,
        var link: String,
        var username: String,
        var userId: String,
        var postDate: String
    ) : Parcelable {

        fun youTubeId(): String? =
            if (isYouTubeVideo(link)) {
                link.toUri().getQueryParameter("v")
            } else {
                null
            }

        private fun isYouTubeVideo(url: String) = url.toUri().authority.equals(YOUTUBE_AUTHORITY, true)
    }

    @Parcelize
    data class Statistics(
        var userAverage: String,
        var complexity: String,
        var bayesAverage: String,
        var numberOfComments: Int,
        var median: String,
        var standardDeviation: String,
        var usersRated: Int,
        var numberOfWeights: Int,
        var owned: Int,
        var trading: Int,
        var wanting: Int,
        var wishing: Int,
        var ranks: List<Rank>
    ) : Parcelable {

        fun formattedRating(): String =
            when (AppPreferences.selectedRating) {
                RatingSource.USER_AVERAGE_0_VOTES -> userAverage
                RatingSource.USER_AVERAGE_10_VOTES -> userAverage.takeIf { usersRated > 10 }
                RatingSource.USER_AVERAGE_100_VOTES -> userAverage.takeIf { usersRated > 100 }
                RatingSource.GEEK_RATING -> bayesAverage
            }.formatRating()
    }

    @Parcelize
    data class Name(
        var type: String,
        var value: String
    ) : Parcelable

    @Parcelize
    data class Rank(
        var rankType: String,
        var id: Int,
        var name: String,
        var friendlyname: String,
        var value: String,
        var bayesaverage: String
    ) : Parcelable

    @Parcelize
    data class Link(
        var linkType: LinkType,
        var id: Int,
        var value: String
    ) : Parcelable

    @Parcelize
    data class Poll(
        var pollType: PollType,
        var results: List<Results>,
        var title: String,
        var totalVotes: Int
    ) : Parcelable

    @Parcelize
    data class Results(
        var numberOfPlayers: String?,
        var results: List<Result>
    ) : Parcelable

    @Parcelize
    data class Result(
        var level: Int?,
        var numberOfVotes: Int,
        var value: String
    ) : Parcelable {

        fun recommendationValue() = numberOfVotes * when(value.trim().toLowerCase(Locale.ENGLISH)) {
            "recommended" -> 3
            "best" -> 5
            "not recommended" -> -4
            else -> 0
        }
    }
}