package se.oskarh.boardgamehub.repository

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.Reusable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.api.TopCategory
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.boardgame.RankedBoardGame
import se.oskarh.boardgamehub.util.TOP_GAMES_TIMEOUT
import se.oskarh.boardgamehub.util.extension.extractInteger
import timber.log.Timber
import javax.inject.Inject

// TODO: Save to db, use cached result if less than ~12h old
@Reusable
class TopGamesRepository @Inject constructor(
    private val finderRepository: FinderRepository) {

    @WorkerThread
    fun findTopGames(topCategory: TopCategory): LiveData<ApiResponse<List<RankedBoardGame>>> {
        return MutableLiveData<ApiResponse<List<RankedBoardGame>>>().apply {
            postValue(LoadingResponse())
            try {
                val topGames = scrapeTopGames(topCategory)
                if (topGames.isNotEmpty()) {
                    GlobalScope.launch(Dispatchers.IO) {

                        val cachedGames = finderRepository.prefetchTopListGames(topGames.map { it.boardGame.id })
                        val cachedTopGames = topGames.map { rankedBoardGame ->
                            cachedGames.firstOrNull { cachedGame ->
                                cachedGame.id == rankedBoardGame.boardGame.id
                            }?.let { cachedGame ->
                                Timber.d("Found cached top game ${cachedGame.primaryName()}")
                                RankedBoardGame(cachedGame, rankedBoardGame.rank)
                            } ?: rankedBoardGame
                        }
                        Timber.d("Top games returned size ${cachedTopGames.size}")
                        postValue(SuccessResponse(cachedTopGames))
                    }
                } else {
                    Timber.d("Empty top categories...")
                    postValue(EmptyResponse())
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse top games")
                postValue(ErrorResponse(Log.getStackTraceString(e)))
            }
        }
    }

    private fun scrapeTopGames(topCategory: TopCategory): List<RankedBoardGame> {
        return Jsoup
            .connect(topCategory.url)
            .timeout(TOP_GAMES_TIMEOUT)
            .get()
            .select("tr#row_")
            .mapIndexedNotNull { index, element ->
                val link = element.getElementsByClass("collection_thumbnail").first().getElementsByTag("a").first()
                extractGameId(link.attr("href"))?.let { gameId ->
                    val imageUrl = link.child(0).attr("src")
                    val boardGameData = element.getElementsByClass("collection_objectname")
                        .first()
                        .child(1)
                    val title = boardGameData.getElementsByTag("a").text()
                    val yearPublished = boardGameData.getElementsByTag("span")
                        .text()
                        .trim()
                        .removeSurrounding("(", ")")
                        .toInt()
                    RankedBoardGame(
                        BoardGame(gameId, title, GameType.BOARDGAME, yearPublished, true, imageUrl = imageUrl),
                        index + 1
                    )
                }
            }.toList()
    }

    private fun extractGameId(link: String): Int? =
        link.extractInteger("/boardgame/", "/") ?: link.extractInteger("/boardgameexpansion/", "/")
}
