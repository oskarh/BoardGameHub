package se.oskarh.boardgamehub.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.db.boardgame.BoardGameDao
import se.oskarh.boardgamehub.db.boardgame.BoardGameFts
import se.oskarh.boardgamehub.db.boardgamemeta.BoardGameProperty
import se.oskarh.boardgamehub.db.boardgamemeta.BoardGamePropertyDao
import se.oskarh.boardgamehub.db.collection.BoardGameCollection
import se.oskarh.boardgamehub.db.collection.BoardGameCollectionDao
import se.oskarh.boardgamehub.db.favorite.FavoriteItem
import se.oskarh.boardgamehub.db.favorite.FavoriteItemDao
import se.oskarh.boardgamehub.db.suggestion.Suggestion
import se.oskarh.boardgamehub.db.suggestion.SuggestionDao

@TypeConverters(
    CommentListConverter::class,
    NameListConverter::class,
    PropertyTypeConverter::class,
    IntListConverter::class,
    LinkTypeListConverter::class,
    PollTypeListConverter::class,
    StringListConverter::class,
    RankListConverter::class,
    GameTypeConverter::class,
    StatisticsConverter::class,
    FavoriteTypeConverter::class,
    FavoriteStatusConverter::class,
    BoardGameCollectionItemListConverter::class,
    VideoListConverter::class)
@Database(entities = [
    BoardGame::class,
    BoardGameFts::class,
    BoardGameProperty::class,
    FavoriteItem::class,
    BoardGameCollection::class,
    Suggestion::class],
    version = 1)
abstract class BoardGameDatabase : RoomDatabase() {

    abstract fun boardGameDao(): BoardGameDao

    abstract fun suggestionDao(): SuggestionDao

    abstract fun propertyDao(): BoardGamePropertyDao

    abstract fun favoriteItemDao(): FavoriteItemDao

    abstract fun boardGameCollectionDao(): BoardGameCollectionDao
}