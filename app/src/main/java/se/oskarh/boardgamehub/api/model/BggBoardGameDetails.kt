package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml(name = "item")
data class BggBoardGameDetails(

    @Attribute(name = "id")
    var id: Int,

    @Element(name = "name")
    var names: List<Name>,

    @PropertyElement(name = "description")
    var description: String,

    @PropertyElement(name = "image")
    var image: String?,

    @PropertyElement(name = "thumbnail")
    var thumbnail: String?,

    @Attribute(name = "type")
    var type: String,

    @Path("yearpublished")
    @Attribute(name = "value")
    var yearPublished: Int,

    @Path("minplayers")
    @Attribute(name = "value")
    var minPlayers: Int,

    @Path("maxplayers")
    @Attribute(name = "value")
    var maxPlayers: Int,

    @Path("minage")
    @Attribute(name = "value")
    var minimumAge: Int,

    @Path("playingtime")
    @Attribute(name = "value")
    var playingTime: Int,

    @Path("minplaytime")
    @Attribute(name = "value")
    var minPlayTime: Int,

    @Path("maxplaytime")
    @Attribute(name = "value")
    var maxPlayTime: Int,

    @Element(name = "poll")
    var polls: List<Poll>,

    @Element(name = "statistics")
    val statistics: BggStatistics?,

    @Element
    var comments: BggBoardGameComments?,

    @Element(name = "link")
    var links: List<BggLink>,

    @Element(name = "videos")
    var videos: BggVideos?) {

    fun boardGameComments() =
        // TODO: Fix comments properly
//        comments?.boardGameComments
        comments?.boardGameComments?.map { it.toBoardGameComment() } ?: emptyList()

    // TODO: Remove isShown
    fun toBoardGame(): BoardGame {
        return BoardGame(
            id,
            names.map { it.toName() },
            GameType.from(type),
            yearPublished,
            true,
            description,
            image,
            thumbnail,
            minPlayers,
            maxPlayers,
            minimumAge,
            playingTime,
            minPlayTime,
            maxPlayTime,
            statistics?.toStatistics(),
            videos?.videos?.map { it.toVideo() } ?: emptyList(),
            comments?.boardGameComments?.map { it.toBoardGameComment() } ?: emptyList(),
            links.mapNotNull { it.toLink() },
            polls.mapNotNull { it.toPoll() }
        )
    }
}