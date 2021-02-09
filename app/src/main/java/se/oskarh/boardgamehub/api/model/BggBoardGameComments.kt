package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "comments")
data class BggBoardGameComments(
    @Element(name = "comment")
    var boardGameComments: List<BggComment>,

    @Attribute(name = "page")
    var page: Int,

    @Attribute(name = "totalitems")
    var totalComments: Int)
