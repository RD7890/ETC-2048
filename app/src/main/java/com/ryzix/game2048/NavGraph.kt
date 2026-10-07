package com.ryzix.game2048

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.navigation.navArgument

val LocalNavController = compositionLocalOf<NavController> { error("No NavController") }

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController = navController, startDestination = "None") {
        composable("Home") { HomeScreen() }
        composable("Game?grid={grid}", arguments = listOf(navArgument("grid") { defaultValue = "4" })) { GameScreen() }
        }
    }
}
