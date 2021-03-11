package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "items")
data class HotBoardGames(

    @Element(name = "item")
    var games: List<BggRankedBoardGame>,

    // TODO: Remove all these unnecessary attributes we don't use?
    @Attribute(name = "termsofuse")
    var termsOfUse: String)
