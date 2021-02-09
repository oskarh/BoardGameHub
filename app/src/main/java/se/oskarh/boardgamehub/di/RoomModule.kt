package se.oskarh.boardgamehub.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import se.oskarh.boardgamehub.db.BoardGameDatabase
import se.oskarh.boardgamehub.db.boardgame.BoardGameDao
import se.oskarh.boardgamehub.db.boardgame.BoardGameRepository
import se.oskarh.boardgamehub.db.boardgamemeta.BoardGamePropertyDao
import se.oskarh.boardgamehub.db.boardgamemeta.BoardGamePropertyRepository
import se.oskarh.boardgamehub.db.collection.BoardGameCollectionDao
import se.oskarh.boardgamehub.db.collection.BoardGameCollectionRepository
import se.oskarh.boardgamehub.db.favorite.FavoriteItemDao
import se.oskarh.boardgamehub.db.favorite.FavoriteItemRepository
import se.oskarh.boardgamehub.db.suggestion.SuggestionDao
import se.oskarh.boardgamehub.db.suggestion.SuggestionRepository
import javax.inject.Singleton

@Module
object RoomModule {

    @JvmStatic
    @Provides
    @Singleton
    fun providesBoardGameDatabase(context: Context): BoardGameDatabase =
        Room.databaseBuilder(context, BoardGameDatabase::class.java, "boardgame_db")
            .fallbackToDestructiveMigration()
            .build()

    @JvmStatic
    @Provides
    @Singleton
    internal fun providesBoardGameDao(boardGameDatabase: BoardGameDatabase): BoardGameDao =
        boardGameDatabase.boardGameDao()

    @JvmStatic
    @Provides
    @Singleton
    internal fun providesSuggestionDao(boardGameDatabase: BoardGameDatabase): SuggestionDao =
        boardGameDatabase.suggestionDao()

    @JvmStatic
    @Provides
    @Singleton
    internal fun providesPropertyDao(boardGameDatabase: BoardGameDatabase): BoardGamePropertyDao =
        boardGameDatabase.propertyDao()

    @JvmStatic
    @Provides
//    @Singleton
    internal fun providesFavoriteItemDao(boardGameDatabase: BoardGameDatabase): FavoriteItemDao =
        boardGameDatabase.favoriteItemDao()

    @JvmStatic
    @Provides
    @Singleton
    internal fun providesBoardGameCollectionDao(boardGameDatabase: BoardGameDatabase): BoardGameCollectionDao =
        boardGameDatabase.boardGameCollectionDao()

    @JvmStatic
    @Provides
    @Singleton
    internal fun providesBoardGameRepository(boardGameDao: BoardGameDao): BoardGameRepository =
        BoardGameRepository(boardGameDao)

    @JvmStatic
    @Provides
    @Singleton
    internal fun providesSuggestionRepository(suggestionDao: SuggestionDao): SuggestionRepository =
        SuggestionRepository(suggestionDao)

    @JvmStatic
    @Provides
    @Singleton
    internal fun providesBoardGamePropertyRepository(propertyDao: BoardGamePropertyDao): BoardGamePropertyRepository =
        BoardGamePropertyRepository(propertyDao)

    @JvmStatic
    @Provides
    internal fun providesFavoriteItemRepository(favoriteItemDao: FavoriteItemDao): FavoriteItemRepository =
        FavoriteItemRepository(favoriteItemDao)

    @JvmStatic
    @Provides
    @Singleton
    internal fun providesBoardGameCollectionRepository(boardGameCollectionDao: BoardGameCollectionDao): BoardGameCollectionRepository =
        BoardGameCollectionRepository(boardGameCollectionDao)
}