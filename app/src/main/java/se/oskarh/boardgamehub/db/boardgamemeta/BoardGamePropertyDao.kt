package se.oskarh.boardgamehub.db.boardgamemeta

import androidx.room.Dao
import androidx.room.Query
import se.oskarh.boardgamehub.db.BaseDao

@Dao
interface BoardGamePropertyDao : BaseDao<BoardGameProperty> {

    @Query("SELECT * FROM boardgame_property_table WHERE id = :id AND propertyType = :propertyType")
    suspend fun getProperty(id: Int, propertyType: PropertyType): BoardGameProperty?

    @Query("DELETE FROM boardgame_property_table")
    suspend fun deleteAllProperties()
}