package se.oskarh.boardgamehub.ui.common

import android.app.Activity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_import_collection.view.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.util.AppPreferences

fun showImportCollectionBottomDialog(activity: Activity, onClick: (String) -> Unit) {
    BottomSheetDialog(activity).run {
        val importCollectionView = activity.layoutInflater.inflate(R.layout.dialog_import_collection, null).apply {
            username.setText(AppPreferences.latestUsernameCollection)
            username.text.toString().trim()
            only_keep_imported_ids_checkbox.isChecked = !AppPreferences.isOldCollectionKept
            owned_checkbox.isChecked = AppPreferences.areOwnedBoardGamesImported
            previously_owned_checkbox.isChecked = AppPreferences.arePreviouslyOwnedBoardGamesImported
            for_trade_checkbox.isChecked = AppPreferences.areForTradeBoardGamesImported
            want_checkbox.isChecked = AppPreferences.areWantBoardGamesImported
            want_to_play_checkbox.isChecked = AppPreferences.areWantToPlayBoardGamesImported
            want_to_buy_checkbox.isChecked = AppPreferences.areWantToBuyBoardGamesImported
            wish_list_checkbox.isChecked = AppPreferences.areWishListBoardGamesImported
            preordered_checkbox.isChecked = AppPreferences.arePreOrderedBoardGamesImported

            import_collection_cancel.setOnClickListener {
                cancel()
            }
            import_collection_ok.setOnClickListener {
                AppPreferences.latestUsernameCollection = username.text.toString().trim()
                AppPreferences.isOldCollectionKept = !only_keep_imported_ids_checkbox.isChecked
                AppPreferences.areOwnedBoardGamesImported = owned_checkbox.isChecked
                AppPreferences.arePreviouslyOwnedBoardGamesImported = previously_owned_checkbox.isChecked
                AppPreferences.areForTradeBoardGamesImported = for_trade_checkbox.isChecked
                AppPreferences.areWantBoardGamesImported = want_checkbox.isChecked
                AppPreferences.areWantToPlayBoardGamesImported = want_to_play_checkbox.isChecked
                AppPreferences.areWantToBuyBoardGamesImported = want_to_buy_checkbox.isChecked
                AppPreferences.areWishListBoardGamesImported = wish_list_checkbox.isChecked
                AppPreferences.arePreOrderedBoardGamesImported = preordered_checkbox.isChecked
                onClick(username.text?.toString().orEmpty().trim())
                hide()
            }
        }
        setContentView(importCollectionView)
        show()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}
