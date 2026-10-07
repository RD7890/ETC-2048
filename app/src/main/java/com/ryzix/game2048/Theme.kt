package com.ryzix.game2048

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.ryzix.game2048.R

object C {
    val primary = Color(0xFFFF2541)
    val primaryShadow = Color(0xFFD8132F)
    val bg = Color(0xFFFFFFFF)
    val cardBg = Color(0xFFF7F9FA)
    val border = Color(0xFFE5E9F0)
    val dark = Color(0xFF1E2029)
    val darkShadow = Color(0xFF12141A)
    val blueMain = Color(0xFF1CB0F6)
    val blueShadow = Color(0xFF1899D6)
    val yellowMain = Color(0xFFFFC800)
    val yellowShadow = Color(0xFFE5B200)
    val greenMain = Color(0xFF58CC02)
    val greenShadow = Color(0xFF46A302)
    val textDark = Color(0xFF2D3748)
    val textLight = Color(0xFF718096)
    val boardBg = Color(0xFF1E2029)
    val cellBg = Color(0xFF2D303E)
    val shell = Color(0xFF111319)
}

data class TileStyle(val bg: Color, val fg: Color, val shadow: Color, val border: Color? = null)

val kTileStyles: Map<Int, TileStyle> = mapOf(
    2 to TileStyle(Color(0xFFE2E8F0), Color(0xFF2D3748), Color(0xFFCBD5E1)),
    4 to TileStyle(Color(0xFFFFE2E2), Color(0xFFD8132F), Color(0xFFFFC1C1)),
    8 to TileStyle(Color(0xFFFFF4D1), Color(0xFFD79200), Color(0xFFFFE390)),
    16 to TileStyle(Color(0xFF1CB0F6), Color(0xFFFFFFFF), Color(0xFF1899D6)),
    32 to TileStyle(Color(0xFFFF9600), Color(0xFFFFFFFF), Color(0xFFE58600)),
    64 to TileStyle(Color(0xFFFF2541), Color(0xFFFFFFFF), Color(0xFFD8132F)),
    128 to TileStyle(Color(0xFF58CC02), Color(0xFFFFFFFF), Color(0xFF46A302)),
    256 to TileStyle(Color(0xFFFFC800), Color(0xFFFFFFFF), Color(0xFFE5B200)),
    512 to TileStyle(Color(0xFFCE82FF), Color(0xFFFFFFFF), Color(0xFFAF61E1)),
    1024 to TileStyle(Color(0xFF00E676), Color(0xFFFFFFFF), Color(0xFF00B259)),
    2048 to TileStyle(Color(0xFF1E2029), Color(0xFFFFC800), Color(0xFF12141A)),
)

fun styleFor(v: Int) = kTileStyles[v] ?: kTileStyles[2048]!!

fun buildFontFamily(): FontFamily? = FontFamily(Font(R.font.fredoka_500, FontWeight.W500), Font(R.font.fredoka_600, FontWeight.W600), Font(R.font.fredoka_700, FontWeight.W700))
