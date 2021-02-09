package se.oskarh.boardgamehub.repository

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import dagger.Reusable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import se.oskarh.boardgamehub.db.boardgamemeta.BoardGameProperty
import se.oskarh.boardgamehub.db.boardgamemeta.BoardGamePropertyDao
import se.oskarh.boardgamehub.db.boardgamemeta.BoardGamePropertyRepository
import se.oskarh.boardgamehub.db.boardgamemeta.PropertyType
import se.oskarh.boardgamehub.util.TOP_GAMES_TIMEOUT
import se.oskarh.boardgamehub.util.extension.mapResponse
import timber.log.Timber
import javax.inject.Inject

@Reusable
class PropertyRepository @Inject constructor(
    private val boardGameGeekService: BoardGameGeekService,
    private val propertyDao: BoardGamePropertyDao,
    private val propertyRepository: BoardGamePropertyRepository) {

    // TODO: Rename
    fun findNewProperty(id: Int, propertyType: PropertyType): LiveData<ApiResponse<BoardGameProperty>> {
        return MediatorLiveData<ApiResponse<BoardGameProperty>>().apply {
            value = LoadingResponse()
            GlobalScope.launch(Dispatchers.IO) {
                val cachedGame: BoardGameProperty? = propertyRepository.getProperty(id, propertyType)
                if (cachedGame != null) {
                    Timber.d("Could find property details in cache, full info ${cachedGame.name}")
                    postValue(SuccessResponse(cachedGame))
                } else {
                    Timber.d("Could find NOT property details in cache $id, in flight requests now contain")
                    launch(Dispatchers.Main) {
                        addSource(findMyProperty(id, propertyType)) { apiResponse ->
                            Timber.d("Got returned details")
                            value = apiResponse
//                            value = apiResponse.mapResponse({ it.toPropertyData() })
                            if (apiResponse is SuccessResponse) {
                                GlobalScope.launch(Dispatchers.IO) {
                                    propertyDao.insertOrUpdate(apiResponse.data)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // TODO: Rename
    private fun findMyProperty(id: Int, propertyType: PropertyType): LiveData<ApiResponse<BoardGameProperty>> {
        return when (propertyType) {
            PropertyType.ARTIST -> boardGameGeekService.getArtist(id).map {
                it.mapResponse({
                    BoardGameProperty(id, it.person.name, it.person.description, propertyType)
                })
            }
            PropertyType.DESIGNER -> boardGameGeekService.getDesigner(id).map {
                it.mapResponse({
                    BoardGameProperty(id, it.person.name, it.person.description, propertyType)
                })
            }
            PropertyType.PUBLISHER -> boardGameGeekService.getPublisher(id).map {
                it.mapResponse({
                    BoardGameProperty(id, it.company.name, it.company.description, propertyType)
                })
            }
            PropertyType.BOARD_GAME_MECHANIC, PropertyType.BOARD_GAME_CATEGORY, PropertyType.TYPE ->
                liveData(Dispatchers.IO) {
                    emitSource(findProperty(id, propertyType))
                }
        }
    }

    @WorkerThread
    fun findProperty(categoryId: Int, propertyType: PropertyType): LiveData<ApiResponse<BoardGameProperty>> {
        return MutableLiveData<ApiResponse<BoardGameProperty>>().apply {
            postValue(LoadingResponse())

            try {
                val property = scrapeBoardGameProperty(categoryId, propertyType)
                Timber.d("Scraped category [$property]")
                postValue(SuccessResponse(property))

            } catch (e: Exception) {
                Timber.e(e, "Failed to parse category $categoryId")
                postValue(ErrorResponse(Log.getStackTraceString(e)))
            }
        }
    }

    private fun scrapeBoardGameProperty(propertyId: Int, propertyType: PropertyType): BoardGameProperty {
        val categoryDocument = Jsoup
            .connect(propertyType.toBoardGameGeekUrl(propertyId))
            .timeout(TOP_GAMES_TIMEOUT)
            .get()

        val name = categoryDocument
            .select("div#module_2")
            .select("div.geekitem_name")
            .text()

        val description = categoryDocument.select("div#editdesc").html()
        return BoardGameProperty(propertyId, name, description, propertyType)
//        return BoardGamePropertyData(name, description)
    }
}
