package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml(name = "comment")
data class BggComment(

    @Attribute(name = "rating")
    var rating: String,

    @Attribute(name = "username")
    var username: String,

    @Attribute(name = "value")
    var text: String) {

    fun toBoardGameComment() = BoardGame.Comment(rating, username, text)
}