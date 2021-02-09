package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "items")
data class FamilyResponse(

    @Element(name = "item")
    var boardgameFamily: BggBoardGameFamily,

    @Attribute(name = "termsofuse")
    var termsOfUse: String)
