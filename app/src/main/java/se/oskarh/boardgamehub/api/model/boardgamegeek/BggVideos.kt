package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "videos")
data class BggVideos(
    @Attribute(name = "total")
    var total: Int,

    @Element(name = "video")
    var videos: List<BggVideo>?)
