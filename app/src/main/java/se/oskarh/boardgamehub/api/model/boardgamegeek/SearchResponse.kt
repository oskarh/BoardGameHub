package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.api.FilterAction
import se.oskarh.boardgamehub.api.SortAction

@Xml(name = "items")
data class SearchResponse(

    @Element(name = "item")
    var searchedGames: List<BggBoardGame>?,

    @Attribute(name = "total")
    var total: Int
) {
    val games = searchedGames ?: emptyList()

    fun filterBy(filterAction: FilterAction) = copy(searchedGames = filterAction.filter(games))

    fun sortBy(query: String, sortAction: SortAction) = copy(searchedGames = sortAction.sort(query, games))
}