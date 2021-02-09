package se.oskarh.boardgamehub.api.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import se.oskarh.boardgamehub.api.CollectionFilterOptions
import se.oskarh.boardgamehub.db.collection.BoardGameCollection
import se.oskarh.boardgamehub.db.favorite.FavoriteStatus

@Xml(name = "item")
data class CollectionBoardGame(

    @Attribute(name = "objecttype")
    var objectType: String,

    @Attribute(name = "objectid")
    var id: Int,

    @Attribute(name = "subtype")
    var subtype: String,

    @Attribute(name = "collid")
    var collid: Int,

    @PropertyElement(name = "name")
    var name: String,

    @PropertyElement(name = "yearpublished")
    var yearPublished: Int?,

    @PropertyElement(name = "image")
    var image: String?,

    @PropertyElement(name = "thumbnail")
    var thumbnail: String?,

    @PropertyElement(name = "numplays")
    var numberOfPlays: Int,

    @PropertyElement(name = "comment")
    var comment: String?,

    @Path("status")
    @Attribute(name = "own")
    var owned: Int,

    @Path("status")
    @Attribute(name = "prevowned")
    var previouslyOwned: Int,

    @Path("status")
    @Attribute(name = "fortrade")
    var forTrade: Int,

    @Path("status")
    @Attribute(name = "want")
    var want: Int,

    @Path("status")
    @Attribute(name = "wanttoplay")
    var wantToPlay: Int,

    @Path("status")
    @Attribute(name = "wanttobuy")
    var wantToBuy: Int,

    @Path("status")
    @Attribute(name = "wishlist")
    var wishList: Int,

    @Path("status")
    @Attribute(name = "preordered")
    var preOrdered: Int,

    @Path("status")
    @Attribute(name = "lastmodified")
    var lastModified: String) {

    fun toBoardGameCollectionItem() =
        BoardGameCollection.BoardGameCollectionItem(
            id,
            name,
            yearPublished,
            image,
            thumbnail,
            numberOfPlays,
            comment,
            owned,
            previouslyOwned,
            forTrade,
            want,
            wantToPlay,
            wantToBuy,
            wishList,
            preOrdered,
            lastModified)

    fun isUserIncluded(collectionFilterOptions: CollectionFilterOptions) =
        (owned > 0 && collectionFilterOptions.includeOwned) ||
                (previouslyOwned > 0 && collectionFilterOptions.includePreviouslyOwned) ||
                (forTrade > 0 && collectionFilterOptions.includeForTrade) ||
                (want > 0 && collectionFilterOptions.includeWant) ||
                (wantToPlay > 0 && collectionFilterOptions.includeWantToPlay) ||
                (wantToBuy > 0 && collectionFilterOptions.includeWantToBuy) ||
                (wishList > 0 && collectionFilterOptions.includeWishList) ||
                (preOrdered > 0 && collectionFilterOptions.includePreOrdered)

    fun favoriteStatus() =
        when {
            owned > 0 -> FavoriteStatus.OWNED
            previouslyOwned > 0 -> FavoriteStatus.PREVIOUSLY_OWNED
            forTrade > 0 -> FavoriteStatus.FOR_TRADE
            want > 0 -> FavoriteStatus.WANT
            wantToPlay > 0 -> FavoriteStatus.WANT_TO_PLAY
            wantToBuy > 0 -> FavoriteStatus.WANT_TO_BUY
            wishList > 0 -> FavoriteStatus.WISH_LIST
            preOrdered > 0 -> FavoriteStatus.PRE_ORDERED
            else -> FavoriteStatus.UNKNOWN
        }
}