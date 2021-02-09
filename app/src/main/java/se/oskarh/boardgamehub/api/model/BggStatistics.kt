package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Xml
data class BggStatistics(
    @Path("ratings/average")
    @Attribute(name = "value")
    var average: String,

    @Path("ratings/averageweight")
    @Attribute(name = "value")
    var complexity: String,

    @Path("ratings/bayesaverage")
    @Attribute(name = "value")
    var bayesAverage: String,

    @Path("ratings/numcomments")
    @Attribute(name = "value")
    var numberOfComments: Int,

    @Path("ratings/median")
    @Attribute(name = "value")
    var median: String,

    @Path("ratings/owned")
    @Attribute(name = "value")
    var owned: Int,

    @Attribute(name = "page")
    var page: Int,

    @Path("ratings/ranks")
    @Element
    var ranks: List<Rank>,

    @Path("ratings/stddev")
    @Attribute(name = "value")
    var standardDeviation: String,

    @Path("ratings/trading")
    @Attribute(name = "value")
    var trading: Int,

    @Path("ratings/usersrated")
    @Attribute(name = "value")
    var usersRated: Int,

    @Path("ratings/wanting")
    @Attribute(name = "value")
    var wantingCount: Int,

    @Path("ratings/numweights")
    @Attribute(name = "value")
    var numberOfWeights: Int,

    @Path("ratings/wishing")
    @Attribute(name = "value")
    var wishingCount: Int) {

    fun toStatistics() = BoardGame.Statistics(
        average,
        complexity,
        bayesAverage,
        numberOfComments,
        median,
        standardDeviation,
        usersRated,
        numberOfWeights,
        owned,
        trading,
        wantingCount,
        wishingCount,
        ranks.map { it.toRank() }
    )
}