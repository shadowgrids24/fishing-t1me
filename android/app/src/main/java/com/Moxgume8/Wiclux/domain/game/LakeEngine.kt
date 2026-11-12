package com.Moxgume8.Wiclux.domain.game

import com.Moxgume8.Wiclux.core.config.GameConfig
import com.Moxgume8.Wiclux.domain.model.Lane
import com.Moxgume8.Wiclux.domain.model.LevelSpec
import com.Moxgume8.Wiclux.domain.model.Rarity
import com.Moxgume8.Wiclux.domain.model.Species
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

enum class LakePhase { IDLE, DROPPING, REELING, SNAPPED, FINISHED }

data class FishView(
    val id: Int,
    val speciesId: String,
    val nameRes: Int,
    val spriteRes: Int,
    val tint: Long,
    val left: Float,
    val top: Float,
    val sizeDp: Float,
    val facingRight: Boolean,
    val alpha: Float
)

data class ShardView(
    val id: Int,
    val left: Float,
    val top: Float,
    val sizeDp: Float,
    val spin: Float
)

data class RippleView(
    val centerX: Float,
    val golden: Boolean,
    val open: Boolean,
    val preFlash: Boolean
)

data class LakeView(
    val fish: List<FishView>,
    val shards: List<ShardView>,
    val rodX: Float,
    val hookX: Float,
    val hookY: Float,
    val phase: LakePhase,
    val ripple: RippleView?,
    val canJerk: Boolean,
    val width: Float,
    val height: Float,
    val lanes: List<Lane>,
    val hookedSpriteRes: Int,
    val hookedTint: Long,
    val hookedSizeDp: Float
)

sealed interface LakeEvent {
    data class Caught(
        val species: Species,
        val rippleHit: Boolean,
        val golden: Boolean,
        val x: Float,
        val y: Float
    ) : LakeEvent

    data object Snagged : LakeEvent

    data object Missed : LakeEvent
}

private class FishEntity(
    val id: Int,
    val species: Species,
    var centerX: Float,
    val centerY: Float,
    var speed: Float,
    var movingRight: Boolean,
    val bobPhase: Float
)

private class ShardEntity(
    val id: Int,
    var centerX: Float,
    val centerY: Float,
    var speed: Float,
    var movingRight: Boolean,
    val sizeDp: Float,
    val lane: Lane
)

