package se.oskarh.boardgamehub.db.boardgamemeta

class BoardGamePropertyRepository(private val boardGamePropertyDao: BoardGamePropertyDao) {

    suspend fun insert(vararg property: BoardGameProperty) =
        boardGamePropertyDao.insertOrUpdate(*property)

    suspend fun getProperty(id: Int, propertyType: PropertyType): BoardGameProperty? =
        boardGamePropertyDao.getProperty(id, propertyType)

    suspend fun deleteAllProperties() =
        boardGamePropertyDao.deleteAllProperties()
}
