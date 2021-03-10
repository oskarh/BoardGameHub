package se.oskarh.boardgamehub.ui.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.property_dialog.*
import se.oskarh.boardgamehub.R
import se.oskarh.boardgamehub.analytics.Analytics
import se.oskarh.boardgamehub.analytics.EVENT_BOARDGAMEGEEK_LINK_OPEN
import se.oskarh.boardgamehub.db.boardgamemeta.BoardGameProperty
import se.oskarh.boardgamehub.db.boardgamemeta.PropertyType
import se.oskarh.boardgamehub.repository.ErrorResponse
import se.oskarh.boardgamehub.repository.LoadingResponse
import se.oskarh.boardgamehub.repository.SuccessResponse
import se.oskarh.boardgamehub.ui.details.PropertyViewModel
import se.oskarh.boardgamehub.util.PROPERTY_ID
import se.oskarh.boardgamehub.util.PROPERTY_TITLE
import se.oskarh.boardgamehub.util.PROPERTY_TYPE
import se.oskarh.boardgamehub.util.extension.injector
import se.oskarh.boardgamehub.util.extension.requireArgumentEnum
import se.oskarh.boardgamehub.util.extension.requireArgumentInt
import se.oskarh.boardgamehub.util.extension.requireArgumentString
import se.oskarh.boardgamehub.util.extension.underline
import se.oskarh.boardgamehub.util.extension.visibleIf
import javax.inject.Inject

class PropertyDialog : BottomSheetDialogFragment() {

    private lateinit var propertyTitle: String

    private lateinit var propertyType: PropertyType

    private var propertyId: Int = -1

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var propertyViewModel: PropertyViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.property_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        injector.inject(this)
        propertyTitle = requireArgumentString(PROPERTY_TITLE)
        propertyId = requireArgumentInt(PROPERTY_ID)
        propertyType = requireArgumentEnum(PROPERTY_TYPE)
        property_title.text = propertyTitle
        boardgame_geek_link.underline()
        boardgame_geek_link.setOnClickListener {
            Analytics.logEvent(EVENT_BOARDGAMEGEEK_LINK_OPEN)
            startActivity(Intent(Intent.ACTION_VIEW, propertyType.toBoardGameGeekUrl(propertyId).toUri()))
        }
        propertyViewModel = ViewModelProvider(this, viewModelFactory).get(PropertyViewModel::class.java)
        propertyViewModel.propertyInformation(propertyId, propertyType).observe(viewLifecycleOwner,
            { response ->
                property_loading.visibleIf { response is LoadingResponse }
                boardgame_geek_link.visibleIf { response !is LoadingResponse }
                property_error.visibleIf { response is ErrorResponse }
                if (response is SuccessResponse) {
                    setProperty(response.data)
                }
            })
    }

    private fun setProperty(property: BoardGameProperty) {
        property_title.text = property.name
        property_description.text = property.description.parseAsHtml()
    }

    companion object {
        fun newInstance(propertyTitle: String, propertyId: Int, propertyType: PropertyType): PropertyDialog {
            Analytics.logEvent(propertyType.eventName)
            return PropertyDialog().apply {
                arguments = bundleOf(
                    PROPERTY_TITLE to propertyTitle,
                    PROPERTY_ID to propertyId,
                    PROPERTY_TYPE to propertyType.ordinal)
            }
        }
    }
}