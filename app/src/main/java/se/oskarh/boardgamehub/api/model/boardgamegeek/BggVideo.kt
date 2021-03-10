package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml(name = "video")
data class BggVideo(
    @Attribute(name = "id")
    var id: Int,

    @Attribute(name = "title")
    var title: String,

    @Attribute(name = "category")
    var category: String,

    @Attribute(name = "language")
    var language: String,

    @Attribute(name = "link")
    var link: String,

    @Attribute(name = "username")
    var username: String,

    @Attribute(name = "userid")
    var userId: String,

    @Attribute(name = "postdate")
    var postDate: String) {

    fun toVideo() = BoardGame.Video(id, title, category, language, link, username, userId, postDate)
}
