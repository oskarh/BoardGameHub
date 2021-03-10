package se.oskarh.boardgamehub.util.extension

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_POPUP_MENU_FAVORITE
import se.oskarh.boardgamehub.analytics.EVENT_POPUP_MENU_SHARE
import se.oskarh.boardgamehub.analytics.EVENT_POPUP_MENU_SHOW
import se.oskarh.boardgamehub.db.boardgame.BoardGame
import se.oskarh.boardgamehub.ui.common.DefaultChip
import se.oskarh.boardgamehub.util.ANIMATION_DEFAULT_DURATION
import se.oskarh.boardgamehub.util.IMAGE_BACKGROUND_ANIMATION_DURATION
import se.oskarh.boardgamehub.util.PREFETCH_BUFFER_SIZE
import timber.log.Timber
import java.lang.ref.WeakReference
import kotlin.math.abs

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.visibleIf(elseVisibility: Int = View.GONE, isVisible: () -> Boolean) {
    visibility = when {
        isVisible() -> View.VISIBLE
        else -> elseVisibility
    }
}

fun View.gone() {
    visibility = View.GONE
}

fun View.getCompatColor(@ColorRes colorResource: Int) = context.getCompatColor(colorResource)

fun View.animateToSize(size: Float, animationDuration: Long = ANIMATION_DEFAULT_DURATION) {
    ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat(View.SCALE_X, size),
        PropertyValuesHolder.ofFloat(View.SCALE_Y, size)
    ).run {
        interpolator = DecelerateInterpolator()
        duration = animationDuration
        start()
    }
}

fun View.animateToGone(animationDuration: Long = ANIMATION_DEFAULT_DURATION) {
    ValueAnimator.ofInt(measuredHeight, 0).run {
        addUpdateListener {
            layoutParams = layoutParams.apply {
                height = it.animatedValue as Int
            }
        }
        interpolator = DecelerateInterpolator()
        duration = animationDuration
        start()
    }
}

fun View.animateToVisible(animationDuration: Long = ANIMATION_DEFAULT_DURATION) {
    val alphaAnimation = AlphaAnimation(0.0f, 1.0f).apply {
        duration = animationDuration
        fillAfter = true
        interpolator = DecelerateInterpolator()
    }
    startAnimation(alphaAnimation)
}

fun View?.rotateBy(degrees: Float = 720f, duration: Long = 500) = this?.animate()
    ?.rotationBy(degrees)
    ?.setDuration(duration)
    ?.setInterpolator(FastOutSlowInInterpolator())
    ?.start()

fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

fun View.showSnackbar(@StringRes message: Int, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

fun View.showActionSnackbar(message: String, @StringRes actionMessage: Int, duration: Int = Snackbar.LENGTH_SHORT, action: (View) -> Unit) {
    Snackbar.make(this, message, duration)
        .setAction(actionMessage, action)
        .show()
}

fun View.startAnimation(@AnimRes animationResource: Int) {
    startAnimation(context.loadAnimation(animationResource))
}

fun View.showPopupMenu(boardGame: BoardGame, isFavorite: Boolean = false, favoriteToggle: (Int) -> Unit) {
    Analytics.logEvent(EVENT_POPUP_MENU_SHOW)
    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    PopupMenu(context, this).run {
        setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.favorite_item -> {
                    Analytics.logEvent(EVENT_POPUP_MENU_FAVORITE)
                    favoriteToggle(boardGame.id)
                }
                R.id.share_item -> {
                    Analytics.logEvent(EVENT_POPUP_MENU_SHARE)
                    startActivity(
                        context,
                        Intent.createChooser(
                            context.shareBoardGameIntent(boardGame.id, boardGame.primaryName()),
                            context.getString(R.string.share_title)), null)
                }
            }
            false
        }
        inflate(R.menu.popup_menu)
        menu.findItem(R.id.favorite_item)?.let { menuItem ->
            val message = if(isFavorite) R.string.remove_favorite else R.string.add_favorite
            menuItem.title = context.getString(message)
        }
        show()
    }
}

fun ViewPager.addOnPageSelectedListener(pageSelected: (Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            pageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }
    })
}

fun ImageView.loadImage(url: String?) {
    Glide.with(context)
        .load(url)
        .error(ContextCompat.getDrawable(context, R.drawable.image_missing))
        .into(this)
}

// TODO: Add placeholder thumbnail if missing thumbnail or fail to load. Also create Glide in application?
fun ImageView.loadImageChangeBackground(url: String?) {
    loadImageChangeBackground(url, 0)
}

fun ImageView.loadImageAnimateBackground(url: String?) {
    loadImageChangeBackground(url, IMAGE_BACKGROUND_ANIMATION_DURATION)
}

