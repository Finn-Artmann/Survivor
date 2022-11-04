package scenes

import com.soywiz.klock.*
import com.soywiz.korau.sound.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tiles.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.util.*
import com.soywiz.korma.geom.*
import views.*
import kotlin.math.*
import kotlin.random.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import models.*

// Acts as controller
class GameScene : Scene() {

    private val enemies = EnemiesMemStore()


    private lateinit var tilemap: TileMap
    private lateinit var player: Player
    private lateinit var joystickText: Text
    private lateinit var backgroundMusic: SoundChannel

    private val cleanupDist = 2000.0


    override suspend fun Container.sceneInit() {

        val tileset = TileSet(bitmap("50+ Repeat Space Backgrounds 200x200 PNG/bg49.png").toBMP32().slice(), 200, 200)
        tilemap = tileMap(Bitmap32(1, 1), repeatX = BaseTileMap.Repeat.REPEAT, repeatY = BaseTileMap.Repeat.REPEAT, tileset = tileset)
        addChild(tilemap)

        player = Player()
        player.loadPlayer(1.0 + views.virtualWidth / 2, 146.0)
        addChild(player)

        joystickText = text("-").position(5, 5).apply { smoothing = false }


        addTouchGamepad(
            views.virtualWidth.toDouble(), views.virtualHeight.toDouble(),
            onStick = { x, y -> player.moveX = x; player.moveY= y }
        )
        addUpdater{ update(it) }
    }

    override suspend fun sceneAfterInit() {
        super.sceneAfterInit()
        backgroundMusic =  resourcesVfs["!SFX + MUSIC!/Audio/Simple Music/DeepSpaceA.mp3"].readMusic().playForever()
        spawnEnemies(6)
    }

    private fun update(dt : TimeSpan){

        playerControl(dt)
        enemiesUpdater(dt)
    }

    private fun enemiesUpdater(dt: TimeSpan){

        enemies.findAll().forEach { e->
            e.view.moveInGoalDirection(dt)

            if(e.view.collidesWith(player, CollisionKind.SHAPE)){
                CoroutineScope(coroutineContext).launchImmediately {
                    player.damageSound.play()
                }
                player.health--
                player.healthBar.setHealth(player.health, player.maxHealth)
            }

            if(abs(e.view.x - player.x) >= cleanupDist || abs(e.view.y - player.y) >= cleanupDist){
                e.view.despawn { enemies.delete(e) }
            }
        }
    }

    private fun playerControl(dt: TimeSpan){

        val fieldMargin = 15
        val backgroundSpeed = 10


        joystickText.setText("Stick: (${player.moveX.toStringDecimal(2)}, ${player.moveY.toStringDecimal(2)})")
        tilemap.x += -player.moveX * backgroundSpeed
        tilemap.y += -player.moveY * backgroundSpeed
        enemies.findAll().forEach { e ->
            e.view.x -= player.moveX * backgroundSpeed
            e.view.y -= player.moveY * backgroundSpeed

            if(e.goalPoint != null){
                e.goalPoint!!.x -= player.moveX * backgroundSpeed
                e.goalPoint!!.y -= player.moveY * backgroundSpeed
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

    private fun randPointLeft(margin: Double) : Point {
        return Point(
            Random.nextDouble(-margin,  -0.0),
            Random.nextDouble(-margin, views.virtualHeight.toDouble() + margin)
        )
    }

    private fun randPointRight(margin: Double) : Point {
        return Point(
            Random.nextDouble(views.virtualWidth.toDouble() , views.virtualWidth.toDouble() + margin),
            Random.nextDouble(-margin, views.virtualHeight.toDouble() + margin)
        )
    }

    private fun randPointTop(margin: Double) : Point {
        return Point(
            Random.nextDouble(-margin,  views.virtualWidth.toDouble() + margin),
            Random.nextDouble(-margin, 0.0)
        )
    }

    private fun randPointBottom(margin: Double) : Point {
        return Point(
            Random.nextDouble(-margin, views.virtualWidth.toDouble() + margin),
            Random.nextDouble(views.virtualHeight.toDouble(), views.virtualHeight.toDouble() + margin)
        )
    }


    private fun generateEnemyPoints(): Pair<Point, Point> {

        lateinit var spawnPoint: Point
        lateinit var movePoint: Point
        val margin = 40.0


        when (Random.nextInt(0, 3)) {

            // Right
            0 -> {
                spawnPoint = randPointRight(margin)
                movePoint = randPointLeft(margin)
            }

            // Bottom
            1 -> {
                spawnPoint = randPointBottom(margin)
                movePoint = randPointTop(margin)
            }

            // Left
            2 -> {
                spawnPoint = randPointLeft(margin)
                movePoint = randPointRight(margin)
            }

            // Top
            3 -> {
                spawnPoint = randPointTop(margin)
                movePoint = randPointBottom(margin)
            }
        }


        return Pair(spawnPoint, movePoint)
    }

    private fun spawnEnemies(count: Int){

        for(i in 0 until count){
            var newEnemyId = enemies.create(EnemyModel())

            CoroutineScope(coroutineContext).launch{
                val points = generateEnemyPoints()
                enemies.findOne(newEnemyId)?.view?.loadEnemy(points.first)
                enemies.findOne(newEnemyId)?.view?.setGoal(points.second)
            }
            sceneView.addChild(enemies[i])
        }
    }

}
suspend fun bitmap(path: String) = resourcesVfs[path].readBitmap()
