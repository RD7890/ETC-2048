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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

// ── TileView ─────────────────────────────────────────────────────────────────
@Composable
fun TileView(tile: TileData, size: Dp, fontSize: TextUnit, modifier: Modifier = Modifier) {
    val style = styleFor(tile.value)
    val scale = remember { Animatable(if (tile.isNew) 0.2f else 1f) }
    LaunchedEffect(tile.id) { if (tile.isNew) scale.animateTo(1f, tween(180, easing = FastOutSlowInEasing)) }
    val mergeScale = remember { Animatable(1f) }
    LaunchedEffect(tile.value) {
        if (tile.isMerged) { mergeScale.animateTo(1.18f, tween(90)); mergeScale.animateTo(1f, tween(90)) }
    }
    Box(
        modifier = modifier.size(size)
            .graphicsLayer { scaleX = scale.value * mergeScale.value; scaleY = scale.value * mergeScale.value }
            .clip(RoundedCornerShape(12.dp)).background(style.bg)
            .then(if (style.border != null) Modifier.border(BorderStroke(3.dp, style.border), RoundedCornerShape(12.dp)) else Modifier),
        contentAlignment = Alignment.Center
    ) { Text(tile.value.toString(), fontSize = fontSize, fontWeight = FontWeight.W700, color = style.fg) }
}

// ── GameBoardView ─────────────────────────────────────────────────────────────
@Composable
fun GameBoardView(game: Game2048, boardSize: Dp, gap: Dp) {
    val density = LocalDensity.current
    val boardPx = with(density) { boardSize.toPx() }
    val boardPad = with(density) { 12.dp.toPx() }
    val gapPx = with(density) { gap.toPx() }
    val cellPx = (boardPx - boardPad * 2 - gapPx * (game.gridSize - 1)) / game.gridSize
    val cellDp = with(density) { cellPx.toDp() }
    val fontSize = when (game.gridSize) { 5 -> 16.sp; 3 -> 32.sp; else -> 26.sp }
    Box(modifier = Modifier.size(boardSize).clip(RoundedCornerShape(20.dp)).background(C.boardBg).padding(12.dp)) {
        for (r in 0 until game.gridSize) for (c in 0 until game.gridSize) {
            val x = with(density) { (c * (cellPx + gapPx)).toDp() }
            val y = with(density) { (r * (cellPx + gapPx)).toDp() }
            Box(modifier = Modifier.offset(x, y).size(cellDp).clip(RoundedCornerShape(12.dp)).background(C.cellBg))
        }
        game.tiles.forEach { tile ->
            key(tile.id) {
                val tx by animateDpAsState(with(density) { (tile.x*(cellPx+gapPx)).toDp() }, tween(150), label="tx")
                val ty by animateDpAsState(with(density) { (tile.y*(cellPx+gapPx)).toDp() }, tween(150), label="ty")
                TileView(tile=tile, size=cellDp, fontSize=fontSize, modifier=Modifier.offset(tx,ty))
            }
        }
    }
}

// ── Btn3D ─────────────────────────────────────────────────────────────────────
@Composable
fun Btn3D(label: String="", bg: Color=C.primary, shadow: Color=C.primaryShadow, fg: Color=Color.White,
          icon: @Composable (()->Unit)?=null, modifier: Modifier=Modifier, onClick: ()->Unit) {
    var pressed by remember { mutableStateOf(false) }
    val top by animateDpAsState(if (pressed) 4.dp else 0.dp, tween(80), label="btn3d")
    Box(modifier=modifier) {
        Box(modifier=Modifier.matchParentSize().padding(top=4.dp).clip(RoundedCornerShape(14.dp)).background(shadow))
        Box(modifier=Modifier.padding(top=top).clip(RoundedCornerShape(14.dp)).background(bg)
            .clickable(onClick={ pressed=true; onClick(); pressed=false })
            .padding(horizontal=20.dp, vertical=14.dp), contentAlignment=Alignment.Center) {
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp), verticalAlignment=Alignment.CenterVertically) {
                icon?.invoke()
                if (label.isNotBlank()) Text(label, color=fg, fontWeight=FontWeight.W700, fontSize=16.sp)
            }
        }
    }
}

