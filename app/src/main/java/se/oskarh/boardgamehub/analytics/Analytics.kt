package se.oskarh.boardgamehub.analytics

import android.app.Activity
import android.content.Context
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

object Analytics {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    fun setupFirebaseAnalytics(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
    }

    fun logEvent(eventName: String, vararg parameters: Pair<String, Any>) {
        Timber.d("Logging event $eventName params ${bundleOf(*parameters)}")
        firebaseAnalytics.logEvent(eventName, bundleOf(*parameters))
    }

    fun setCurrentScreen(activity: Activity, screenType: ScreenType) {
        firebaseAnalytics.setCurrentScreen(activity, screenType.screenName, screenType.screenName)
    }

    fun setUserProperty(userProperty: String, value: Int) {
        Timber.d("Logging event user property $userProperty to $value")
        firebaseAnalytics.setUserProperty(userProperty, value.toString())
    }
}
