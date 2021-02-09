package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.deserialize
import se.oskarh.boardgamehub.util.extension.serialize

class CommentListConverter {

    @TypeConverter
    fun commentListToJson(commentList: List<BoardGame.Comment>): String =
        serialize(commentList)

    @TypeConverter
    fun jsonToCommentList(jsonCommentList: String): List<BoardGame.Comment> =
        deserialize(jsonCommentList)
}