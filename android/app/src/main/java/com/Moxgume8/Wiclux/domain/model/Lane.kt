package com.Moxgume8.Wiclux.domain.model

enum class Lane(
    val topFraction: Float,
    val bottomFraction: Float,
    val minSpeed: Float,
    val maxSpeed: Float,
    val shadowAlpha: Float,
    val contrastAlpha: Float
) {
    SHALLOW(0.30f, 0.45f, 34f, 52f, 0.92f, 1.0f),
    MID(0.50f, 0.68f, 26f, 40f, 0.78f, 0.92f),
    DEEP(0.72f, 0.90f, 18f, 30f, 0.62f, 0.84f)
}
