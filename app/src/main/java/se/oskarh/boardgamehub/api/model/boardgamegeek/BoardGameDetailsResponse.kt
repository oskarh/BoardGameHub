package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "items")
data class BoardGameDetailsResponse(

    @Element(name = "item")
    var gameDetails: BggBoardGameDetails,

    @Attribute(name = "termsofuse")
    var termsOfUse: String)
