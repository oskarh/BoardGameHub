package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml(name = "results")
data class Results(

    @Attribute(name = "numplayers")
    var numberOfPlayers: String?,

    @Element(name = "result")
    var result: List<Result>?) {

    fun toResults() =
            BoardGame.Results(numberOfPlayers, result?.map { it.toResult() }.orEmpty())
}