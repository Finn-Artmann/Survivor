package com.example.space_survivor

import android.util.Log.i
import com.soywiz.kds.IntArray2
import com.soywiz.kds.intMapOf
import com.soywiz.klock.*
import com.soywiz.korau.sound.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korge.view.camera.cameraContainer
import com.soywiz.korim.format.*
import com.soywiz.korma.geom.*
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korge.view.tiles.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.format.ImageDecodingProps.Companion.DEFAULT_PREMULT

import com.soywiz.korinject.AsyncInjector
import kotlin.reflect.KClass
import com.soywiz.korio.util.*
import kotlin.math.*
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.shape.ops.collidesWith


suspend fun bitmap(path: String) = resourcesVfs[path].readBitmap()

class GameScene : Scene() {

    private lateinit var tilemap: TileMap
    private lateinit var player: Player
    private lateinit var joystick_text: TextOld
    private lateinit var timer_text: Text
    private lateinit var backgroundMusic: SoundChannel
    private val enemies: MutableList<Enemy> = mutableListOf()
    private val cleanupDist = 1000.0
    private val waveGen = WaveGenerator(this, enemies)
    private var timer = 0.minutes
    var backgroundSpeed = 10.0


    override suspend fun Container.sceneInit() {

        var tileset = TileSet(
            intMapOf(
                0 to TileSetTileInfo(0, bitmap("bg49.png").slice()),
                1 to TileSetTileInfo(1, bitmap("bg56.png").slice()),
            )
        )

        tilemap = tileMap(
            IntArray2(1, 1, intArrayOf(0)),
            repeatX = BaseTileMap.Repeat.REPEAT,
            repeatY = BaseTileMap.Repeat.REPEAT,
            tileset = tileset

        )

        player = Player()
        player.loadPlayer(
            1.0 + views.virtualWidth / 2,
            146.0, views.virtualWidth, views.virtualHeight
        )
        addChild(player)


        joystick_text = textOld("-").position(5, 5).apply { filtering = false }
        timer_text = text("$timer").position((views.virtualWidth / 2 ), 5).centerXOnStage().apply {textSize = 50.0 }


        addTouchGamepad(
            views.virtualWidth.toDouble(), views.virtualHeight.toDouble(),
            onStick = { x, y -> player.moveX = x; player.moveY= y }
        )
        addUpdater{ update(it) }
    }

    override suspend fun sceneAfterInit() {
        super.sceneAfterInit()
        backgroundMusic =  resourcesVfs["DeepSpaceA.mp3"].readMusic().playForever()

    }

    private fun update(dt : TimeSpan){

        playerControl(dt)
        waveGen.checkNextWave(dt)
        joystick_text.setText("Wave: ${waveGen.waveNumber} Enemies: ${waveGen.enemiesPerWave}")

        // Update timer
        timer += dt
        timer_text.setText("${ISO8601.TIME_LOCAL_COMPLETE.format(timer)}")
        timer_text.centerXOnStage()
    }


    private fun playerControl(dt: TimeSpan){


        //joystick_text.setText("Stick: (${player.moveX.toStringDecimal(2)}, ${player.moveY.toStringDecimal(2)})")


        backgroundSpeed = if(player.colliding) 0.1 else 10.0 // Slow down background / player when colliding

        launchImmediately(views.coroutineContext) {

            tilemap.x += -player.moveX * backgroundSpeed
            tilemap.y += -player.moveY * backgroundSpeed
            enemies.forEach { enemy ->

                // despawn enemy if out of view
                if (enemy.x < -200 || enemy.x > 900 || enemy.y < -200 || enemy.y > 1540){
                    enemy.despawn{ enemies.remove(enemy) }
                }

                // Hunter type enemies will chase the player
                // They still move in a random direction but will be attracted to the player
                if(enemy.type == Enemy.Type.HUNTER){
                    enemy.hunt(player.x, player.y, dt)
                }

                enemy.x -= player.moveX * backgroundSpeed
                enemy.y -= player.moveY * backgroundSpeed

                if(enemy.goalPoint != null){
                    enemy.goalPoint!!.x -= player.moveX * backgroundSpeed
                    enemy.goalPoint!!.y -= player.moveY * backgroundSpeed

                }

            }
        }

    }
}

