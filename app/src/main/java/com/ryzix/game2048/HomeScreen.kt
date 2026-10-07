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
fun HomeScreen() {
    val navController = LocalNavController.current
    val context = androidx.compose.ui.platform.LocalContext.current
    var score by remember { mutableIntStateOf(0) }
    var best by remember { mutableIntStateOf(0) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBadge(icon = Icons.Rounded.LocalFireDepartment, text = "streak", color = Color(0xFFFF9600))
                StatBadge(icon = Icons.Rounded.EmojiEvents, text = "best", color = Color(0xFFFFC800))
                StatBadge(icon = Icons.Rounded.Diamond, text = "gems", color = Color(0xFF1CB0F6))
                BtnGray(icon = Icons.Rounded.Fullscreen, onClick = { android.widget.Toast.makeText(context, "Fullscreen", android.widget.Toast.LENGTH_SHORT).show() })
            }
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                    Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFFF2541)).padding(20.dp).clip(RoundedCornerShape(20.dp)), horizontalArrangement = Arrangement.spacedBy(0.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start) {
                            Text("CHALLENGE MAP", fontSize = 12.sp, fontWeight = FontWeight.W700, color = Color(0xCCFFFFFF))
                            Text("Select Level Grid", fontSize = 18.sp, fontWeight = FontWeight.W700, color = Color.White, modifier = Modifier)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Icon(Icons.Rounded.SportsEsports, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    LevelCard(icon = Icons.Rounded.GridView, iconBg = Color(0xFFFF2541), iconShadow = Color(0xFFD8132F), title = "Level 1: Classic", subtitle = "Standard 4×4 Board", onClick = { navController.navigate("Game?grid=4") })
                    Spacer(modifier = Modifier.height(20.dp))
                    LevelCard(icon = Icons.Rounded.OpenInFull, iconBg = Color(0xFF1CB0F6), iconShadow = Color(0xFF1899D6), title = "Level 2: Master Grid", subtitle = "Expanded 5×5 Matrix", onClick = { navController.navigate("Game?grid=5") })
                    Spacer(modifier = Modifier.height(20.dp))
                    LevelCard(icon = Icons.Rounded.Bolt, iconBg = Color(0xFFFFC800), iconShadow = Color(0xFFE5B200), title = "Level 3: Blitz Speed", subtitle = "Compact 3×3 Extreme", onClick = { navController.navigate("Game?grid=3") })
                }
            }
            Row(modifier = Modifier.fillMaxWidth().background(C.Color.White).padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
                NavItem(icon = Icons.Rounded.SportsEsports, label = "Play", active = true)
                NavItem(icon = Icons.Rounded.Star, label = "Pro", active = false)
                NavItem(icon = Icons.Rounded.Settings, label = "Settings", active = false)
            }
        }
        }
    }
}
