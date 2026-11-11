package com.seedrow.spacer.presentation.rotation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedrow.spacer.domain.model.Bed
import com.seedrow.spacer.domain.model.SeasonRecord
import com.seedrow.spacer.domain.repository.GardenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class RotationNotesViewModel(
    private val garden: GardenRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RotationNotesUiState())
    val state: StateFlow<RotationNotesUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(garden.seasons, garden.beds) { seasons, beds ->
                build(seasons, beds)
            }.collect { next ->
                _state.value = next
            }
        }
    }

    private fun build(seasons: List<SeasonRecord>, beds: List<Bed>): RotationNotesUiState {
        val names = beds.associate { it.id to it.name }
        val notes = seasons
            .groupBy { it.bedId }
            .toSortedMap()
            .flatMap { entry ->
                val ordered = entry.value.sortedBy { it.year }
                ordered.mapIndexed { index, record ->
                    val previous = ordered.getOrNull(index - 1)
                    RotationNote(
                        id = record.bedId + record.year.toString(),
                        bedName = names[record.bedId] ?: record.bedId,
                        year = record.year,
                        cropName = record.cropName,
                        cropFamily = record.cropFamily,
                        spriteIndex = record.spriteIndex,
                        warning = previous != null &&
                            previous.cropFamily == record.cropFamily &&
                            previous.year == record.year - 1
                    )
                }
            }
            .sortedWith(compareBy({ it.bedName }, { -it.year }))
        return RotationNotesUiState(
            notes = notes,
            warningCount = notes.count { it.warning }
        )
    }
}
