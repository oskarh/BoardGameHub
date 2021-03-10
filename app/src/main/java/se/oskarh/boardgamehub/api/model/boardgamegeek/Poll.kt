package se.oskarh.boardgamehub.api.model.boardgamegeek

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.api.PollType
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml(name = "poll")
data class Poll(
    @Attribute(name = "name")
    var name: String,

    @Element(name = "results")
    var results: List<Results>?,

    @Attribute(name = "title")
    var title: String,

    @Attribute(name = "totalvotes")
    var totalVotes: Int) {

    fun toPoll() = PollType.toPollTypeOrNull(name)?.let { pollType ->
        BoardGame.Poll(pollType, results?.map { it.toResults() }.orEmpty(), title, totalVotes)
    }
}

