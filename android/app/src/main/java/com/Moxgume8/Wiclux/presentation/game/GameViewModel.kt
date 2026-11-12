package com.Moxgume8.Wiclux.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Moxgume8.Wiclux.core.config.GameConfig
import com.Moxgume8.Wiclux.domain.game.LakeEngine
import com.Moxgume8.Wiclux.domain.game.LakeEvent
import com.Moxgume8.Wiclux.domain.game.LakePhase
import com.Moxgume8.Wiclux.domain.model.CatchRecord
import com.Moxgume8.Wiclux.domain.model.LevelSpec
import com.Moxgume8.Wiclux.domain.model.Species
import com.Moxgume8.Wiclux.domain.repository.ProgressRepository
import com.Moxgume8.Wiclux.domain.repository.SettingsRepository
import com.Moxgume8.Wiclux.domain.usecase.ResolveTripUseCase
import com.Moxgume8.Wiclux.domain.usecase.ScoreCatchUseCase
import com.Moxgume8.Wiclux.domain.usecase.UnlockLevelUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val scoreCatch: ScoreCatchUseCase,
    private val resolveTrip: ResolveTripUseCase,
    private val unlockLevel: UnlockLevelUseCase,
    private val catalogue: List<Species>,
    private val levels: List<LevelSpec>
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private val catches = mutableListOf<CatchRecord>()

    private var engine: LakeEngine? = null
    private var loopJob: Job? = null
    private var level: LevelSpec = levels[0]
    private var attempt = 0
    private var streak = 0
    private var bestStreak = 0
    private var flashCounter = 0
    private var lakeWidth = 0f
    private var lakeHeight = 0f
    private var tripElapsedMs = 0L

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                engine?.applySettings(settings.swiftHook, settings.highContrast)
                _state.update { it.copy(settings = settings) }
            }
        }
    }

    fun prepare(levelId: Int) {
        val current = engine
        if (current != null && level.id == levelId && !_state.value.finished) {
            resume()
            return
        }
        beginTrip(levelId)
    }

    fun resize(width: Float, height: Float) {
        if (width <= 0f || height <= 0f) return
        lakeWidth = width
        lakeHeight = height
        val settings = settingsRepository.settings.value
        engine?.configure(width, height, settings.swiftHook, settings.highContrast)
        publish()
    }

    fun tapLake() {
        val current = engine ?: return
        val snapshot = _state.value
        if (snapshot.paused || snapshot.finished) return
        if (current.phase() == LakePhase.DROPPING) current.jerk() else current.drop()
        publish()
    }

    fun setPaused(paused: Boolean) {
        if (_state.value.finished) return
        _state.update { it.copy(paused = paused) }
        if (paused) stopLoop() else runLoop()
    }

    fun pause() {
        stopLoop()
    }

    fun resume() {
        val snapshot = _state.value
        if (snapshot.finished || snapshot.paused) return
        runLoop()
    }

    override fun onCleared() {
        stopLoop()
        super.onCleared()
    }

    private fun beginTrip(levelId: Int) {
        stopLoop()
        level = levels.firstOrNull { it.id == levelId } ?: levels[0]
        attempt++
        catches.clear()
        streak = 0
        bestStreak = 0
        tripElapsedMs = 0L
        progressRepository.rememberLevel(level.id)
        val settings = settingsRepository.settings.value
        val created = LakeEngine(level, catalogue, attempt)
        engine = created
        if (lakeWidth > 0f && lakeHeight > 0f) {
            created.configure(lakeWidth, lakeHeight, settings.swiftHook, settings.highContrast)
        }
        _state.value = GameUiState(
            levelId = level.id,
            levelNameRes = level.nameRes,
            depthM = level.depthM,
            targetGrams = level.targetGrams,
            grams = 0,
            points = 0,
            linesLeft = GameConfig.LINES_PER_TRIP,
            maxLines = GameConfig.LINES_PER_TRIP,
            streak = 0,
            phase = LakePhase.IDLE,
            lake = created.view(),
            settings = settings,
            paused = false,
            finished = false,
            flash = null,
            shakeTick = 0,
            hintBright = true
        )
        runLoop()
    }

    private fun runLoop() {
        if (loopJob?.isActive == true) return
        loopJob = viewModelScope.launch {
            while (isActive) {
                delay(GameConfig.FRAME_MS)
                val current = engine ?: break
                tripElapsedMs += GameConfig.FRAME_MS
                val event = current.step(GameConfig.FRAME_MS)
                if (event != null) handleEvent(event)
                if (_state.value.finished) break
                publish()
            }
        }
    }

    private fun stopLoop() {
        loopJob?.cancel()
        loopJob = null
    }

    private fun handleEvent(event: LakeEvent) {
        when (event) {
            is LakeEvent.Caught -> onCaught(event)
            LakeEvent.Snagged -> onLineLost()
            LakeEvent.Missed -> onLineLost()
        }
    }

    private fun onCaught(event: LakeEvent.Caught) {
        val settings = _state.value.settings
        val species = event.species
        val points = scoreCatch.points(species, event.rippleHit, event.golden, settings)
        val multiplier = scoreCatch.multiplier(species, event.rippleHit, event.golden, settings)
        val knownIds = progressRepository.progress.value.species.map { it.speciesId }
        val firstEver = !knownIds.contains(species.id) && catches.none { it.speciesId == species.id }
        catches.add(
            CatchRecord(
                speciesId = species.id,
                grams = species.weightG,
                points = points,
                multiplier = multiplier,
                rippleHit = event.rippleHit,
                golden = event.golden,
                firstEver = firstEver
            )
        )
        streak++
        if (streak > bestStreak) bestStreak = streak
        if (streak >= GameConfig.STREAK_FOR_GOLDEN) engine?.armGoldenRipple()
        flashCounter++
        val totalGrams = catches.sumOf { it.grams }
        val totalPoints = catches.sumOf { it.points }
        _state.update { current ->
            current.copy(
                grams = totalGrams,
                points = totalPoints,
                streak = streak,
                flash = CatchFlash(
                    id = flashCounter,
                    grams = species.weightG,
                    multiplier = multiplier,
                    x = event.x,
                    y = event.y,
                    golden = event.golden
                )
            )
        }
        if (totalGrams >= level.targetGrams) finishTrip()
    }

    private fun onLineLost() {
        streak = 0
        engine?.clearGoldenRipple()
        val remaining = (_state.value.linesLeft - 1).coerceAtLeast(0)
        _state.update { current ->
            current.copy(
                linesLeft = remaining,
                streak = 0,
                shakeTick = current.shakeTick + 1,
                flash = null
            )
        }
        if (remaining <= 0) finishTrip()
    }

    private fun finishTrip() {
        stopLoop()
        engine?.finish()
        val progress = progressRepository.progress.value
        val trip = resolveTrip(
            level = level,
            catches = catches.toList(),
            bestStreak = bestStreak,
            unlockedSpots = progress.unlockedSpots,
            totalLevels = levels.size
        )
        unlockLevel(trip)
        _state.update { current ->
            current.copy(finished = true, phase = LakePhase.FINISHED, lake = engine?.view())
        }
        viewModelScope.launch { eventChannel.send(GameEvent.Finished(trip)) }
    }

    private fun publish() {
        val current = engine ?: return
        _state.update { snapshot ->
            snapshot.copy(
                lake = current.view(),
                phase = current.phase(),
                hintBright = tripElapsedMs < 6000L
            )
        }
    }
}
