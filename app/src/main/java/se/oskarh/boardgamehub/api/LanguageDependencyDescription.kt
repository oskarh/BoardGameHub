package se.oskarh.boardgamehub.api

data class LanguageDependencyDescription(
    val level: String,
    val description: String,
    val votes: Int)