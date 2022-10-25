import com.soywiz.klock.*
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
    var playerMoveX = 0.0
    var playerMoveY = 0.0
	override suspend fun Container.sceneInit() {


        val tileset = TileSet(bitmap("50+ Repeat Space Backgrounds 200x200 PNG/bg49.png").toBMP32().slice(), 200, 200)
        tilemap = tileMap(Bitmap32(1, 1), repeatX = BaseTileMap.Repeat.REPEAT, repeatY = BaseTileMap.Repeat.REPEAT, tileset = tileset)

        player = Player()
        player.loadPlayer(1.0 + views.virtualWidth / 2, 146.0)
        addChild(player)

        joystick_text = textOld("-").position(5, 5).apply { filtering = false }


        addTouchGamepad(
            views.virtualWidth.toDouble(), views.virtualHeight.toDouble(),
            onStick = { x, y -> playerMoveX = x; playerMoveY= y }
        )
        addUpdater{ update(it) }

    }

    private fun update(dt : TimeSpan){

        playerControl(dt)
    }

    private fun playerControl(dt: TimeSpan){

        val fieldMargin = 15
        val backgroundSpeed = 10

        joystick_text.setText("Stick: (${playerMoveX.toStringDecimal(2)}, ${playerMoveY.toStringDecimal(2)})")
        tilemap.x += -playerMoveX * backgroundSpeed
        tilemap.y += -playerMoveY * backgroundSpeed

        player.rotation(Angle(atan2(playerMoveX, -playerMoveY)))

        if(playerMoveX <= 0.0 && player.x > fieldMargin ){

            player.x += playerMoveX * player.moveSpeed * dt.seconds
        }

        if(playerMoveX >= 0.0 && player.x < views.virtualWidth - fieldMargin ){

            player.x += playerMoveX * player.moveSpeed * dt.seconds
        }

        if(playerMoveY <= 0.0 && player.y > fieldMargin){

            player.y += playerMoveY * player.moveSpeed * dt.seconds
        }

        if(playerMoveY >= 0.0 && player.y < views.virtualHeight - fieldMargin){

            player.y += playerMoveY * player.moveSpeed * dt.seconds
        }


        /*
        if (views.input.keys[Key.LEFT]) {
            tilemap.x += backgroundSpeed
            if (player.x > fieldMargin) {
                player.x -= player.moveSpeed * dt.seconds

            }

        }

        if (views.input.keys[Key.RIGHT]) {
            tilemap.x -= backgroundSpeed
            if (player.x < views.virtualWidth - fieldMargin) {
                player.x += player.moveSpeed * dt.seconds

            }

        }

        if (views.input.keys[Key.UP]) {
            tilemap.y += backgroundSpeed
            if (player.y > fieldMargin) player.y -= player.moveSpeed * dt.seconds


        }

        if (views.input.keys[Key.DOWN]) {
            tilemap.y -= backgroundSpeed
            if (player.y < views.virtualHeight - fieldMargin) player.y += player.moveSpeed * dt.seconds


        }
        */
    }

}

