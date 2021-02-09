package se.oskarh.boardgamehub.db.boardgamemeta

import androidx.room.Entity

@Entity(tableName = "boardgame_property_table", primaryKeys = ["id", "propertyType"])
data class BoardGameProperty(
    val id: Long,
    val name: String,
    val description: String,
    val propertyType: PropertyType,
    val created: Long = System.currentTimeMillis()) {

    constructor(id: Int, name: String, description: String, propertyType: PropertyType) :
            this(id.toLong(), name, description, propertyType)
}