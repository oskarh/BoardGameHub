package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "items")
data class BoardGameDetailsListResponse(

    @Element(name = "item")
    var gameDetails: List<BggBoardGameDetails>)
