package com.ryzix.game2048

import android.content.Context
import android.media.MediaPlayer

object Sfx {
    private val players = mutableMapOf<String, MediaPlayer?>()
    var enabled = true

    fun init(ctx: Context) {
        players["click"] = MediaPlayer.create(ctx, R.raw.click)
        players["slide"] = MediaPlayer.create(ctx, R.raw.slide)
        players["gameover"] = MediaPlayer.create(ctx, R.raw.gameover)
        players["win"] = MediaPlayer.create(ctx, R.raw.win)
        players["merge_2"] = MediaPlayer.create(ctx, R.raw.merge_2)
        players["merge_4"] = MediaPlayer.create(ctx, R.raw.merge_4)
        players["merge_8"] = MediaPlayer.create(ctx, R.raw.merge_8)
        players["merge_16"] = MediaPlayer.create(ctx, R.raw.merge_16)
        players["merge_32"] = MediaPlayer.create(ctx, R.raw.merge_32)
        players["merge_64"] = MediaPlayer.create(ctx, R.raw.merge_64)
        players["merge_128"] = MediaPlayer.create(ctx, R.raw.merge_128)
        players["merge_256"] = MediaPlayer.create(ctx, R.raw.merge_256)
        players["merge_512"] = MediaPlayer.create(ctx, R.raw.merge_512)
        players["merge_1024"] = MediaPlayer.create(ctx, R.raw.merge_1024)
        players["merge_2048"] = MediaPlayer.create(ctx, R.raw.merge_2048)
    }

    private fun play(name: String) {
        if (!enabled) return
        try {
            players[name]?.let { it.seekTo(0); it.start() }
        } catch (e: Exception) {}
    }

    fun click() = play("click")
    fun slide() = play("slide")
    fun gameover() = play("gameover")
    fun win() = play("win")
    fun merge_2() = play("merge_2")
    fun merge_4() = play("merge_4")
    fun merge_8() = play("merge_8")
    fun merge_16() = play("merge_16")
    fun merge_32() = play("merge_32")
    fun merge_64() = play("merge_64")
    fun merge_128() = play("merge_128")
    fun merge_256() = play("merge_256")
    fun merge_512() = play("merge_512")
    fun merge_1024() = play("merge_1024")
    fun merge_2048() = play("merge_2048")

    fun slide() = play("slide")
    fun click() = play("click")

    fun merge(value: Int) {
        val pick = listOf(2,4,8,16,32,64,128,256,512,1024,2048).lastOrNull { value >= it } ?: 2
        play("merge_${pick}")
    }

    fun release() { players.values.forEach { it?.release() }; players.clear() }
}
