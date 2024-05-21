package space_survivor.game_data.scenes

import com.soywiz.kds.IntArray2
import com.soywiz.kds.intMapOf
import com.soywiz.klock.*
import com.soywiz.korau.sound.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korge.view.tiles.*
import com.soywiz.korim.bitmap.*

import com.soywiz.korio.async.*
import space_survivor.game_data.*
import space_survivor.game_data.util.GameState

import space_survivor.game_data.util.WaveGenerator
import space_survivor.game_data.views.Bullet
import space_survivor.game_data.views.Enemy
import space_survivor.game_data.views.GameOverOverlay
import space_survivor.game_data.views.Player
import space_survivor.main.MainApp
import space_survivor.models.ScoreModel


suspend fun bitmap(path: String) = resourcesVfs[path].readBitmap()

class GameScene(var app: MainApp) : Scene() {

    private lateinit var tilemap: TileMap
    private lateinit var player: Player
    private lateinit var infoText: Text
    private lateinit var timerText: Text
    lateinit var backgroundMusic: SoundChannel
    private lateinit var gameOverOverlay: GameOverOverlay
    private lateinit var score: ScoreModel

    private var enemies: MutableList<Enemy> = mutableListOf()
    private var waveGen = WaveGenerator(this, enemies)
    private var timer = 0.minutes
    var backgroundSpeed = 10.0
    var gameOver = false


    override suspend fun Container.sceneInit() {

        app.resetGame = false
        if (app.gameState != null) {
            loadGameState(app.gameState!!)
        } else {

            player = Player(sceneView).apply { scale = 1.5 }
            player.loadPlayer(
                1.0 + views.virtualWidth / 2,
                146.0, views.virtualWidth, views.virtualHeight
            )
        }

        val tileset = TileSet(
            intMapOf(
                0 to TileSetTileInfo(0, bitmap("bg1.png").slice())
            )
        )

        tilemap = tileMap(
            IntArray2(1, 1, intArrayOf(0)),
            repeatX = BaseTileMap.Repeat.REPEAT,
            repeatY = BaseTileMap.Repeat.REPEAT,
            tileset = tileset

        )


        addChild(player)
        addChild(player.bullet)


        infoText = text("-").position(25, 50).apply { smoothing = false; textSize = 20.0 }
            .apply { scale = 2.0 }
        timerText = text("$timer").position((views.virtualWidth / 2), 50).centerXOnStage()
            .apply { textSize = 50.0; scale = 1.5 }
        //Set timer text to always be in foreground (in front of enemies) with KorGE


        gameOverOverlay = GameOverOverlay(sceneContainer) {
            launchImmediately { sceneContainer.changeTo<GameScene>() }
        }
        addChild(gameOverOverlay)


        addTouchGamepad(
            views.virtualWidth.toDouble(), views.virtualHeight.toDouble(),
            onStick = { x, y -> player.moveX = x; player.moveY = y },
            app = app
        )

        addUpdater { update(it) }

    }

    override suspend fun sceneAfterInit() {
        super.sceneAfterInit()
        backgroundMusic = resourcesVfs["ruskerdax_-_savage_ambush.mp3"].readMusic().playForever()

    }

    private fun update(dt: TimeSpan) {

        if (gameOver) return

        // Check if player is dead
        if (player.state == Player.State.DEAD) {

            gameOver = true
            backgroundMusic.stop()

            // Check if player is logged in and if so, save score
            if (app.account != null) {


                // Get current date and time as string
                val date = DateTime.nowLocal().toString("yyyy-MM-dd HH:mm:ss")
                score = ScoreModel(
                    "0",
                    app.account?.displayName.toString(),
                    timer.millisecondsLong,
                    date
                )
                app.scores.create(score)
            }

            launchImmediately { gameOverOverlay.load() }
        }

        playerMovementUpdate()


        // Change background music when waveGen reached specific waves to indicate difficulty increase
        if (waveGen.checkNextWave(dt)) {

            when (waveGen.waveNumber) {
                15 -> {
                    backgroundMusic.stop()
                    launch {
                        backgroundMusic =
                            resourcesVfs["ruskerdax_-_open_warfare.mp3"].readMusic().playForever()
                    }
                }

                30 -> {
                    backgroundMusic.stop()
                    launch {
                        backgroundMusic =
                            resourcesVfs["Caves.ogg"].readMusic().playForever()
                    }
                }
            }

        }

        infoText.setText("\tWave:\t${waveGen.waveNumber}\n\tEnemies:\t${waveGen.enemiesPerWave}")

        // Update timer
        timer += dt
        timerText.setText(ISO8601.TIME_LOCAL_COMPLETE.format(timer))
        timerText.centerXOnStage()

    }


