package com.example.space_survivor


import android.util.Log.i
import com.soywiz.klock.seconds
import com.soywiz.korau.sound.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.file.std.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.shape.*
import kotlinx.coroutines.*
import kotlin.math.atan2



class Player : Container(){

    enum class State{
        IDLE,
        MOVING,
        DAMAGED
    }

    private lateinit var idle: Image
    lateinit var moveSound: Sound
    lateinit var damageSound: Sound
    lateinit var state: State
    lateinit var healthBar: HealthBar
    var hitRadius = 10.0


    var moveSpeed = 300.0
    var moveX = 0.0
    var moveY = 0.0
    var health = 100.0
    var maxHealth = 100.0
    private val fieldMargin = 150
    var colliding = false

    suspend fun loadPlayer(initialXPos: Double, initialYPos: Double, virtWidth: Int, virtHeight: Int) {
        position(initialXPos, initialYPos)
        state = State.IDLE

        damageSound = resourcesVfs["destroyed_stones.mp3"].readSound()
        moveSound = resourcesVfs["mystic.mp3"].readSound()
        idle = Image(
            resourcesVfs["Main Ship - Base - Full health.png"].readBitmap(),
            smoothing = false,
            anchorX = .5
        )

        healthBar = HealthBar(30.0).centerXOn(this).positionY(height + 20.0)

        var hitCircle = circle{ radius = hitRadius; fill = Colors.RED}
        hitCircle.x = -hitRadius
        hitCircle.y = -hitRadius
        hitCircle.visible = false

        hitCircle.onCollision(filter = { it != this && it is Circle}) {
            takeDamage(1.0)
            moveSpeed = 50.0
            colliding = true
        }

        hitCircle.onCollisionExit(filter = { it != this && it is Circle}) {
            moveSpeed = 300.0
            colliding = false
        }

        addUpdater {

            rotation(Angle.fromRadians(atan2(moveX, -moveY)))

            if(moveX <= 0.0 && x > fieldMargin ){

                x += moveX * moveSpeed * it.seconds
            }

            if(moveX >= 0.0 && x < virtWidth - fieldMargin ){

                x += moveX * moveSpeed * it.seconds
            }

            if(moveY <= 0.0 && y > fieldMargin){

                y += moveY * moveSpeed * it.seconds
            }

            if(moveY >= 0.0 && y < virtHeight - fieldMargin){

                y += moveY * moveSpeed * it.seconds
            }
        }


        addChild(idle)
        addChild(healthBar)
    }

    fun takeDamage(damage: Double) {
        if (state != State.IDLE) return;
        state = State.DAMAGED
        if (health > 0) {
            health -= damage
        }
        healthBar.setHealth(health, maxHealth)



        //TODO add damage sound and animation
        launchImmediately(GlobalScope.coroutineContext) {
            idle.colorMul = Colors.RED
            delay(0.1.seconds)
            idle.colorMul = Colors.WHITE
            state = State.IDLE
        }

    }


    fun isMoving() : Boolean{

        return moveX != 0.0 || moveY != 0.0;
    }

}
