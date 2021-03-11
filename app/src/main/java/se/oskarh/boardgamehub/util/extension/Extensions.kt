package se.oskarh.boardgamehub.util.extension

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Parcelable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.RecyclerView
import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.livedata.asLiveData
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.youtube.player.YouTubeIntents
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.ScreenType
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.di.DaggerComponentProvider
import se.oskarh.boardgamehub.repository.ApiResponse
import se.oskarh.boardgamehub.repository.EmptyResponse
import se.oskarh.boardgamehub.repository.ErrorResponse
import se.oskarh.boardgamehub.repository.LoadingResponse
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.util.COULD_NOT_PARSE_DEFAULT
import se.oskarh.boardgamehub.util.DetailedRankFormatter
import se.oskarh.boardgamehub.util.PRIMARY
import se.oskarh.boardgamehub.util.PUBLICATION_YEAR_UNKNOWN
import se.oskarh.boardgamehub.util.RankFormatter
import se.oskarh.boardgamehub.util.ignoredCharacters
import se.oskarh.boardgamehub.util.nonDigitsRegex
import java.text.Normalizer
import java.util.Locale
import kotlin.reflect.KProperty0

val Activity.injector get() = (application as DaggerComponentProvider).component

val Fragment.injector get() = (requireActivity().application as DaggerComponentProvider).component

val Service.injector get() = (applicationContext as DaggerComponentProvider).component

val userPrimaryLocale: Locale = ConfigurationCompat.getLocales(Resources.getSystem().configuration).get(0)

fun CharSequence?.trimmedCount(): Int = this?.trim()?.count() ?: 0

inline fun <reified T : Fragment> FragmentActivity.addFragment(fragment: T, @IdRes containerViewId: Int = R.id.main_content) {
    hideKeyboard()
    supportFragmentManager.beginTransaction()
        .add(containerViewId, fragment, fragment.javaClass.simpleName)
        .addToBackStack(T::class.java.simpleName)
        .commit()
}

fun Activity.showKeyboard() {
    getInputMethodManager().toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity.setCurrentScreen(screenType: ScreenType) {
    Analytics.setCurrentScreen(this, screenType)
}

fun Activity.isLandscapeOrientation() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Fragment.hideKeyboard() {
    activity?.hideKeyboard()
}

fun Activity.hideKeyboard() {
    currentFocus?.let { view ->
        getInputMethodManager().hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun String.extractInteger(startDelimiter: String, endDelimiter: String): Int? {
    return substringAfter(startDelimiter)
        .substringBefore(endDelimiter)
        .toIntOrNull()
}

fun Fragment.requireArgumentInt(key: String): Int = requireArguments().getInt(key)

fun Fragment.requireArgumentString(key: String): String = requireArguments().getString(key)!!

fun <T : Parcelable> Fragment.requireArgumentParcelable(key: String): T = requireArguments().getParcelable(key)!!

inline fun <reified T : Enum<T>> Fragment.requireArgumentEnum(key: String): T =
    T::class.java.enumConstants!![requireArgumentInt(key)]

inline fun <reified T : Enum<T>> Activity.argumentEnum(key: String): T? =
    T::class.java.enumConstants!!.getOrNull(intent.getIntExtra(key, -1))

fun String.toIntOrZero() = toIntOrNull() ?: 0

// TODO: Normalize differently? Only keep alphabetic letters, numbers and some special characters?
fun String.normalize() =
    replace(ignoredCharacters, "")
        .replace("&", "and")
        .unaccent()
        .toLowerCase(Locale.ENGLISH)
        .trim()

fun String.keepDigits() = nonDigitsRegex.replace(this, "")

fun <T> KotprefModel.asDistinctLiveData(property: KProperty0<T>): LiveData<T> =
    Transformations.distinctUntilChanged(asLiveData(property))

inline fun <reified T> serialize(instance: T): String =
    Gson().toJson(instance, object : TypeToken<T>() {}.type)

inline fun <reified T> deserialize(json: String): T =
    Gson().fromJson(json, object : TypeToken<T>() {}.type)

fun IntRange.expand(size: Int) =
    if (this != IntRange.EMPTY) {
        IntRange(start, endInclusive + size)
    } else {
        IntRange.EMPTY
    }

fun List<BoardGame.Name>.primaryName() =
    firstOrNull { it.type == PRIMARY }?.value
        ?: firstOrNull()?.value
        ?: "Unknown"

fun BottomSheetBehavior<*>.toggleState() {
    state = if (state != BottomSheetBehavior.STATE_EXPANDED) {
        BottomSheetBehavior.STATE_EXPANDED
    } else {
        BottomSheetBehavior.STATE_COLLAPSED
    }
}

inline fun <T, R> ApiResponse<T>.mapResponse(transform: (T) -> R, isEmpty: (T) -> Boolean = { false }): ApiResponse<R> =
    when (this) {
        is SuccessResponse<T> -> {
            if (isEmpty(data)) {
                EmptyResponse()
            } else {
                SuccessResponse(transform(data))
            }
        }
        is LoadingResponse -> LoadingResponse()
        is EmptyResponse -> EmptyResponse()
        is ErrorResponse -> ErrorResponse(errorMessage)
    }

fun <T> LiveData<T>.requireValue() = value!!

fun Activity.searchYouTube(query: String) {
    startActivity(YouTubeIntents.createSearchIntent(this, getString(R.string.youtube_search, query)))
}

fun Activity.showTapTarget(view: View, @StringRes title: Int, @StringRes message: Int) {
    TapTargetView.showFor(this,
        TapTarget.forView(view, getString(title), getString(message))
            .outerCircleColor(R.color.secondaryBackgroundColor)
            .outerCircleAlpha(0.96f)
            .targetCircleColor(R.color.light_transparent)
            .titleTextSize(22)
            .descriptionTextSize(18)
            .textColor(R.color.primaryTextColor)
            .drawShadow(true)
            .cancelable(true)
            .tintTarget(false)
            .transparentTarget(false)
            .targetRadius(60))
}

private val REGEX_UNACCENT = "\\p{InCombiningDiacriticalMarks}+".toRegex()

fun CharSequence.unaccent(): String =
    REGEX_UNACCENT.replace(Normalizer.normalize(this, Normalizer.Form.NFD), "")

fun ApiResponse<*>.log() =
    when (this) {
        is LoadingResponse -> "Loading Response..."
        is SuccessResponse -> "Success Response... ${data.toString()}"
        is EmptyResponse -> "Empty Response..."
        is ErrorResponse -> "Error Response... [$errorMessage]"
    }

fun Boolean.toInt() = if(this) 1 else 0

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    return if (condition) this.apply(block) else this
}

fun String?.formatRating(isDetailed: Boolean = false) =
    this?.takeUnless { it == "0" }
    ?.toFloatOrNull()
    ?.let {
        if (isDetailed) {
            DetailedRankFormatter.format(it).toString()
        } else {
            RankFormatter.format(it).toString()
        }
    } ?: COULD_NOT_PARSE_DEFAULT

fun Intent.requireString(key: String) = getStringExtra(key)!!

fun RecyclerView.Adapter<*>.isEmpty() = itemCount == 0

val BoardGame.hasPublicationYear: Boolean
    get() = yearPublished != PUBLICATION_YEAR_UNKNOWN
