package se.oskarh.boardgamehub.ui.about

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_ABOUT_OPEN_SOURCE
import se.oskarh.boardgamehub.analytics.EVENT_ABOUT_PRIVACY_POLICY
import se.oskarh.boardgamehub.analytics.EVENT_ABOUT_RATE
import se.oskarh.boardgamehub.analytics.EVENT_ABOUT_SHARE
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAMEGEEK_LINK_OPEN
import se.oskarh.boardgamehub.util.AppPreferences
import se.oskarh.boardgamehub.util.BOARDGAME_GEEK_URL
import se.oskarh.boardgamehub.util.BOARDGAME_HUB_PRIVACY_URL
import se.oskarh.boardgamehub.util.TEXT_PLAIN
import se.oskarh.boardgamehub.util.extension.canHandleRatingIntent
import se.oskarh.boardgamehub.util.extension.loadAnimation
import se.oskarh.boardgamehub.util.extension.redirectToPlayStore
import se.oskarh.boardgamehub.util.extension.startActivity

class AboutActivity : MaterialAboutActivity() {

    override fun onStart() {
        super.onStart()
        findViewById<RecyclerView>(R.id.mal_recyclerview)
            .startAnimation(loadAnimation(R.anim.about_activity_enter_animation))
    }

    override fun getActivityTitle(): String = getString(R.string.about)

    override fun getMaterialAboutList(context: Context): MaterialAboutList {
        val appInfoCard = MaterialAboutCard.Builder().apply {
            addItem(
                MaterialAboutTitleItem.Builder()
                    .text(R.string.app_name)
                    .icon(R.mipmap.ic_launcher)
                    .build()
            )

            addItem(
                ConvenienceBuilder.createVersionActionItem(
                    this@AboutActivity,
                    getDrawable(R.drawable.ic_info_black_24dp),
                    getString(R.string.version),
                    true
                )
            )

// TODO: Re-enable this
//            addItem(MaterialAboutActionItem.Builder()
//                .text(getString(R.string.view_changelog))
//                .icon(R.drawable.ic_history_black_24dp)
//                .setOnClickAction {
//                    showChangelog(this@AboutActivity)
//                }
//                .build())

            addItem(MaterialAboutActionItem.Builder()
                .text(getString(R.string.open_source_licenses))
                .icon(R.drawable.ic_code_black_24dp)
                .setOnClickAction {
                    Analytics.logEvent(EVENT_ABOUT_OPEN_SOURCE)
                    startActivity<OssLicensesMenuActivity>()
                }
                .build())

            addItem(MaterialAboutActionItem.Builder()
                .text(getString(R.string.boardgamegeek))
                .subText(getString(R.string.boardgamegeek_thanks))
                .icon(R.drawable.boardgamegeek_logo_real)
                .setOnClickAction {
                    Analytics.logEvent(EVENT_BOARDGAMEGEEK_LINK_OPEN)
                    startActivity(Intent(Intent.ACTION_VIEW, BOARDGAME_GEEK_URL.toUri()))
                }
                .build())
        }

        val appActionsCard = MaterialAboutCard.Builder().apply {
            addItem(
                ConvenienceBuilder.createEmailItem(
                    this@AboutActivity,
                    getDrawable(R.drawable.ic_email_black_24dp),
                    getString(R.string.contact_me_title),
                    true,
                    getString(R.string.boardgamehub_email),
                    getString(R.string.email_subject),
                    getString(R.string.contact_me_description)
                ))


            addItem(ConvenienceBuilder.createRateActionItem(
                this@AboutActivity,
                getDrawable(R.drawable.ic_star_black_24dp),
                getString(R.string.rate_app), null
            )
                .setOnClickAction {
                    Analytics.logEvent(EVENT_ABOUT_RATE)
                    AppPreferences.hasReviewed = true
                    if (canHandleRatingIntent()) {
                        redirectToPlayStore()
                    }
                })

            addItem(MaterialAboutActionItem.Builder()
                .text(getString(R.string.share_app))
                .icon(R.drawable.ic_share_accent_24dp)
                .setOnClickAction {
                    Analytics.logEvent(EVENT_ABOUT_SHARE)
                    shareApp()
                }
                .subText(getString(R.string.share_app_subtext))
                .build())

            addItem(MaterialAboutActionItem.Builder()
                .text(R.string.privacy_policy)
                .icon(R.drawable.ic_assignment_black_24dp)
                .setOnClickAction {
                    Analytics.logEvent(EVENT_ABOUT_PRIVACY_POLICY)
                    startActivity(Intent(Intent.ACTION_VIEW, BOARDGAME_HUB_PRIVACY_URL.toUri()))
                }
                .build())
        }

        return MaterialAboutList(appInfoCard.build(), appActionsCard.build())
    }

    private fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = TEXT_PLAIN
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.play_store_web_uri))
        }
        startActivity(intent)
    }
}