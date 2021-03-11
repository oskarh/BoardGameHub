package se.oskarh.boardgamehub.repository

import android.util.Log
import androidx.annotation.WorkerThread
import dagger.Reusable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.api.TopCategory
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.boardgame.RankedBoardGame
import se.oskarh.boardgamehub.util.PUBLICATION_YEAR_UNKNOWN
import se.oskarh.boardgamehub.util.TOP_GAMES_TIMEOUT
import se.oskarh.boardgamehub.util.extension.extractInteger
import timber.log.Timber
import javax.inject.Inject

// TODO: Save to db, use cached result if less than ~12h old
@Reusable
class TopGamesRepository @Inject constructor(private val finderRepository: FinderRepository) {

    private val topBoardGames = MutableStateFlow<ApiResponse<List<RankedBoardGame>>>(LoadingResponse())

    @WorkerThread
    suspend fun findTopGames(topCategory: TopCategory): StateFlow<ApiResponse<List<RankedBoardGame>>> {
        topBoardGames.emit(LoadingResponse())
        try {
            val scrapedTopBoardGames = scrapeTopGames(topCategory)
            if (scrapedTopBoardGames.isNotEmpty()) {
                val cachedBoardGames = finderRepository.prefetchTopListGames(scrapedTopBoardGames.map { it.boardGame.id })
                val cachedTopGames = scrapedTopBoardGames.map { rankedBoardGame ->
                    cachedBoardGames.firstOrNull { cachedGame ->
                        cachedGame.id == rankedBoardGame.boardGame.id
                    }?.let { cachedGame ->
                        Timber.d("Found cached top game ${cachedGame.primaryName()}")
                        RankedBoardGame(cachedGame, rankedBoardGame.rank)
                    } ?: rankedBoardGame
                }
                Timber.d("Top games returned size ${cachedTopGames.size}")
                topBoardGames.emit(SuccessResponse(cachedTopGames))
            } else {
                Timber.d("Empty top categories...")
                topBoardGames.emit(EmptyResponse())
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse top games")
            topBoardGames.emit(ErrorResponse(Log.getStackTraceString(e)))
        }
        return topBoardGames
    }

    private fun scrapeTopGames(topCategory: TopCategory): List<RankedBoardGame> {
        return Jsoup
            .connect(topCategory.url)
            .timeout(TOP_GAMES_TIMEOUT)
            .get()
            .select("tr#row_")
            .mapIndexedNotNull { index, element ->
                val link = element.getElementsByClass("collection_thumbnail").first().getElementsByTag("a").first()
                parseBoardGameId(link.attr("href"))?.let { id ->
                    val imageUrl = parseImageUrl(link)
                    val boardGameData = parseBoardGameData(element)
                    val title = parseBoardGameTitle(boardGameData)
                    val yearPublished = parsePublishedYear(boardGameData.getElementsByTag("span"))

                    RankedBoardGame(
                        BoardGame(id, title, GameType.BOARDGAME, yearPublished, true, imageUrl = imageUrl),
                        index + 1
                    )
                }
            }.toList()
    }

    private fun parseBoardGameTitle(boardGameData: Element) =
        boardGameData.getElementsByTag("a").text()

    private fun parseBoardGameData(element: Element) =
        element.getElementsByClass("collection_objectname")
            .first()
            .child(1)

    private fun parseImageUrl(link: Element) = link.child(0).attr("src")

    private fun parsePublishedYear(elements: Elements): Int =
        elements.firstOrNull()
            ?.text()
            ?.trim()
            ?.removeSurrounding("(", ")")
            ?.toInt() ?: PUBLICATION_YEAR_UNKNOWN

    private fun parseBoardGameId(link: String): Int? =
        link.extractInteger("/boardgame/", "/") ?: link.extractInteger("/boardgameexpansion/", "/")
}
