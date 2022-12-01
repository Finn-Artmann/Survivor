package com.example.space_survivor


import com.soywiz.klock.seconds
import com.soywiz.korau.sound.*
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.file.std.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.shape.*
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext


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



    var moveSpeed = 600.0
    var moveX = 0.0
    var moveY = 0.0
    var health = 100.0
    var maxHealth = 100.0

    suspend fun loadPlayer(initialXPos: Double, initialYPos: Double) {
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

        var hitRadius = 13.0
        hitShape { Shape2d.Circle(hitRadius /2 , hitRadius /2, hitRadius) }
        var circ = circle{ radius = hitRadius; fill = Colors.RED}
        circ.x = -hitRadius
        circ.y = -hitRadius


        onCollision {

            if (it is Enemy){
                takeDamage(1.0)
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

               //idle.colorMul = Colors.RED
               //delay(0.1.seconds)
               //idle.colorMul = Colors.WHITE
               //state = State.IDLE

    }



    fun isMoving() : Boolean{

        return moveX != 0.0 || moveY != 0.0;
    }

}
