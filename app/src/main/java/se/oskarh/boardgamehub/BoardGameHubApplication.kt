package se.oskarh.boardgamehub

import androidx.multidex.MultiDexApplication
import com.chibatching.kotpref.Kotpref
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.di.ApplicationComponent
import se.oskarh.boardgamehub.di.DaggerApplicationComponent
import se.oskarh.boardgamehub.di.DaggerComponentProvider
import timber.log.Timber

class BoardGameHubApplication : MultiDexApplication(), DaggerComponentProvider {

    override val component: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder()
            .applicationContext(applicationContext)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        Analytics.setupFirebaseAnalytics(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        setupRemoteConfig()
        // Android Default Emulator: Run the command in the terminal -
        // adb forward tcp:8080 tcp:8080 and open http://localhost:8080
//        Timber.d("Debug Database IP [${DebugDB.getAddressLog()}]")
    }

    private fun setupRemoteConfig() {
        FirebaseRemoteConfig.getInstance().run {
            setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(14_400)
                    .build())
//            setDefaults(R.xml.remote_config_defaults)
        }
    }
}
