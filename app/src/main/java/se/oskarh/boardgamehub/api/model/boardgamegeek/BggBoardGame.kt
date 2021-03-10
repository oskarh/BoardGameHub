package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.normalize

@Xml(name = "item")
data class BggBoardGame(

    @Attribute(name = "id")
    var id: Int,

    @Path("name")
    @Attribute(name = "value")
    var name: String,

    @Path("name")
    @Attribute(name = "type")
    // TODO: Rename
    var nameType: String,

    @Attribute(name = "type")
    var itemType: String,

    @Path("yearpublished")
    @Attribute(name = "value")
    var yearPublished: Int
) {

    var isShown = true

    fun normalizedName() = name.normalize()

    fun gameTypes() = itemType.split(", ")
        .map { GameType.from(it) }
        .toSet()

    fun toBoardGame() = BoardGame(id, listOf(BoardGame.Name(nameType, name)), GameType.from(itemType), yearPublished, isShown)
}
