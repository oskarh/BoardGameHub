package se.oskarh.boardgamehub.repository

import android.content.Context
import dagger.Reusable
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.api.model.boardgamegeek.BoardGameDetailsResponse
import se.oskarh.boardgamehub.api.model.boardgamegeek.HotBoardGames
import se.oskarh.boardgamehub.api.model.boardgamegeek.PeopleResponse
import se.oskarh.boardgamehub.api.model.boardgamegeek.PublisherResponse
import se.oskarh.boardgamehub.api.model.boardgamegeek.SearchResponse
import se.oskarh.boardgamehub.util.extension.xmlToClass
import javax.inject.Inject

@Reusable
class MockedRepository @Inject constructor(private val context: Context) {

    fun searchGames() = context.xmlToClass<SearchResponse>(R.raw.mocked_search)

    fun gameDetails() = context.xmlToClass<BoardGameDetailsResponse>(R.raw.mocked_details)

    fun hotGames() = context.xmlToClass<HotBoardGames>(R.raw.mocked_hot)

    fun findArtist() = context.xmlToClass<PeopleResponse>(R.raw.mocked_designer)

    fun findDesigner() = context.xmlToClass<PeopleResponse>(R.raw.mocked_designer)

    fun findPublisher() = context.xmlToClass<PublisherResponse>(R.raw.mocked_publisher)
}