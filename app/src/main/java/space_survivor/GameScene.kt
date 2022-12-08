package com.example.space_survivor

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.soywiz.kds.IntArray2
import com.soywiz.kds.intMapOf
import com.soywiz.klock.*
import com.soywiz.korau.sound.*
import com.soywiz.korev.TouchEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.TouchComponent
import com.soywiz.korge.input.gamepad
import com.soywiz.korge.input.mouse
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korge.view.tiles.*
import com.soywiz.korge.view.tween.hide
import com.soywiz.korge.view.tween.show
import com.soywiz.korim.bitmap.*

import com.soywiz.korio.async.*
import space_survivor.GameOverOverlay
import space_survivor.main.MainApp
import space_survivor.models.ScoreModel


suspend fun bitmap(path: String) = resourcesVfs[path].readBitmap()

class GameScene(var app: MainApp) : Scene() {

    private lateinit var tilemap: TileMap
    private lateinit var player: Player
    private lateinit var infoText: Text
    private lateinit var timerText: Text
    private lateinit var backgroundMusic: SoundChannel
    private lateinit var gameOverOverlay: GameOverOverlay
    private lateinit var score : ScoreModel


    private val enemies: MutableList<Enemy> = mutableListOf()
    private val cleanupDist = 1000.0
    private val waveGen = WaveGenerator(this, enemies)
    private var timer = 0.minutes
    var backgroundSpeed = 10.0
    var gameOver = false



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

        player = Player().apply { scale = 1.5 }
        player.loadPlayer(
            1.0 + views.virtualWidth / 2,
            146.0, views.virtualWidth, views.virtualHeight
        )
        addChild(player)


        infoText = text("-").position(25, 50).apply { smoothing = false; textSize = 20.0 }.apply { scale = 2.0 }
        timerText = text("$timer").position((views.virtualWidth / 2 ), 50).centerXOnStage().apply {textSize = 50.0; scale = 1.5}
        //Set timer text to always be in foreground (in front of enemies) with KorGE




        gameOverOverlay = GameOverOverlay(sceneContainer) {
            launchImmediately { sceneContainer.changeTo<GameScene>() }
        }
        addChild(gameOverOverlay)

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

        if(gameOver) return

        // Check if player is dead
        if (player.state == Player.State.DEAD){

            gameOver = true
            backgroundMusic.stop()

            // Check if player is logged in and if so, save score
            if (app.account != null) {
                score = ScoreModel("0", app.account?.displayName.toString(), timer.millisecondsLong)
                app.scores.create(score)
            }

            launchImmediately { gameOverOverlay.load() }
        }


        playerMovementUpdate(dt)
        waveGen.checkNextWave(dt)
        infoText.setText("\tWave:\t${waveGen.waveNumber}\n\tEnemies:\t${waveGen.enemiesPerWave}")

        // Update timer
        timer += dt
        timerText.setText("${ISO8601.TIME_LOCAL_COMPLETE.format(timer)}")
        timerText.centerXOnStage()

    }


    private fun playerMovementUpdate(dt: TimeSpan){

        //joystick_text.setText("Stick: (${player.moveX.toStringDecimal(2)}, ${player.moveY.toStringDecimal(2)})")

        backgroundSpeed = if(player.colliding) 0.1 else 10.0 // Slow down background / player when colliding

        launchImmediately(views.coroutineContext) {

            tilemap.x += -player.moveX * backgroundSpeed
            tilemap.y += -player.moveY * backgroundSpeed
            enemies.forEach { enemy ->

                // despawn enemy if out of view
                if (enemy.x < -200 || enemy.x > views.virtualWidth+200 || enemy.y < -200 || enemy.y > views.virtualHeight+200){
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

