package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml(name = "result")
data class Result(
    @Attribute(name = "level")
    var level: Int?,

    @Attribute(name = "numvotes")
    var numberOfVotes: Int,

    @Attribute(name = "value")
    var value: String) {

    fun toResult() =
        BoardGame.Result(level, numberOfVotes, value)
}
