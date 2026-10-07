package com.ryzix.game2048

import kotlin.random.Random

enum class Dir { UP, DOWN, LEFT, RIGHT }

data class TileData(
    val id: Int, var x: Int, var y: Int, var value: Int,
    var isNew: Boolean = true, var isMerged: Boolean = false,
    var toDelete: Boolean = false, var isMerging: Boolean = false,
    var nextValue: Int = 0, var mergedThisTurn: Boolean = false,
)

data class MoveResult(val moved: Boolean, val scoreGained: Int, val maxMergedVal: Int)

class Game2048(val gridSize: Int = 4) {
    private val rng = Random.Default
    private var idCounter = 1
    var score = 0; private set
    var bestScore = 0; private set
    var board: Array<IntArray> = Array(gridSize) { IntArray(gridSize) }; private set
    var tiles: MutableList<TileData> = mutableListOf(); private set
    private data class Snapshot(val board: Array<IntArray>, val tiles: List<TileData>, val score: Int)
    private val history = ArrayDeque<Snapshot>()
    val canUndo get() = history.isNotEmpty()
    init { reset() }

    fun reset() {
        score = 0; idCounter = 1; tiles.clear(); history.clear()
        board = Array(gridSize) { IntArray(gridSize) }
        spawnTile(); spawnTile()
    }

    private fun spawnTile() {
        val empty = (0 until gridSize).flatMap { r -> (0 until gridSize).filter { c -> board[r][c] == 0 }.map { c -> r to c } }
        if (empty.isEmpty()) return
        val (r, c) = empty[rng.nextInt(empty.size)]
        val v = if (rng.nextDouble() < 0.9) 2 else 4
        board[r][c] = v; tiles.add(TileData(id = idCounter++, x = c, y = r, value = v))
    }

    private fun snapshot() {
        history.addLast(Snapshot(board.map { it.copyOf() }.toTypedArray(), tiles.map { it.copy() }, score))
        if (history.size > 5) history.removeFirst()
    }

    fun undo(): Boolean {
        if (history.isEmpty()) return false
        val s = history.removeLast(); board = s.board; tiles = s.tiles.toMutableList(); score = s.score; return true
    }

    fun move(dir: Dir): MoveResult {
        snapshot()
        val vx = when(dir) { Dir.LEFT -> -1; Dir.RIGHT -> 1; else -> 0 }
        val vy = when(dir) { Dir.UP -> -1; Dir.DOWN -> 1; else -> 0 }
        val xs = (0 until gridSize).let { if (vx == 1) it.reversed() else it.toList() }
        val ys = (0 until gridSize).let { if (vy == 1) it.reversed() else it.toList() }
        var moved = false; var gained = 0; var maxMerged = 0
        for (t in tiles) t.mergedThisTurn = false
        for (r in ys) for (c in xs) {
            if (board[r][c] == 0) continue
            val tile = tiles.firstOrNull { it.x == c && it.y == r && !it.toDelete } ?: continue
            var nx = c; var ny = r
            while (true) {
                val tx = nx + vx; val ty = ny + vy
                if (tx !in 0 until gridSize || ty !in 0 until gridSize) break
                if (board[ty][tx] == 0) { nx = tx; ny = ty }
                else if (board[ty][tx] == tile.value) {
                    val other = tiles.firstOrNull { it.x == tx && it.y == ty && !it.toDelete && !it.mergedThisTurn }
                    if (other != null) {
                        nx = tx; ny = ty; board[r][c] = 0; board[ny][nx] = tile.value * 2
                        tile.x = nx; tile.y = ny; tile.nextValue = tile.value * 2
                        tile.isMerging = true; tile.mergedThisTurn = true
                        other.toDelete = true; gained += tile.value * 2
                        if (tile.value * 2 > maxMerged) maxMerged = tile.value * 2; moved = true
                    }; break
                } else break
            }
            if ((nx != c || ny != r) && !tile.isMerging) { board[r][c] = 0; board[ny][nx] = tile.value; tile.x = nx; tile.y = ny; moved = true }
        }
        if (moved) { score += gained; if (score > bestScore) bestScore = score } else history.removeLast()
        return MoveResult(moved, gained, maxMerged)
    }

    fun settle() {
        tiles.removeAll { it.toDelete }
        for (t in tiles) if (t.isMerging) { t.value = t.nextValue; t.isMerged = true; t.isMerging = false }
        spawnTile()
    }

    val isGameOver: Boolean get() {
        for (r in 0 until gridSize) for (c in 0 until gridSize) {
            if (board[r][c] == 0) return false
            if (c < gridSize-1 && board[r][c] == board[r][c+1]) return false
            if (r < gridSize-1 && board[r][c] == board[r+1][c]) return false
        }; return true
    }
    val hasWon: Boolean get() = tiles.any { it.value >= 2048 }
}
