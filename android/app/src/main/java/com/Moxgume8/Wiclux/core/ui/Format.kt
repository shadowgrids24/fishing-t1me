package com.Moxgume8.Wiclux.core.ui

fun formatNumber(value: Int): String {
    val digits = value.toString()
    if (digits.length <= 3) return digits
    val builder = StringBuilder()
    var index = 0
    val offset = digits.length % 3
    while (index < digits.length) {
        if (index > 0 && (index - offset) % 3 == 0) builder.append(' ')
        builder.append(digits[index])
        index++
    }
    return builder.toString()
}

fun formatPercent(part: Int, total: Int): Int {
    if (total <= 0) return 0
    return (part * 100) / total
}
