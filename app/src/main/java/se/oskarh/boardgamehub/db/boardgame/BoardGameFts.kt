package se.oskarh.boardgamehub.db.boardgame

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.RoomWarnings

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Fts4(contentEntity = BoardGame::class)
@Entity(tableName = "boardgame_table_fts")
data class BoardGameFts(
    var normalizedName: String)