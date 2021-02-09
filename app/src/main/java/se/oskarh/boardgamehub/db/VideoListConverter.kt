package se.oskarh.boardgamehub.db

import androidx.room.TypeConverter
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.util.extension.deserialize
import se.oskarh.boardgamehub.util.extension.serialize

class VideoListConverter {

    @TypeConverter
    fun videoListToJson(videoList: List<BoardGame.Video>): String =
        serialize(videoList)

    @TypeConverter
    fun jsonToVideoList(jsonVideoList: String): List<BoardGame.Video> =
        deserialize(jsonVideoList)
}