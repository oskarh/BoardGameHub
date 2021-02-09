package se.oskarh.boardgamehub.api

import se.oskarh.boardgamehub.db.boardgame.BoardGame

// TODO: Fix comments properly
data class BoardGameComments(val totalComments: Int, val comments: List<BoardGame.Comment>) {

    fun hasAllComments() = totalComments == comments.size
}