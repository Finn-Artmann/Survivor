package views
import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.shape.*
import models.*
import kotlin.math.*

class EnemyView(private val enemy: EnemyModel) : Container(){

    private lateinit var enemyImage: Image
    suspend fun loadEnemy(point: Point) {

        position(point)
        scale(.5, .5)
        enemyImage = Image(
            enemy.image.readBitmap(),
            smoothing = false,
            anchorX = .5
        )

        //hitShape { circle { radius = 80.0 } }
        hitShape2d = Shape2d.Circle(100.0 / 2, 100.0 / 2, 100.0)


        addChild(enemyImage)

    }

    fun setGoal(point: Point){
        enemy.goalPoint = point
        enemy.initialDistToGoal = Point(enemy.goalPoint!!.x - x, enemy.goalPoint!!.y - y)
        rotation(Angle(atan2(enemy.initialDistToGoal.x, -enemy.initialDistToGoal.y)))
    }

    fun moveInGoalDirection(dt: TimeSpan){

        if(enemy.goalPoint == null) return

        x += enemy.initialDistToGoal.normalized.x * enemy.moveSpeed * dt.seconds
        y += enemy.initialDistToGoal.normalized.y * enemy.moveSpeed * dt.seconds
    }

    fun hunt(huntX: Double, huntY: Double, dt: TimeSpan){

        val dist = Point(huntX - x, huntY - y)
        rotation(Angle(atan2(dist.x, -dist.y)))
        x += dist.normalized.x * enemy.moveSpeed * dt.seconds
        y += dist.normalized.y * enemy.moveSpeed * dt.seconds
    }

    fun despawn(onDespawn: () -> Unit){
        removeChildren()
        removeFromParent()
    }


}