private fun ImageView.loadImageChangeBackground(url: String?, animationDuration: Long = IMAGE_BACKGROUND_ANIMATION_DURATION) {
    Glide.with(context)
        .load(url)
        .listener(object : RequestListener<Drawable> {
            override fun onResourceReady(resource: Drawable?,
                                         model: Any?,
                                         target: Target<Drawable>?,
                                         dataSource: DataSource?,
                                         isFirstResource: Boolean): Boolean {
                resource?.let { drawable ->
                    Palette.from(drawable.toBitmap()).generate { palette ->
                        palette?.getDominantColor(getCompatColor(R.color.secondaryBackgroundColor))?.let { dominantColor ->
                            animateToBackgroundColor(dominantColor, animationDuration)
                        } ?: setBackgroundColor(getCompatColor(R.color.secondaryBackgroundColor))
                    }
                }
                return false
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                Timber.d("onLoadFailed...")
                return false
            }
        })
        // TODO: Fix better / more fun image missing drawable
        .error(ContextCompat.getDrawable(context, R.drawable.image_missing))
        .applyIf(animationDuration == 0L) {
            dontAnimate()
            dontTransform()
        }
        .into(this)
}

fun ImageView.animateToBackgroundColor(@ColorInt toColor: Int, animationDuration: Long = ANIMATION_DEFAULT_DURATION) {
    ObjectAnimator.ofInt(this, "backgroundColor", R.color.secondaryBackgroundColor, toColor).run {
        setEvaluator(ArgbEvaluator())
        duration = animationDuration
        interpolator = DecelerateInterpolator()
        start()
    }
}

fun ImageView.setImageDrawableCompat(@DrawableRes drawableResource: Int) =
    setImageDrawable(ContextCompat.getDrawable(context, drawableResource))

fun RecyclerView?.prefetchItemIndexes(headerSize: Int = 0, size: Int = PREFETCH_BUFFER_SIZE): IntRange =
    visibleItemIndexes(headerSize).expand(size)

fun RecyclerView?.visibleItemIndexes(headerSize: Int): IntRange {
    return (this?.layoutManager as? LinearLayoutManager)
        ?.takeUnless { it.findFirstVisibleItemPosition() == NO_POSITION || !this@visibleItemIndexes.isVisible }
        ?.run {
            (findFirstVisibleItemPosition() - headerSize)..(findLastVisibleItemPosition() - headerSize)
        } ?: IntRange.EMPTY
}

fun RecyclerView?.lastAdapterIndex(): Int = (this?.adapter?.itemCount?.minus(1)) ?: -1

fun EditText.setCursorEndOfLine() = postDelayed({ setSelection(text.length) }, 5)

fun RecyclerView.closeKeyboardOnScrollStart(activity: Activity) = closeKeyboardOnScrollStart(WeakReference(activity))

fun RecyclerView.closeKeyboardOnScrollStart(activity: WeakReference<Activity>) =
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == SCROLL_STATE_DRAGGING) {
                activity.get()?.hideKeyboard()
            }
        }
    })

fun NestedScrollView.closeKeyboardOnScroll(activity: Activity) =
    closeKeyboardOnScroll(WeakReference(activity))

fun NestedScrollView.closeKeyboardOnScroll(activity: WeakReference<Activity>) {
    setOnScrollChangeListener(
        NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            Timber.d("Nested scroll happening $scrollY $oldScrollY dy[${abs(scrollY - oldScrollY)}]")
            if (abs(scrollY - oldScrollY) > 10) {
                activity.get()?.hideKeyboard()
            }
        })
}

fun RecyclerView.scrollToTop() {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            scrollToPosition(0)
            viewTreeObserver?.removeOnGlobalLayoutListener(this)
        }
    })
}

// TODO: Fix the childCount == 0 mess
fun ChipGroup.addChips(context: Context, links: List<BoardGame.Link>, isForceAdd: Boolean = false, onOpen: (BoardGame.Link) -> Unit = {}) {
    if (childCount == 0 || isForceAdd) {
        links.forEach { link ->
            val chip = DefaultChip(context, link.value).apply {
                setOnClickListener {
                    onOpen(link)
                }
            }
            addView(chip)
        }
    }
}

// TODO: Merge this with method above
// TODO: Fix the childCount == 0 mess
fun ChipGroup.addRankChips(context: Context, links: List<BoardGame.Rank>, onOpen: (BoardGame.Rank) -> Unit = {}) {
    if (childCount == 0) {
        links.forEach { link ->
            val chip = DefaultChip(context, link.friendlyname.split(' ').first()).apply {
                setOnClickListener {
                    onOpen(link)
                }
            }
            addView(chip)
        }
    }
}

fun NestedScrollView.removeScrollChangeListener() =
    setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ -> })

fun TextView.underline() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

fun TextView.italicize() {
    setTypeface(null, Typeface.ITALIC)
}

// TODO: Fix deprecation warnings
fun Activity.enableFullScreen() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE)
}

fun EditText.textChangedObserver(): LiveData<String> {
    return MutableLiveData<String>().apply {
        value = text?.toString().orEmpty()
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                value = editable?.toString().orEmpty()
            }

            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
}

fun ViewGroup.inflate(resourceId: Int, isAttachedToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(resourceId, this, isAttachedToRoot)
}