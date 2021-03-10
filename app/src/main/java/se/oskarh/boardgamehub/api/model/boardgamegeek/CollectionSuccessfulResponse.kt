package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "items")
data class CollectionSuccessfulResponse(

    @Attribute(name = "totalitems")
    var totalitems: Int?,

    @Attribute(name = "pubdate")
    var pubdate: String?,

    @Element(name = "item")
    var boardGames: List<CollectionBoardGame>?) {

    fun collectionBoardGames() = boardGames.orEmpty().map { it.toBoardGameCollectionItem() }
}