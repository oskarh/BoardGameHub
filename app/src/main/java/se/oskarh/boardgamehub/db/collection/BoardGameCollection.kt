package se.oskarh.boardgamehub.db.collection

import androidx.room.Entity
import androidx.room.PrimaryKey
import se.oskarh.boardgamehub.api.GameType
import se.oskarh.boardgamehub.db.boardgame.BoardGame

@Entity(tableName = "boardgame_collection_table")
data class BoardGameCollection(
    var user: String,
    var publicationDate: String,
    var boardGames: List<BoardGameCollectionItem>,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val created: Long = System.currentTimeMillis()) {

    data class BoardGameCollectionItem(
        var id: Int,
        var name: String,
        var yearPublished: Int?,
        var imageUrl: String?,
        var thumbnailUrl: String?,
        var numberOfPlays: Int,
        var comment: String?,
        var owned: Int,
        var previouslyOwned: Int,
        var forTrade: Int,
        var want: Int,
        var wantToPlay: Int,
        var wantToBuy: Int,
        var wishList: Int,
        var preOrdered: Int,
        var lastModified: String) {

        fun toBoardGame() =
            BoardGame(id, name, GameType.BOARDGAME, yearPublished ?: 0, true)
    }
}
