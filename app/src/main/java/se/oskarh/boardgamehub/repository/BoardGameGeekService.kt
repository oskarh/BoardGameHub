package se.oskarh.boardgamehub.repository

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import se.oskarh.boardgamehub.api.model.BoardGameDetailsListResponse
import se.oskarh.boardgamehub.api.model.BoardGameDetailsResponse
import se.oskarh.boardgamehub.api.model.CollectionSuccessfulResponse
import se.oskarh.boardgamehub.api.model.FamilyResponse
import se.oskarh.boardgamehub.api.model.GetForumResponse
import se.oskarh.boardgamehub.api.model.HotBoardGames
import se.oskarh.boardgamehub.api.model.ListForumsResponse
import se.oskarh.boardgamehub.api.model.PeopleResponse
import se.oskarh.boardgamehub.api.model.PublisherResponse
import se.oskarh.boardgamehub.api.model.SearchResponse
import se.oskarh.boardgamehub.api.model.ThreadDetailsResponse
import se.oskarh.boardgamehub.util.ARTIST
import se.oskarh.boardgamehub.util.ARTIST_ID
import se.oskarh.boardgamehub.util.BOARDGAME
import se.oskarh.boardgamehub.util.BRIEF
import se.oskarh.boardgamehub.util.COLLECTION
import se.oskarh.boardgamehub.util.COMMENTS
import se.oskarh.boardgamehub.util.DESIGNER
import se.oskarh.boardgamehub.util.DESIGNER_ID
import se.oskarh.boardgamehub.util.DOMAIN
import se.oskarh.boardgamehub.util.DO_NOT_INCLUDE_DATA
import se.oskarh.boardgamehub.util.EXACT
import se.oskarh.boardgamehub.util.FAMILY
import se.oskarh.boardgamehub.util.GET_FORUM
import se.oskarh.boardgamehub.util.HOT
import se.oskarh.boardgamehub.util.ID
import se.oskarh.boardgamehub.util.INCLUDE_DATA
import se.oskarh.boardgamehub.util.LIST_FORUMS
import se.oskarh.boardgamehub.util.PUBLISHER
import se.oskarh.boardgamehub.util.PUBLISHER_ID
import se.oskarh.boardgamehub.util.QUERY
import se.oskarh.boardgamehub.util.SEARCH
import se.oskarh.boardgamehub.util.STATISTICS
import se.oskarh.boardgamehub.util.STATS
import se.oskarh.boardgamehub.util.SUBTYPE
import se.oskarh.boardgamehub.util.THING
import se.oskarh.boardgamehub.util.THREAD
import se.oskarh.boardgamehub.util.TYPE
import se.oskarh.boardgamehub.util.USERNAME
import se.oskarh.boardgamehub.util.VERSION
import se.oskarh.boardgamehub.util.VIDEOS

interface BoardGameGeekService {

    @GET(THING)
    fun getGame(
        @Query(ID) id: Int,
        @Query(STATISTICS) statistics: String = INCLUDE_DATA,
        // TODO: Fix this so I load videos lazily and don't always include them here
        @Query(VIDEOS) videos: String = INCLUDE_DATA,
        @Query(COMMENTS) comments: String = DO_NOT_INCLUDE_DATA) : LiveData<ApiResponse<BoardGameDetailsResponse>>

    @GET(THING)
    fun getGames(
        @Query(ID) ids: String,
        @Query(STATISTICS) statistics: String = INCLUDE_DATA,
        // TODO: Fix this so I load videos lazily and don't always include them here
        @Query(VIDEOS) videos: String = INCLUDE_DATA,
        @Query(COMMENTS) comments: String = DO_NOT_INCLUDE_DATA) : LiveData<ApiResponse<BoardGameDetailsListResponse>>

    @GET(SEARCH)
    fun searchGames(
        @Query(QUERY) query: String,
        @Query(EXACT) exact: String,
        @Query(TYPE) type: String) : LiveData<ApiResponse<SearchResponse>>

    @GET(HOT)
    fun hotGames(@Query(TYPE) type: String): LiveData<ApiResponse<HotBoardGames>>

    @GET(ARTIST)
    fun getArtist(@Path(ARTIST_ID) artistId: Int): LiveData<ApiResponse<PeopleResponse>>

    @GET(DESIGNER)
    fun getDesigner(@Path(DESIGNER_ID) designerId: Int): LiveData<ApiResponse<PeopleResponse>>

    @GET(PUBLISHER)
    fun getPublisher(@Path(PUBLISHER_ID) publisherId: Int): LiveData<ApiResponse<PublisherResponse>>

    @GET(FAMILY)
    fun getFamily(@Query(ID) id: Int) : LiveData<ApiResponse<FamilyResponse>>

    @GET(COLLECTION)
    fun getCollection(
        @Query(USERNAME) username: String,
        @Query(VERSION) version: String = DO_NOT_INCLUDE_DATA,
        @Query(STATS) stats: String = DO_NOT_INCLUDE_DATA,
        @Query(BRIEF) brief: String = INCLUDE_DATA,
        @Query(DOMAIN) domain: String = BOARDGAME,
        @Query(SUBTYPE) subtype: String = BOARDGAME) : Call<CollectionSuccessfulResponse>

    @GET(LIST_FORUMS)
    fun listForums(@Query(ID) id: Int, @Query(TYPE) type: String = "thing") : LiveData<ApiResponse<ListForumsResponse>>

    @GET(GET_FORUM)
    fun getForum(@Query(ID) id: Int) : LiveData<ApiResponse<GetForumResponse>>

    @GET(THREAD)
    fun getThread(@Query(ID) id: Int) : LiveData<ApiResponse<ThreadDetailsResponse>>
}
