package com.kaizm.recomposetester

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PeopleViewModel : ViewModel() {
    private var hasLoadedInitialData = false
    private val _state = MutableStateFlow(PeopleScreenState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadPeople()
                hasLoadedInitialData = true
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PeopleScreenState()
        )

    private fun loadPeople() = viewModelScope.launch {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        delay(1000)
        _state.update {
            it.copy(
                isLoading = false,
                people = dummyPeople
            )
        }
    }

    fun onAction(action: PeopleAction) {
        when (action) {
            is PeopleAction.OnLoadDetail -> loadDetail(action.id)
        }

    }

    private fun loadDetail(id: String) {
        _state.update {
            it.copy(
                people = it.people.map { person ->
                    if (person.id == id) {
                        person.copy(
                            isLoadingDetail = true,
                            detailLoadingProgress = 0f
                        )
                    } else person
                }
            )
        }

        val progressFlow = flow {
            var currentProgress = 0f
            while (currentProgress < 1f) {
                emit(currentProgress)
                currentProgress += 0.1f
                delay(10)
            }
        }

        progressFlow.onEach { progress ->
            _state.update {
                it.copy(
                    people = it.people.map { person ->
                        if (person.id == id) {
                            person.copy(
                                detailLoadingProgress = progress
                            )
                        } else person
                    }
                )
            }
        }.onCompletion {
            _state.update {
                it.copy(
                    people = it.people.map { person ->
                        if (person.id == id) {
                            person.copy(
                                isLoadingDetail = false,
                                detail = PersonDetail(
                                    phoneNumber = "123456789",
                                    bio = "This is a bio"
                                )
                            )
                        } else person
                    }
                )
            }
        }.launchIn(viewModelScope)
    }

    private val dummyPeople =
        listOf(
            PersonUi(
                id = "1",
                name = "John Doe",
                isLoadingDetail = false,
                detailLoadingProgress = 0f,
                detail = null
            ),
            PersonUi(
                id = "2",
                name = "Jane Doe",
                isLoadingDetail = false,
                detailLoadingProgress = 0f,
                detail = null
            ),
            PersonUi(
                id = "3",
                name = "John Smith",
                isLoadingDetail = false,
                detailLoadingProgress = 0f,
                detail = null
            ),
            PersonUi(
                id = "4",
                name = "Jane Smith",
                isLoadingDetail = false,
                detailLoadingProgress = 0f,
                detail = null
            ),
            PersonUi(
                id = "5",
                name = "Samson",
                isLoadingDetail = false,
                detailLoadingProgress = 0f,
                detail = null
            ),
            PersonUi(
                id = "6",
                name = "William",
                isLoadingDetail = false,
                detailLoadingProgress = 0f,
                detail = null
            ),
            PersonUi(
                id = "7",
                name = "Michale",
                isLoadingDetail = false,
                detailLoadingProgress = 0f,
                detail = null
            ),
            PersonUi(
                id = "8",
                name = "Bucky",
                isLoadingDetail = false,
                detailLoadingProgress = 0f,
                detail = null
            ),
        )
}

@Stable
data class PeopleScreenState(
    val isLoading: Boolean = false,
    val people: List<PersonUi> = emptyList()
)

data class PersonUi(
    val id: String,
    val name: String,
    val isLoadingDetail: Boolean,
    val detailLoadingProgress: Float,
    val detail: PersonDetail?
)


data class PersonDetail(
    val phoneNumber: String,
    val bio: String
)

sealed interface PeopleAction {
    data class OnLoadDetail(val id: String) : PeopleAction
}