class LakeEngine(
    private val level: LevelSpec,
    private val catalogue: List<Species>,
    attempt: Int
) {
    val seed: Long = level.id * 7919L xor (attempt.toLong() * 104729L)

    private val random = Random(seed)
    private val fish = mutableListOf<FishEntity>()
    private val shards = mutableListOf<ShardEntity>()
    private val respawnQueue = mutableListOf<Pair<Lane, Long>>()

    private var width = 0f
    private var height = 0f
    private var ready = false
    private var elapsedMs = 0L

    private var sweepSpeed = GameConfig.SWEEP_SPEED_DP_S
    private var descentSpeed = GameConfig.DESCENT_SPEED_DP_S
    private var highContrast = false

    private var rodX = 0f
    private var rodRight = true
    private var hookX = 0f
    private var hookY = 0f
    private var jerkAvailable = false
    private var snapTimer = 0L
    private var phase = LakePhase.IDLE

    private var hooked: Species? = null
    private var dropOnRipple = false
    private var dropOnGolden = false

    private var rippleStage = 0
    private var rippleTimer = GameConfig.RIPPLE_GAP_MIN_MS
    private var rippleX = 0f
    private var rippleGolden = false
    private var goldenArmed = false
    private var forceRareDeep = false

    private var nextId = 1

    fun configure(lakeWidth: Float, lakeHeight: Float, swift: Boolean, contrast: Boolean) {
        if (lakeWidth <= 0f || lakeHeight <= 0f) return
        val changed = abs(lakeWidth - width) > 1f || abs(lakeHeight - height) > 1f
        width = lakeWidth
        height = lakeHeight
        applySettings(swift, contrast)
        if (!ready || changed) {
            ready = true
            rodX = width * 0.5f
            hookX = rodX
            hookY = 0f
            phase = LakePhase.IDLE
            populate()
        }
    }

    fun applySettings(swift: Boolean, contrast: Boolean) {
        sweepSpeed = if (swift) GameConfig.SWEEP_SPEED_SWIFT_DP_S else GameConfig.SWEEP_SPEED_DP_S
        descentSpeed = if (swift) GameConfig.DESCENT_SPEED_SWIFT_DP_S else GameConfig.DESCENT_SPEED_DP_S
        highContrast = contrast
    }

    fun isReady(): Boolean = ready

    fun phase(): LakePhase = phase

    fun armGoldenRipple() {
        goldenArmed = true
    }

    fun clearGoldenRipple() {
        goldenArmed = false
    }

    fun finish() {
        phase = LakePhase.FINISHED
    }

    fun drop(): Boolean {
        if (!ready || phase != LakePhase.IDLE) return false
        hookX = rodX
        hookY = 0f
        jerkAvailable = true
        dropOnRipple = rippleStage == 2 && abs(rodX - rippleX) <= GameConfig.RIPPLE_HIT_RADIUS_DP
        dropOnGolden = dropOnRipple && rippleGolden
        if (dropOnRipple) closeRipple()
        phase = LakePhase.DROPPING
        return true
    }

    fun jerk(): Boolean {
        if (phase != LakePhase.DROPPING || !jerkAvailable) return false
        val lane = laneAt(hookY)
        val target = fish
            .filter { it.species.lane == lane }
            .minByOrNull { abs(it.centerX - hookX) }
        jerkAvailable = false
        if (target == null) return true
        val delta = target.centerX - hookX
        val shift = max(-GameConfig.JERK_DISTANCE_DP, min(GameConfig.JERK_DISTANCE_DP, delta))
        hookX = clampX(hookX + shift)
        return true
    }

    fun step(deltaMs: Long): LakeEvent? {
        if (!ready || phase == LakePhase.FINISHED) return null
        elapsedMs += deltaMs
        val dt = deltaMs / 1000f
        advanceFish(dt)
        advanceShards(dt)
        advanceRespawns(deltaMs)
        advanceRipple(deltaMs)
        if (phase == LakePhase.IDLE) {
            advanceRod(dt)
            return null
        }
        if (phase == LakePhase.DROPPING) return advanceHook(dt)
        if (phase == LakePhase.REELING) {
            hookY -= GameConfig.REEL_SPEED_DP_S * dt
            if (hookY <= 0f) {
                hookY = 0f
                hooked = null
                phase = LakePhase.IDLE
            }
            return null
        }
        if (phase == LakePhase.SNAPPED) {
            snapTimer -= deltaMs
            if (snapTimer <= 0L) {
                hookY = 0f
                phase = LakePhase.IDLE
            }
        }
        return null
    }

    fun view(): LakeView {
        val fishViews = fish.map { entity ->
            val lane = entity.species.lane
            val bob = sin((elapsedMs / 1400f) * TWO_PI + entity.bobPhase) * 3f
            val size = entity.species.sizeDp
            FishView(
                id = entity.id,
                speciesId = entity.species.id,
                nameRes = entity.species.nameRes,
                spriteRes = entity.species.spriteRes,
                tint = entity.species.tint,
                left = entity.centerX - size * 0.5f,
                top = entity.centerY + bob - size * 0.5f,
                sizeDp = size,
                facingRight = entity.movingRight,
                alpha = if (highContrast) lane.contrastAlpha else lane.shadowAlpha
            )
        }
        val shardViews = shards.map { entity ->
            ShardView(
                id = entity.id,
                left = entity.centerX - entity.sizeDp * 0.5f,
                top = entity.centerY - entity.sizeDp * 0.5f,
                sizeDp = entity.sizeDp,
                spin = (entity.id * 37f + elapsedMs / 60f) % 360f
            )
        }
        val rippleView = if (rippleStage == 0) {
            null
        } else {
            RippleView(
                centerX = rippleX,
                golden = rippleGolden,
                open = rippleStage == 2,
                preFlash = rippleStage == 1
            )
        }
        val carried = hooked
        return LakeView(
            fish = fishViews,
            shards = shardViews,
            rodX = rodX,
            hookX = hookX,
            hookY = hookY,
            phase = phase,
            ripple = rippleView,
            canJerk = phase == LakePhase.DROPPING && jerkAvailable,
            width = width,
            height = height,
            lanes = level.lanes,
            hookedSpriteRes = carried?.spriteRes ?: 0,
            hookedTint = carried?.tint ?: 0L,
            hookedSizeDp = carried?.sizeDp ?: 0f
        )
    }

    private fun advanceRod(dt: Float) {
        val delta = sweepSpeed * level.speedScale * dt
        rodX += if (rodRight) delta else -delta
        if (rodX >= width - EDGE_MARGIN) {
            rodX = width - EDGE_MARGIN
            rodRight = false
        }
        if (rodX <= EDGE_MARGIN) {
            rodX = EDGE_MARGIN
            rodRight = true
        }
        hookX = rodX
    }

    private fun advanceHook(dt: Float): LakeEvent? {
        hookY += descentSpeed * dt
        val half = GameConfig.HOOK_BOX_DP * 0.5f
        for (shard in shards) {
            val reach = (shard.sizeDp + GameConfig.HOOK_BOX_DP) * 0.5f
            if (abs(shard.centerX - hookX) < reach && abs(shard.centerY - hookY) < reach) {
                phase = LakePhase.SNAPPED
                snapTimer = GameConfig.SNAP_RECOVER_MS
                return LakeEvent.Snagged
            }
        }
        val hit = fish.firstOrNull { entity ->
            val size = entity.species.sizeDp
            abs(entity.centerX - hookX) < size * 0.5f + half &&
                abs(entity.centerY - hookY) < size * 0.3f + half
        }
        if (hit != null) {
            fish.remove(hit)
            scheduleRespawn(hit.species.lane)
            hooked = hit.species
            phase = LakePhase.REELING
            return LakeEvent.Caught(
                species = hit.species,
                rippleHit = dropOnRipple,
                golden = dropOnGolden,
                x = hookX,
                y = hookY
            )
        }
        if (hookY >= height) {
            hookY = height
            phase = LakePhase.SNAPPED
            snapTimer = GameConfig.SNAP_RECOVER_MS
            return LakeEvent.Missed
        }
        return null
    }

    private fun advanceFish(dt: Float) {
        for (entity in fish) {
            val delta = entity.speed * level.speedScale * dt
            entity.centerX += if (entity.movingRight) delta else -delta
            val size = entity.species.sizeDp
            if (entity.movingRight && entity.centerX > width + size) entity.centerX = -size
            if (!entity.movingRight && entity.centerX < -size) entity.centerX = width + size
        }
    }

    private fun advanceShards(dt: Float) {
        for (entity in shards) {
            val delta = entity.speed * level.speedScale * dt
            entity.centerX += if (entity.movingRight) delta else -delta
            if (entity.movingRight && entity.centerX > width + entity.sizeDp) {
                entity.centerX = -entity.sizeDp
            }
            if (!entity.movingRight && entity.centerX < -entity.sizeDp) {
                entity.centerX = width + entity.sizeDp
            }
        }
    }

    private fun advanceRespawns(deltaMs: Long) {
        if (respawnQueue.isEmpty()) return
        val pending = respawnQueue.toList()
        respawnQueue.clear()
        for (item in pending) {
            val left = item.second - deltaMs
            if (left <= 0L) spawnFish(item.first, fromEdge = true) else respawnQueue.add(item.first to left)
        }
    }

    private fun advanceRipple(deltaMs: Long) {
        rippleTimer -= deltaMs
        if (rippleTimer > 0L) return
        if (rippleStage == 0) {
            rippleX = EDGE_MARGIN + random.nextFloat() * max(1f, width - EDGE_MARGIN * 2f)
            rippleGolden = goldenArmed
            if (rippleGolden) {
                goldenArmed = false
                forceRareDeep = true
            }
            rippleStage = 1
            rippleTimer = GameConfig.RIPPLE_PREFLASH_MS
            return
        }
        if (rippleStage == 1) {
            rippleStage = 2
            rippleTimer = if (rippleGolden) GameConfig.GOLDEN_RIPPLE_MS else GameConfig.RIPPLE_OPEN_MS
            return
        }
        closeRipple()
    }

    private fun closeRipple() {
        rippleStage = 0
        rippleGolden = false
        val span = GameConfig.RIPPLE_GAP_MAX_MS - GameConfig.RIPPLE_GAP_MIN_MS
        rippleTimer = GameConfig.RIPPLE_GAP_MIN_MS + (random.nextFloat() * span).toLong()
    }

    private fun scheduleRespawn(lane: Lane) {
        respawnQueue.add(lane to (600L + (random.nextFloat() * 800f).toLong()))
    }

    private fun populate() {
        var attempts = 0
        while (attempts < 20) {
            layout()
            if (hasCorridor()) return
            attempts++
        }
        fallbackLayout()
    }

    private fun layout() {
        fish.clear()
        shards.clear()
        respawnQueue.clear()
        for (lane in level.lanes) {
            val count = when (lane) {
                Lane.SHALLOW -> 2 + random.nextInt(3)
                Lane.MID -> 2 + random.nextInt(2)
                Lane.DEEP -> 1 + random.nextInt(2)
            }
            var spawned = 0
            while (spawned < count) {
                spawnFish(lane, fromEdge = false)
                spawned++
            }
        }
        var shardIndex = 0
        while (shardIndex < level.shardCount) {
            val lane = level.lanes[random.nextInt(level.lanes.size)]
            shards.add(
                ShardEntity(
                    id = nextId++,
                    centerX = random.nextFloat() * width,
                    centerY = laneCenter(lane) - 16f,
                    speed = 18f + random.nextFloat() * 16f,
                    movingRight = random.nextBoolean(),
                    sizeDp = 26f + random.nextFloat() * 18f,
                    lane = lane
                )
            )
            shardIndex++
        }
    }

    private fun fallbackLayout() {
        fish.clear()
        shards.clear()
        respawnQueue.clear()
        var index = 0
        for (lane in level.lanes) {
            val pool = poolFor(lane)
            if (pool.isEmpty()) continue
            val pick = pool[index % pool.size]
            fish.add(
                FishEntity(
                    id = nextId++,
                    species = pick,
                    centerX = width * 0.18f,
                    centerY = laneCenter(lane),
                    speed = lane.minSpeed,
                    movingRight = true,
                    bobPhase = index.toFloat()
                )
            )
            index++
        }
    }

    private fun spawnFish(lane: Lane, fromEdge: Boolean) {
        val pool = poolFor(lane)
        if (pool.isEmpty()) return
        val pick = if (lane == Lane.DEEP && forceRareDeep) {
            forceRareDeep = false
            pool.firstOrNull { it.rarity == Rarity.RARE } ?: pool[random.nextInt(pool.size)]
        } else {
            pool[random.nextInt(pool.size)]
        }
        val right = random.nextBoolean()
        val startX = if (fromEdge) {
            if (right) -pick.sizeDp else width + pick.sizeDp
        } else {
            random.nextFloat() * width
        }
        fish.add(
            FishEntity(
                id = nextId++,
                species = pick,
                centerX = startX,
                centerY = laneCenter(lane),
                speed = lane.minSpeed + random.nextFloat() * (lane.maxSpeed - lane.minSpeed),
                movingRight = right,
                bobPhase = random.nextFloat() * TWO_PI
            )
        )
    }

    private fun poolFor(lane: Lane): List<Species> = catalogue.filter { it.lane == lane }

    private fun laneCenter(lane: Lane): Float =
        height * (lane.topFraction + lane.bottomFraction) * 0.5f

    private fun laneAt(y: Float): Lane {
        val fraction = if (height <= 0f) 0f else y / height
        for (lane in level.lanes) {
            if (fraction <= lane.bottomFraction) return lane
        }
        return level.lanes[level.lanes.size - 1]
    }

    private fun clampX(value: Float): Float = max(EDGE_MARGIN, min(width - EDGE_MARGIN, value))

    private fun hasCorridor(): Boolean {
        if (!level.lanes.contains(Lane.DEEP)) return true
        var time = 0f
        while (time <= 4f) {
            var probe = 0
            while (probe < 24) {
                val x = width * (probe + 0.5f) / 24f
                if (clearAt(x, time)) return true
                probe++
            }
            time += 0.25f
        }
        return false
    }

    private fun clearAt(x: Float, time: Float): Boolean {
        for (entity in fish) {
            if (entity.species.lane == Lane.DEEP) continue
            val projected = project(entity.centerX, entity.speed, entity.movingRight, time)
            if (abs(projected - x) < (entity.species.sizeDp + GameConfig.HOOK_BOX_DP) * 0.5f) return false
        }
        for (entity in shards) {
            if (entity.lane == Lane.DEEP) continue
            val projected = project(entity.centerX, entity.speed, entity.movingRight, time)
            if (abs(projected - x) < (entity.sizeDp + GameConfig.HOOK_BOX_DP) * 0.5f) return false
        }
        return true
    }

    private fun project(start: Float, speed: Float, right: Boolean, time: Float): Float {
        if (width <= 0f) return start
        val travelled = speed * level.speedScale * time
        val raw = if (right) start + travelled else start - travelled
        var wrapped = raw % width
        if (wrapped < 0f) wrapped += width
        return wrapped
    }

    private companion object {
        const val EDGE_MARGIN = 24f
        const val TWO_PI = 6.2831855f
    }
}
