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

suspend fun main() = Korge(Korge.Config(module = ConfigModule, virtualSize = SizeInt(800, 1440)))

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
    private lateinit var backgroundMusic: SoundChannel
    private val enemies: MutableList<Enemy> = mutableListOf()
    private val cleanupDist = 2000.0


	override suspend fun Container.sceneInit() {

        val tileset = TileSet(bitmap("50+ Repeat Space Backgrounds 200x200 PNG/bg49.png").toBMP32().slice(), 200, 200)
        tilemap = tileMap(Bitmap32(1, 1), repeatX = BaseTileMap.Repeat.REPEAT, repeatY = BaseTileMap.Repeat.REPEAT, tileset = tileset)
        addChild(tilemap)

        player = Player()
        player.loadPlayer(1.0 + views.virtualWidth / 2, 146.0)
        addChild(player)

        joystick_text = textOld("-").position(5, 5).apply { filtering = false }


        addTouchGamepad(
            views.virtualWidth.toDouble(), views.virtualHeight.toDouble(),
            onStick = { x, y -> player.moveX = x; player.moveY= y }
        )
        addUpdater{ update(it) }
    }

    override suspend fun sceneAfterInit() {
        super.sceneAfterInit()
        //backgroundMusic =  resourcesVfs["!SFX + MUSIC!/Audio/Simple Music/DeepSpaceA.mp3"].readMusic().playForever()
        spawnEnemies(6)
    }

    private fun update(dt : TimeSpan){

        playerControl(dt)
        enemiesUpdater(dt)
    }

    private fun enemiesUpdater(dt: TimeSpan){

        enemies.forEach { enemy ->
            enemy.moveInGoalDirection(dt)

            if(enemy.collidesWith(player, CollisionKind.SHAPE)){
                CoroutineScope(coroutineContext).launchImmediately {
                    player.damageSound.play()
                }
                player.health--;
                player.healthBar.setHealth(player.health, player.maxHealth)
            }

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

    private fun randPointLeft(margin: Double) :Point{
        return Point(
            Random.nextDouble(-margin,  -0.0),
            Random.nextDouble(-margin, views.virtualHeight.toDouble() + margin)
        )
    }

    private fun randPointRight(margin: Double) :Point{
        return Point(
            Random.nextDouble(views.virtualWidth.toDouble() , views.virtualWidth.toDouble() + margin),
            Random.nextDouble(-margin, views.virtualHeight.toDouble() + margin)
        )
    }

    private fun randPointTop(margin: Double) :Point{
        return Point(
            Random.nextDouble(-margin,  views.virtualWidth.toDouble() + margin),
            Random.nextDouble(-margin, 0.0)
        )
    }

    private fun randPointBottom(margin: Double) :Point{
        return Point(
            Random.nextDouble(-margin, views.virtualWidth.toDouble() + margin),
            Random.nextDouble(views.virtualHeight.toDouble(), views.virtualHeight.toDouble() + margin)
        )
    }


    private fun generateEnemyPoints() : Pair<Point, Point> {

        lateinit var spawnPoint: Point
        lateinit var movePoint: Point
        val margin = 40.0
        val randSide = Random.nextInt(0, 3)


        when(randSide){

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

        for(i in 0..count-1){
            enemies.add(i ,Enemy())

            CoroutineScope(coroutineContext).launch{
                val points = generateEnemyPoints()
                enemies[i].loadEnemy(points.first)
                enemies[i].setGoal(points.second)
            }
            sceneView.addChild(enemies[i])
        }
    }

}

