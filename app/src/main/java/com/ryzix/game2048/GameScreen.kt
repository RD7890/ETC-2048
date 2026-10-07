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

@Composable
fun GameScreen() {
    val navController = LocalNavController.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val game = remember { Game2048(4) }
    var score by remember { mutableIntStateOf(0) }
    var best by remember { mutableIntStateOf(0) }
    var tiles by remember { mutableStateOf(game.tiles.toList()) }
    var gameOver by remember { mutableStateOf(false) }
    var hasWon by remember { mutableStateOf(false) }

    fun handleMove(dir: Dir) {
        val res = game.move(dir)
        if (!res.moved) return
        score = game.score
        if (score > best) best = score
        tiles = game.tiles.toList()
        if (res.scoreGained > 0) Sfx.merge(res.maxMergedVal) else Sfx.slide()
        kotlinx.coroutines.MainScope().launch {
            kotlinx.coroutines.delay(150)
            game.settle(); tiles = game.tiles.toList()
            gameOver = game.isGameOver; hasWon = game.hasWon
        }
    }
    LaunchedEffect(Unit) { game.reset(); tiles = game.tiles.toList(); score = 0 }

    Surface(modifier = Modifier.fillMaxSize(), color = C.shell) {
        Box(modifier = Modifier.fillMaxSize()) {
        var dragStart by remember { mutableStateOf(Offset.Zero) }
        Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { dragStart = it },
                onDragEnd = {
                    val delta = dragStart
                    if (kotlin.math.abs(delta.x) > kotlin.math.abs(delta.y)) {
                        if (delta.x > 30) handleMove(Dir.RIGHT) else if (delta.x < -30) handleMove(Dir.LEFT)
                    } else {
                        if (delta.y > 30) handleMove(Dir.DOWN) else if (delta.y < -30) handleMove(Dir.UP)
                    }
                },
                onDrag = { _, delta -> dragStart += delta }
            )
        }) {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BtnGray(icon = Icons.Rounded.Home, onClick = { navController.popBackStack() })
                    ScoreBox(label = "SCORE", value = score)
                    ScoreBox(label = "BEST", value = best)
                }
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally) {
                        GameBoardView(game = game, boardSize = 320.dp, gap = 10.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        DPadView(onMove = { dir -> handleMove(dir) })
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Btn3D(
                                label = "Undo",
                                bg = C.yellowMain, shadow = C.yellowShadow, fg = Color(0xFF4B3800),
                                icon = { Icon(Icons.Rounded.RotateLeft, contentDescription = null, tint = Color(0xFF4B3800), modifier = Modifier.size(20.dp)) },
                                onClick = { if (game.canUndo) { game.undo(); tiles = game.tiles.toList(); score = game.score } }
                            )
                            Btn3D(
                                label = "",
                                bg = C.blueMain, shadow = C.blueShadow, fg = Color.White,
                                icon = { Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) },
                                onClick = { /* hint */ }
                            )
                            Btn3D(
                                label = "Restart",
                                bg = C.primary, shadow = C.primaryShadow, fg = Color.White,
                                icon = { Icon(Icons.Rounded.RotateRight, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) },
                                onClick = { game.reset(); tiles = game.tiles.toList(); score = 0 }
                            )
                        }
                    }
                }
            }
        }
        }
    }
}
