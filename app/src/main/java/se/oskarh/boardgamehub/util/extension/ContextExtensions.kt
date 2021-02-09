package se.oskarh.boardgamehub.util.extension

import android.app.Activity
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.youtube.player.YouTubeIntents
import com.google.gson.Gson
import com.tickaroo.tikxml.TikXml
import okio.buffer
import okio.source
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.api.model.youtube.YouTubeVideoInfo
import se.oskarh.boardgamehub.ui.video.VideoPlayerActivity
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.KEY_YOUTUBE_VIDEO
import se.oskarh.boardgamehub.util.TEXT_PLAIN

inline fun <reified T> Context.jsonToClass(@RawRes resourceId: Int): T =
        Gson().fromJson(resources.openRawResource(resourceId).bufferedReader().use { it.readText() }, T::class.java)

inline fun <reified T> Context.xmlToClass(@RawRes resourceId: Int): T {
    val parser = TikXml.Builder().build()
    return parser.read(resources.openRawResource(resourceId).source().buffer(), T::class.java)
//    return parser.read(Okio.buffer(Okio.source(resources.openRawResource(resourceId))), T::class.java)
}

fun Context.getCompatColor(@ColorRes colorResource: Int) = ContextCompat.getColor(this, colorResource)

fun Context.startVideoActivity(youTubeVideo: YouTubeVideoInfo) {
    if (AppPreferences.shouldOpenVideosInYouTube or true) { // Remove this!
        watchYouTubeVideo(youTubeVideo.id)
    } else {
        startActivity<VideoPlayerActivity>(KEY_YOUTUBE_VIDEO to youTubeVideo)
    }
}

fun Context.watchYouTubeVideo(id: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://$id")))
    } catch (ex: ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$id")))
    }
}

inline fun <reified T : Activity> Fragment.startActivity(vararg params: Pair<String, Any?>) =
        activity?.startActivity<T>(*params)

inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) =
        startActivity(Intent(this, T::class.java).apply {
            putExtras(bundleOf(*params))
        })

inline fun <reified T : Service> Activity.startMyService(vararg params: Pair<String, Any?>) =
        startService(Intent(this, T::class.java).apply {
            putExtras(bundleOf(*params))
        })

fun Context.loadAnimation(@AnimRes animationId: Int): Animation = AnimationUtils.loadAnimation(this, animationId)

fun Context.getInputMethodManager() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

fun Context.redirectToPlayStore() {
    startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = getString(R.string.play_store_uri).toUri()
    })
}

fun Fragment.canHandleGoogleMapsIntent() = context?.canHandleGoogleMapsIntent() == true

fun Context.canHandleGoogleMapsIntent() = try {
    packageManager.getApplicationInfo("com.google.android.apps.maps", 0).enabled
} catch (e: PackageManager.NameNotFoundException) {
    false
}

fun Fragment.canHandleRatingIntent() = context?.canHandleRatingIntent() == true

fun Context.canHandleRatingIntent() = Intent(Intent.ACTION_VIEW).apply {
    data = getString(R.string.play_store_uri).toUri()
}.resolveActivity(packageManager) != null

fun Context.showOkCancelDialog(title: String, message: String, callback: () -> Unit) {
    AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(getString(R.string.ok)) { _, _ ->
            callback()
        }
        setNegativeButton(getString(R.string.cancel)) { _, _ ->
        }
        create()
        show()
    }
}

fun Context.canResolveYouTubeSearch() = YouTubeIntents.canResolveSearchIntent(this)

fun Context.isYouTubeInstalled() = YouTubeIntents.isYouTubeInstalled(this)

fun Context.shareBoardGameIntent(id: Int, title: String) =
    Intent(Intent.ACTION_SEND).apply {
        type = TEXT_PLAIN
        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_boardgame_subject, title))
        putExtra(Intent.EXTRA_TEXT, getString(R.string.share_boardgame_text, getString(R.string.boardgamegeek_game_link_builder, id)))
    }

fun Context.getCompatDrawable(@DrawableRes drawableId: Int) = ContextCompat.getDrawable(this, drawableId)!!