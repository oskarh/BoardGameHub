package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml(name = "rank")
data class Rank(

    // Return as double
    @Attribute(name = "bayesaverage")
    var bayesAverage: String,

    @Attribute(name = "friendlyname")
    var friendlyName: String,

    @Attribute(name = "id")
    var id: Int,

    @Attribute(name = "name")
    var name: String,

    @Attribute(name = "type")
    var type: String,

    // Return as int
    @Attribute(name = "value")
    var value: String) {

    fun toRank() =
        BoardGame.Rank(type, id, name, friendlyName, value, bayesAverage)
}
