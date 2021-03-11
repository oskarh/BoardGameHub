package se.oskarh.boardgamehub.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import se.oskarh.boardgamehub.db.boardgamemeta.BoardGameProperty
import se.oskarh.boardgamehub.db.boardgamemeta.PropertyType
import se.oskarh.boardgamehub.repository.ApiResponse
import se.oskarh.boardgamehub.repository.MockedRepository
import se.oskarh.boardgamehub.repository.PropertyRepository
import javax.inject.Inject

class PropertyViewModel @Inject constructor(
    private val mockedRepository: MockedRepository,
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    fun propertyInformation(id: Int, propertyType: PropertyType): LiveData<ApiResponse<BoardGameProperty>> =
        propertyRepository.findProperty(id, propertyType)
}