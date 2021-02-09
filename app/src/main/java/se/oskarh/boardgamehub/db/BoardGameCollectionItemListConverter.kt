package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.collection.BoardGameCollection
import se.oskarh.boardgamehub.util.extension.deserialize
import se.oskarh.boardgamehub.util.extension.serialize

class BoardGameCollectionItemListConverter {

    @TypeConverter
    fun boardGameCollectionItemListToJson(boardGameCollectionItemList: List<BoardGameCollection.BoardGameCollectionItem>): String =
        serialize(boardGameCollectionItemList)

    @TypeConverter
    fun jsonToBoardGameCollectionItemList(jsonBoardGameCollectionItemList: String): List<BoardGameCollection.BoardGameCollectionItem> =
        deserialize(jsonBoardGameCollectionItemList)
}