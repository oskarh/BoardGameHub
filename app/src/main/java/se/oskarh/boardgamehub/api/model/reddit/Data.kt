package se.oskarh.boardgamehub.api.model.reddit

data class Data(
    val after: String,
    val before: Any,
    val children: List<Children>
)