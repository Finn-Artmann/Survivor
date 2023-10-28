package space_survivor.game_data.views


import com.soywiz.klock.*
import com.soywiz.korge.animate.animate
import com.soywiz.korge.time.delay
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.file.std.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korma.geom.*
import kotlin.math.*
import com.soywiz.korma.geom.Angle
import com.soywiz.korge.tween.*
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.interpolation.Easing
import kotlinx.coroutines.*
import timber.log.Timber
import timber.log.Timber.i
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


    lateinit var piece1: Image
    lateinit var piece2: Image
    lateinit var piece3: Image


    suspend fun loadEnemy(point: Point, enemyType: Type = Type.DEFAULT, speed: Double = 40.0) {

        position(point)
        scale(.5, .5)

        state = State.SPAWNING
        type = enemyType
        moveSpeed = speed

        piece1 = Image(resourcesVfs["piece1.png"].readBitmap(), smoothing = false, anchorX = .5, anchorY = .5)
        piece2 = Image(resourcesVfs["piece2.png"].readBitmap(), smoothing = false, anchorX = .5, anchorY = .5)
        piece3 = Image(resourcesVfs["piece3.png"].readBitmap(), smoothing = false, anchorX = .5, anchorY = .5)

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


    }

    fun setGoal(point: Point){
        goalPoint = point
        initialDistToGoal = Point(goalPoint!!.x - x, goalPoint!!.y - y)
        rotation(Angle.fromRadians(atan2(initialDistToGoal.x, -initialDistToGoal.y)))
    }

    suspend fun moveInGoalDirection(){

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

     suspend fun die(){
        if(state == State.DYING) return
        state = State.DYING
         animate {

             // death animation
             addChild(piece1)
             addChild(piece2)
             addChild(piece3)
             removeChild(idle)

            val randAngle1 = Random.nextDouble(-PI, PI)
            val randAngle2 = Random.nextDouble(-PI, PI)
            val randAngle3 = Random.nextDouble(-PI, PI)

            val randDist1 = Random.nextDouble(-100.00, 100.0)
            val randDist2 = Random.nextDouble(-100.00, 100.0)
            val randDist3 = Random.nextDouble(-100.00, 100.0)

             val randDist4 = Random.nextDouble(-100.00, 100.0)
                val randDist5 = Random.nextDouble(-100.00, 100.0)
                val randDist6 = Random.nextDouble(-100.00, 100.0)

             parallel {
                 tween(piece1::x[randDist1], time = 1.seconds, easing = Easing.EASE_OUT_ELASTIC)
                 tween(piece1::y[randDist4], time = 1.seconds, easing = Easing.EASE_OUT_ELASTIC)
                 tween(piece1::rotation[Angle(randAngle1)], time = 1.seconds, easing = Easing.EASE_IN_OUT)
                 tween(piece1::scale[0.0], time = 1.seconds, easing = Easing.EASE_IN_OUT)

                 tween(piece2::x[randDist2], time = 1.seconds, easing = Easing.EASE_OUT_ELASTIC)
                 tween(piece2::y[randDist5], time = 1.seconds, easing = Easing.EASE_OUT_ELASTIC)
                 tween(piece2::rotation[Angle(randAngle2)], time = 1.seconds, easing = Easing.EASE_OUT_QUAD)
                 tween(piece2::scale[0.0], time = 1.seconds, easing = Easing.EASE_IN_OUT)

                 tween(piece3::y[randDist3], time = 1.seconds, easing = Easing.EASE_OUT_ELASTIC)
                 tween(piece3::x[randDist6], time = 1.seconds, easing = Easing.EASE_OUT_ELASTIC)
                 tween(piece3::rotation[Angle(randAngle3)], time = 1.seconds, easing = Easing.EASE_IN_OUT)
                 tween(piece3::scale[0.0], time = 1.seconds, easing = Easing.EASE_IN_OUT)
             }



         }

        i("xtxr Enemy.kt: die() called")
        removeFromParent()

    }

    fun despawn(){
        if (state == State.DYING) return
        removeFromParent()
    }
}