// ── BtnGray ───────────────────────────────────────────────────────────────────
@Composable
fun BtnGray(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: ()->Unit) {
    Box(modifier=Modifier.clip(RoundedCornerShape(10.dp)).background(C.cardBg)
        .border(BorderStroke(2.dp, C.border), RoundedCornerShape(10.dp))
        .clickable(onClick=onClick).padding(horizontal=14.dp, vertical=8.dp),
        contentAlignment=Alignment.Center) {
        Icon(icon, contentDescription=null, tint=C.textLight, modifier=Modifier.size(20.dp))
    }
}

// ── ScoreBox ──────────────────────────────────────────────────────────────────
@Composable
fun ScoreBox(label: String, value: Int) {
    Box(modifier=Modifier.clip(RoundedCornerShape(14.dp)).background(C.cardBg)
        .border(BorderStroke(2.dp, C.border), RoundedCornerShape(14.dp))
        .padding(horizontal=16.dp, vertical=8.dp)) {
        Column(horizontalAlignment=Alignment.CenterHorizontally) {
            Text(label, fontSize=11.sp, fontWeight=FontWeight.W700, color=C.textLight)
            Text("$value", fontSize=20.sp, fontWeight=FontWeight.W700, color=C.textDark)
        }
    }
}

// ── DPadView ──────────────────────────────────────────────────────────────────
@Composable
fun DPadView(onMove: (Dir)->Unit) {
    @Composable fun DBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, dir: Dir) {
        BtnGray(icon=icon, onClick={ onMove(dir) })
    }
    Column(horizontalAlignment=Alignment.CenterHorizontally) {
        DBtn(Icons.Rounded.KeyboardArrowUp, Dir.UP)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
            DBtn(Icons.Rounded.KeyboardArrowLeft, Dir.LEFT)
            DBtn(Icons.Rounded.KeyboardArrowDown, Dir.DOWN)
            DBtn(Icons.Rounded.KeyboardArrowRight, Dir.RIGHT)
        }
    }
}

// ── LevelCard ─────────────────────────────────────────────────────────────────
@Composable
fun LevelCard(icon: androidx.compose.ui.graphics.vector.ImageVector, iconBg: Color, iconShadow: Color,
              title: String, subtitle: String, onClick: ()->Unit) {
    var pressed by remember { mutableStateOf(false) }
    val lift by animateDpAsState(if (pressed) 3.dp else 0.dp, tween(90), label="lift")
    Box(modifier=Modifier.fillMaxWidth().offset(y=lift).clip(RoundedCornerShape(20.dp))
        .background(Color.White).border(BorderStroke(2.dp, C.border), RoundedCornerShape(20.dp))
        .clickable{ pressed=true; onClick(); pressed=false }.padding(16.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) {
            Box(modifier=Modifier.size(56.dp).shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)).background(iconBg), contentAlignment=Alignment.Center) {
                Icon(icon, contentDescription=null, tint=Color.White, modifier=Modifier.size(28.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier=Modifier.weight(1f)) {
                Text(title, fontSize=18.sp, fontWeight=FontWeight.W700, color=C.textDark)
                Text(subtitle, fontSize=13.sp, fontWeight=FontWeight.W600, color=C.textLight)
            }
            Icon(Icons.Rounded.ChevronRight, contentDescription=null, tint=C.textLight)
        }
    }
}

// ── StatBadge ─────────────────────────────────────────────────────────────────
@Composable
fun StatBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription=null, tint=color, modifier=Modifier.size(17.dp))
        Text(text, fontSize=15.sp, fontWeight=FontWeight.W700, color=color)
    }
}

// ── NavItem ───────────────────────────────────────────────────────────────────
@Composable
fun NavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String,
            active: Boolean=false, onClick: (()->Unit)?=null) {
    val color = if (active) C.primary else C.textLight
    Column(horizontalAlignment=Alignment.CenterHorizontally,
           modifier=Modifier.clickable(onClick=onClick?:{}).padding(horizontal=16.dp, vertical=8.dp)) {
        Icon(icon, contentDescription=null, tint=color, modifier=Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize=12.sp, fontWeight=FontWeight.W700, color=color)
    }
}
