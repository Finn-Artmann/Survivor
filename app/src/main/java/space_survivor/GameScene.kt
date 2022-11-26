package com.example.space_survivor

import com.soywiz.klock.*
import com.soywiz.korau.sound.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.format.*
import com.soywiz.korma.geom.*
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korge.view.tiles.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korinject.AsyncInjector
import kotlin.reflect.KClass
import com.soywiz.korge.Korge
import com.soywiz.korio.util.*
import kotlin.math.*
import com.soywiz.korio.async.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random

//suspend fun main() = Korge(Korge.Config(module = ConfigModule, virtualSize = SizeInt(800, 1440)))

object ConfigModule : Module(){

    override val windowSize: SizeInt
        get() = SizeInt(800, 1440)


    override val mainScene: KClass<out Scene> = GameScene::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { GameScene() }
    }
}

suspend fun bitmap(path: String) = resourcesVfs[path].readBitmap()

class GameScene : Scene() {

    private lateinit var tilemap: TileMap
    private lateinit var player: Player
    private lateinit var joystick_text: TextOld
    private lateinit var timer_text: Text
    private lateinit var backgroundMusic: SoundChannel
    private val enemies: MutableList<Enemy> = mutableListOf()
    private val cleanupDist = 2000.0
    private val waveGen = WaveGenerator(this, enemies)
    private var timer = 0.minutes



    override suspend fun Container.sceneInit() {

        val tileset = TileSet(bitmap("bg49.png").toBMP32().slice(), 200, 200)
        tilemap = tileMap(Bitmap32(1, 1), repeatX = BaseTileMap.Repeat.REPEAT, repeatY = BaseTileMap.Repeat.REPEAT, tileset = tileset)
        addChild(tilemap)

        player = Player()
        player.loadPlayer(1.0 + views.virtualWidth / 2, 146.0)
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
        waveGen.spawnEnemies(2)
    }

    private fun update(dt : TimeSpan){

        playerControl(dt)
        enemiesUpdater(dt)
        waveGen.checkNextWave(dt)

        // Update timer
        timer += dt
        timer_text.setText("${ISO8601.TIME_LOCAL_COMPLETE.format(timer)}")
        timer_text.centerXOnStage()
    }

    private fun enemiesUpdater(dt: TimeSpan){

        enemies.forEach { enemy ->
            enemy.moveInGoalDirection(dt)

            if(abs(enemy.x - player.x) >= cleanupDist || abs(enemy.y - player.y) >= cleanupDist){
                enemy.despawn {
                    enemies.remove(enemy)
                }
            }
        }
    }

    private fun playerControl(dt: TimeSpan){

        val fieldMargin = 15
        val backgroundSpeed = 10


        joystick_text.setText("Stick: (${player.moveX.toStringDecimal(2)}, ${player.moveY.toStringDecimal(2)})")
        tilemap.x += -player.moveX * backgroundSpeed
        tilemap.y += -player.moveY * backgroundSpeed
        enemies.forEach { enemy ->
            enemy.x -= player.moveX * backgroundSpeed
            enemy.y -= player.moveY * backgroundSpeed

            if(enemy.goalPoint != null){
                enemy.goalPoint!!.x -= player.moveX * backgroundSpeed
                enemy.goalPoint!!.y -= player.moveY * backgroundSpeed
            }

        }

        player.rotation(Angle(atan2(player.moveX, -player.moveY)))

        if(player.moveX <= 0.0 && player.x > fieldMargin ){

            player.x += player.moveX * player.moveSpeed * dt.seconds
        }

        if(player.moveX >= 0.0 && player.x < views.virtualWidth - fieldMargin ){

            player.x += player.moveX * player.moveSpeed * dt.seconds
        }

        if(player.moveY <= 0.0 && player.y > fieldMargin){

            player.y += player.moveY * player.moveSpeed * dt.seconds
        }

        if(player.moveY >= 0.0 && player.y < views.virtualHeight - fieldMargin){

            player.y += player.moveY * player.moveSpeed * dt.seconds
        }

    }





}

