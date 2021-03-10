package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.boardgame.RankedBoardGame

@Xml(name = "item")
data class BggRankedBoardGame(

    @Attribute(name = "id")
    var id: Int,

    @Attribute(name = "rank")
    var rank: Int,

    @Path("thumbnail")
    @Attribute(name = "value")
    var thumbnail: String?,

    @Path("name")
    @Attribute(name = "value")
    var name: String,

    @Path("yearpublished")
    @Attribute(name = "value")
    var yearPublished: Int) {

    // TODO: Also get type from ranked board game?
    fun toRankedBoardGame() = RankedBoardGame(
        BoardGame(id, name, GameType.BOARDGAME, yearPublished, true, null, null, thumbnail), rank)
}
