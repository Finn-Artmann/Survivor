
import com.soywiz.klock.*
import com.soywiz.korau.sound.*
import com.soywiz.korge.view.*
import com.soywiz.korio.file.std.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.shape.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.GlobalScope
import kotlin.math.*

class Enemy : Container(){

    enum class State{
        IDLE,
        MOVING
    }

    private lateinit var idle: Image
    lateinit var state: State

    var goalPoint: Point? = null
    var moveSpeed = 100.0
    var health = 100.0
    var initialDistToGoal = Point(0.0, 0.0)


    suspend fun loadEnemy(point: Point) {

        position(point)
        scale(.5, .5)
        state = State.IDLE
        idle = Image(
            resourcesVfs["Galactica Ranger/Galactica_Ranger_A.png"].readBitmap(),
            smoothing = false,
            anchorX = .5
        )

        //hitShape { circle { radius = 80.0 } }
        hitShape2d = Shape2d.Circle(100.0 / 2, 100.0 / 2, 100.0)


        addChild(idle)

    }

    fun setGoal(point: Point){
        goalPoint = point
        initialDistToGoal = Point(goalPoint!!.x - x, goalPoint!!.y - y)
        rotation(Angle(atan2(initialDistToGoal.x, -initialDistToGoal.y)))
    }

    fun moveInGoalDirection(dt: TimeSpan){

        if(goalPoint == null) return

        x += initialDistToGoal.normalized.x * moveSpeed * dt.seconds
        y += initialDistToGoal.normalized.y * moveSpeed * dt.seconds
    }

    fun hunt(huntX: Double, huntY: Double, dt: TimeSpan){

        val dist = Point(huntX - x, huntY - y)
        rotation(Angle(atan2(dist.x, -dist.y)))
        x += dist.normalized.x * moveSpeed * dt.seconds
        y += dist.normalized.y * moveSpeed * dt.seconds
    }

    fun despawn(onDespawn: () -> Unit){
        removeChildren()
        removeFromParent()
    }

}
