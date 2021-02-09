package se.oskarh.boardgamehub.ui.family

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import se.oskarh.boardgamehub.repository.FinderRepository
import se.oskarh.boardgamehub.repository.MockedRepository
import javax.inject.Inject

class BoardGameFamilyViewModel @Inject constructor(
    private val mockedRepository: MockedRepository,
    private val finderRepository: FinderRepository
) : ViewModel() {

    private val currentFamily = MutableLiveData<Int>()

    val familyResponse = Transformations.switchMap(currentFamily) { familyId ->
        finderRepository.findFamily(familyId)
    }

    fun findFamily(id: Int) {
        currentFamily.value = id
    }
}