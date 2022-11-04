import com.soywiz.korge.scene.*
import com.soywiz.korma.geom.*
import com.soywiz.korinject.AsyncInjector
import kotlin.reflect.KClass
import com.soywiz.korge.Korge
import scenes.*

suspend fun main() = Korge(Korge.Config(module = ConfigModule, virtualSize = SizeInt(800, 1440)))

object ConfigModule : Module(){

    override val windowSize: SizeInt
        get() = SizeInt(800, 1440)


    override val mainScene: KClass<out Scene> = GameScene::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { GameScene() }
    }
}





