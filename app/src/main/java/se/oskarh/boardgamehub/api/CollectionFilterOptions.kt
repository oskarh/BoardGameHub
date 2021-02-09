package se.oskarh.boardgamehub.api

data class CollectionFilterOptions(
    val includeOwned: Boolean = false,
    val includePreviouslyOwned: Boolean = false,
    val includeForTrade: Boolean = false,
    val includeWant: Boolean = false,
    val includeWantToPlay: Boolean = false,
    val includeWantToBuy: Boolean = false,
    val includeWishList: Boolean = false,
    val includePreOrdered: Boolean = false)