    private fun playerMovementUpdate() {
        //joystick_text.setText("Stick: (${player.moveX.toStringDecimal(2)}, ${player.moveY.toStringDecimal(2)})")

        backgroundSpeed =
            if (player.colliding) 0.1 else 10.0 // Slow down background / player when colliding

        launchImmediately(views.coroutineContext) {

            tilemap.x += -player.moveX * backgroundSpeed
            tilemap.y += -player.moveY * backgroundSpeed

            val numBullets = sceneView.numChildren
            val bullets = ArrayList<Bullet>()
            for (i in 0 until numBullets) {
                val bullet = sceneView.getChildAt(i) as? Bullet
                if (bullet != null) {
                    bullets.add(bullet)
                }
            }

            bullets.forEach() { bullet ->
                bullet.x -= player.moveX * backgroundSpeed
                bullet.y -= player.moveY * backgroundSpeed

                if (bullet.x < -200 || bullet.x > views.virtualWidth + 200 || bullet.y < -200 || bullet.y > views.virtualHeight + 200) {

                    sceneView.removeChild(bullet)
                }
            }

            enemies.forEach { enemy ->

                // kill enemy if health is 0
                if (enemy.health <= 0) {
                   launch { enemy.die() }
                }

                // despawn enemy if out of view
                if (enemy.x < -200 || enemy.x > views.virtualWidth + 200 || enemy.y < -200 || enemy.y > views.virtualHeight + 200) {
                    sceneView.removeChild(enemy)
                }

                // Hunter type enemies will chase the player
                // They still move in a random direction but will be attracted to the player
                if (enemy.type == Enemy.Type.HUNTER) {
                    enemy.hunt(player.x, player.y)
                }

                enemy.x -= player.moveX * backgroundSpeed
                enemy.y -= player.moveY * backgroundSpeed

                if (enemy.goalPoint != null) {
                    enemy.goalPoint!!.x -= player.moveX * backgroundSpeed
                    enemy.goalPoint!!.y -= player.moveY * backgroundSpeed

                }

            }
        }

    }


    override suspend fun sceneDestroy() {
        super.sceneDestroy()

        if(app.resetGame) {
            app.gameState = null
            return
        }

        // Save game state to object
        val gameState = GameState(
            player,
            waveGen,
            timer,
            gameOver
        )

        app.gameState = gameState

    }

    private suspend fun loadGameState(gameState: GameState) {
        player = Player(sceneView).apply { scale = 1.5 }
        waveGen.waveNumber = gameState.waveGen.waveNumber
        waveGen.enemiesPerWave = gameState.waveGen.enemiesPerWave

        timer = gameState.timer
        gameOver = gameState.gameOver

        player.loadPlayer(
            1.0 + views.virtualWidth / 2,
            146.0, views.virtualWidth, views.virtualHeight
        )
        player.moveSpeed = gameState.player.moveSpeed
        player.state = gameState.player.state
        player.health = gameState.player.health
        player.maxHealth = gameState.player.maxHealth

        /* All enemies have been destroyed,
            so we need to spawn the current wave multiple times to balance the game.

           While it is possible to save the enemies and their state, spawning them correctly
           is difficult since we have to take the original orientation of the device and
           the resulting virtual width and height into account.
       */
        val enemiesPerWave = waveGen.enemiesPerWave
        for (i in 0 until 3) {
            waveGen.spawnEnemies(enemiesPerWave, Enemy.Type.DEFAULT, 5.0)
        }

    }

}







