package com.Moxgume8.Wiclux.data.sample

import com.Moxgume8.Wiclux.R
import com.Moxgume8.Wiclux.domain.model.Lane
import com.Moxgume8.Wiclux.domain.model.LevelSpec
import com.Moxgume8.Wiclux.domain.model.Rarity
import com.Moxgume8.Wiclux.domain.model.Species

object SampleData {

    val species: List<Species> = listOf(
        Species(
            id = "ice_perch",
            nameRes = R.string.species_ice_perch,
            spriteRes = R.drawable.sprite_1,
            tint = 0xFF4E7A8C,
            lane = Lane.SHALLOW,
            rarity = Rarity.COMMON,
            weightG = 80,
            basePoints = 40,
            sizeDp = 48f
        ),
        Species(
            id = "ruby_perch",
            nameRes = R.string.species_ruby_perch,
            spriteRes = R.drawable.sprite_1,
            tint = 0xFFC25A4A,
            lane = Lane.MID,
            rarity = Rarity.RARE,
            weightG = 240,
            basePoints = 120,
            sizeDp = 58f
        ),
        Species(
            id = "silver_roach",
            nameRes = R.string.species_silver_roach,
            spriteRes = R.drawable.sprite_2,
            tint = 0xFFB8C9D2,
            lane = Lane.SHALLOW,
            rarity = Rarity.COMMON,
            weightG = 60,
            basePoints = 30,
            sizeDp = 42f
        ),
        Species(
            id = "glass_roach",
            nameRes = R.string.species_glass_roach,
            spriteRes = R.drawable.sprite_2,
            tint = 0xFF7FE8C5,
            lane = Lane.MID,
            rarity = Rarity.UNCOMMON,
            weightG = 150,
            basePoints = 70,
            sizeDp = 50f
        ),
        Species(
            id = "lake_pike",
            nameRes = R.string.species_lake_pike,
            spriteRes = R.drawable.sprite_3,
            tint = 0xFF3F6B55,
            lane = Lane.MID,
            rarity = Rarity.UNCOMMON,
            weightG = 300,
            basePoints = 110,
            sizeDp = 72f
        ),
        Species(
            id = "frost_pike",
            nameRes = R.string.species_frost_pike,
            spriteRes = R.drawable.sprite_3,
            tint = 0xFF9DCEDB,
            lane = Lane.DEEP,
            rarity = Rarity.RARE,
            weightG = 520,
            basePoints = 210,
            sizeDp = 78f
        ),
        Species(
            id = "arctic_char",
            nameRes = R.string.species_arctic_char,
            spriteRes = R.drawable.sprite_4,
            tint = 0xFF5C87A8,
            lane = Lane.MID,
            rarity = Rarity.UNCOMMON,
            weightG = 260,
            basePoints = 100,
            sizeDp = 56f
        ),
        Species(
            id = "aurora_char",
            nameRes = R.string.species_aurora_char,
            spriteRes = R.drawable.sprite_4,
            tint = 0xFF8E7FD8,
            lane = Lane.DEEP,
            rarity = Rarity.RARE,
            weightG = 430,
            basePoints = 180,
            sizeDp = 64f
        ),
        Species(
            id = "stone_burbot",
            nameRes = R.string.species_stone_burbot,
            spriteRes = R.drawable.sprite_5,
            tint = 0xFF6B6150,
            lane = Lane.SHALLOW,
            rarity = Rarity.COMMON,
            weightG = 120,
            basePoints = 55,
            sizeDp = 54f
        ),
        Species(
            id = "deep_burbot",
            nameRes = R.string.species_deep_burbot,
            spriteRes = R.drawable.sprite_5,
            tint = 0xFF2F4A52,
            lane = Lane.DEEP,
            rarity = Rarity.RARE,
            weightG = 380,
            basePoints = 160,
            sizeDp = 68f
        )
    )

    val levels: List<LevelSpec> = listOf(
        LevelSpec(
            id = 1,
            nameRes = R.string.spot_shore_crack,
            hintRes = R.string.spot_hint_shore_crack,
            depthM = 2,
            targetGrams = 400,
            speedScale = 1.00f,
            shardCount = 0,
            lanes = listOf(Lane.SHALLOW, Lane.MID)
        ),
        LevelSpec(
            id = 2,
            nameRes = R.string.spot_reed_hollow,
            hintRes = R.string.spot_hint_reed_hollow,
            depthM = 4,
            targetGrams = 700,
            speedScale = 1.10f,
            shardCount = 1,
            lanes = listOf(Lane.SHALLOW, Lane.MID)
        ),
        LevelSpec(
            id = 3,
            nameRes = R.string.spot_blue_hole,
            hintRes = R.string.spot_hint_blue_hole,
            depthM = 7,
            targetGrams = 1100,
            speedScale = 1.20f,
            shardCount = 2,
            lanes = listOf(Lane.SHALLOW, Lane.MID, Lane.DEEP)
        ),
        LevelSpec(
            id = 4,
            nameRes = R.string.spot_pressure_ridge,
            hintRes = R.string.spot_hint_pressure_ridge,
            depthM = 9,
            targetGrams = 1600,
            speedScale = 1.32f,
            shardCount = 3,
            lanes = listOf(Lane.SHALLOW, Lane.MID, Lane.DEEP)
        ),
        LevelSpec(
            id = 5,
            nameRes = R.string.spot_sunken_pine,
            hintRes = R.string.spot_hint_sunken_pine,
            depthM = 12,
            targetGrams = 2200,
            speedScale = 1.45f,
            shardCount = 3,
            lanes = listOf(Lane.SHALLOW, Lane.MID, Lane.DEEP)
        ),
        LevelSpec(
            id = 6,
            nameRes = R.string.spot_black_depth,
            hintRes = R.string.spot_hint_black_depth,
            depthM = 16,
            targetGrams = 3000,
            speedScale = 1.60f,
            shardCount = 4,
            lanes = listOf(Lane.SHALLOW, Lane.MID, Lane.DEEP)
        )
    )

    fun speciesById(id: String): Species? = species.firstOrNull { it.id == id }

    fun levelById(id: Int): LevelSpec = levels.firstOrNull { it.id == id } ?: levels[0]
}
