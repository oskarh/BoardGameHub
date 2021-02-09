package se.oskarh.boardgamehub.db.suggestion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import se.oskarh.boardgamehub.util.extension.normalize

@Entity(tableName = "suggestion_table")
data class Suggestion(
    @ColumnInfo(name = "original_suggestion")
    val originalSuggestion: String,

    // TODO: Remove snake case?
    @PrimaryKey
    val formattedSuggestion: String,

    val created: Long = System.currentTimeMillis()) {

    constructor(suggestion: String) : this(suggestion.trim(), suggestion.normalize())
}