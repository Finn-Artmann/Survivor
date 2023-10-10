package space_survivor.game_data.views


import com.soywiz.klock.*
import com.soywiz.korge.time.delay
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.file.std.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korma.geom.*
import kotlin.math.*
import com.soywiz.korma.geom.Angle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.soywiz.korge.tween.*
import com.soywiz.korma.interpolation.Easing
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.random.Random

class Enemy() : Container(){

    enum class Type{
        DEFAULT,
        HUNTER
    }

    enum class State{
        ACTIVE,
        SPAWNING,
        DYING
    }

    private lateinit var idle: Image
    lateinit var state: State
    lateinit var type: Type

    var goalPoint: Point? = null
    var moveSpeed = 4.0
    var health = 100.0
    var initialDistToGoal = Point(0.0, 0.0)
    var hitRadius = 40.0
    var hitCircle = circle{ radius = hitRadius; fill = Colors.RED}



    suspend fun loadEnemy(point: Point, enemyType: Type = Type.DEFAULT, speed: Double = 40.0) {

        position(point)
        scale(.5, .5)

        state = State.SPAWNING
        type = enemyType
        moveSpeed = speed

        if(enemyType == Type.DEFAULT){
            idle = Image(
                resourcesVfs["Galactica_Ranger_A.png"].readBitmap(),
                smoothing = false,
                anchorX = .5
            )

        }
        else if(enemyType == Type.HUNTER){
            idle = Image(
                resourcesVfs["Galactica_Ranger_11.png"].readBitmap(),
                smoothing = false,
                anchorX = .5
            )
        }

        hitCircle.x = -hitRadius
        hitCircle.y = -hitRadius
        hitCircle.visible = false

        addChild(hitCircle)
        addChild(idle)

        state = State.ACTIVE

        addUpdater {

            moveInGoalDirection()

        }

    }

    fun setGoal(point: Point){
        goalPoint = point
        initialDistToGoal = Point(goalPoint!!.x - x, goalPoint!!.y - y)
        rotation(Angle.fromRadians(atan2(initialDistToGoal.x, -initialDistToGoal.y)))
    }

    private fun moveInGoalDirection(){

        if(goalPoint == null) return

        x += initialDistToGoal.normalized.x * moveSpeed
        y += initialDistToGoal.normalized.y * moveSpeed
    }

    fun hunt(huntX: Double, huntY: Double){

        val dist = Point(huntX - x, huntY - y)
        rotation(Angle.fromRadians(atan2(dist.x, -dist.y)))
        x += dist.normalized.x * moveSpeed
        y += dist.normalized.y * moveSpeed
    }

    fun die(){
        state = State.DYING
        // Play death animation and execute onDie() when done
        GlobalScope.launch {
            tween(this@Enemy::scale[0.1], time = .5.seconds, easing = Easing.EASE_IN_OUT)
        }.invokeOnCompletion {
            removeFromParent()
        }
    }

    fun despawn(){
        if (state == State.DYING) return
        removeFromParent()
    }
}
