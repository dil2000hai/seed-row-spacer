package com.seedrow.spacer.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seedrow.spacer.core.config.GameConfig
import com.seedrow.spacer.domain.model.GridKind
import com.seedrow.spacer.domain.repository.GardenRepository
import com.seedrow.spacer.domain.repository.SettingsRepository
import com.seedrow.spacer.domain.usecase.CalculateGridUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val garden: GardenRepository,
    private val settings: SettingsRepository,
    private val calculateGrid: CalculateGridUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var revealJob: Job? = null

    init {
        viewModelScope.launch {
            combine(garden.draft, settings.units) { draft, units ->
                val grid = calculateGrid(
                    lengthCm = draft.bed.lengthCm,
                    widthCm = draft.bed.widthCm,
                    inRowCm = draft.inRowCm,
                    betweenRowCm = draft.betweenRowCm
                )
                GameUiState(
                    bedName = draft.bed.name,
                    cropName = draft.crop.name,
                    spriteIndex = draft.crop.spriteIndex,
                    lengthCm = draft.bed.lengthCm,
                    widthCm = draft.bed.widthCm,
                    inRowCm = draft.inRowCm,
                    betweenRowCm = draft.betweenRowCm,
                    gridKind = draft.gridKind,
                    rows = grid.rowsFor(draft.gridKind),
                    columns = grid.columns,
                    squarePlants = grid.squarePlants,
                    offsetPlants = grid.offsetPlants,
                    gainPercent = grid.gainPercent,
                    fits = grid.fits,
                    units = units
                )
            }.collect { next ->
                _state.value = next
                startReveal()
            }
        }
    }

    fun onEnter() {
        val current = _state.value
        if (!current.paused && current.revealedRows < current.rows) {
            startReveal()
        }
    }

    fun pause() {
        revealJob?.cancel()
        revealJob = null
        _state.update { it.copy(paused = true) }
    }

    fun togglePause() {
        if (_state.value.paused) {
            _state.update { it.copy(paused = false) }
            startReveal()
        } else {
            pause()
        }
    }

    fun selectGrid(kind: GridKind) {
        if (kind == _state.value.gridKind) return
        garden.setGridKind(kind)
    }

    private fun startReveal() {
        revealJob?.cancel()
        revealJob = viewModelScope.launch {
            while (_state.value.revealedRows < _state.value.rows) {
                delay(GameConfig.ROW_STAGGER_MS)
                _state.update { it.copy(revealedRows = it.revealedRows + 1) }
            }
        }
    }

    override fun onCleared() {
        revealJob?.cancel()
        revealJob = null
        super.onCleared()
    }
}
