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
import com.soywiz.korma.geom.Angle
import kotlinx.coroutines.*
import kotlin.math.atan2



class Player : Container(){

    enum class State{
        IDLE,
        MOVING,
        DAMAGED,
        DEAD
    }

    private lateinit var fullHealth: Image
    private lateinit var slightDamage: Image
    private lateinit var damaged: Image
    private lateinit var veryDamaged: Image
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
        fullHealth = Image(
            resourcesVfs["Main Ship - Base - Full health.png"].readBitmap(),
            smoothing = false,
            anchorX = .5
        )

        slightDamage = Image(
            resourcesVfs["Main Ship - Base - Slight damage.png"].readBitmap(),
            smoothing = false,
            anchorX = .5
        ).apply { visible = false }

        damaged = Image(
            resourcesVfs["Main Ship - Base - Damaged.png"].readBitmap(),
            smoothing = false,
            anchorX = .5
        ).apply { visible = false }

        veryDamaged = Image(
            resourcesVfs["Main Ship - Base - Very damaged.png"].readBitmap(),
            smoothing = false,
            anchorX = .5
        ).apply { visible = false }

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


        addChild(fullHealth)
        addChild(slightDamage)
        addChild(damaged)
        addChild(veryDamaged)
        addChild(healthBar)
    }

    fun restart(){
        health = maxHealth
        healthBar.setHealth(health, maxHealth)
        state = State.IDLE
    }

    fun takeDamage(damage: Double) {
        if (state != State.IDLE) return;
        state = State.DAMAGED

        if (health > 0) {
            health -= damage
        }

        healthBar.setHealth(health, maxHealth)

        launchImmediately(GlobalScope.coroutineContext) {
            fullHealth.colorMul = Colors.RED
            delay(0.1.seconds)
            fullHealth.colorMul = Colors.WHITE
            state = State.IDLE
        }

        setDamageImage()

        // Check if player is dead
        if (health <= 0 && state != State.DEAD) {
            die()
        }
    }

    fun die() {
        state = State.DEAD
        launchImmediately(GlobalScope.coroutineContext) {
            // Play death animation
            this@Player.tween(this@Player::rotation[Angle.fromDegrees(360.0)], time = 1.seconds)
            this@Player.tween(this@Player::alpha[0.0], time = 1.seconds)

        }

    }

    fun setDamageImage(){

        var healthPercentage = health / maxHealth

        when {
            healthPercentage > 0.75 -> {
                fullHealth.visible = true
                slightDamage.visible = false
                damaged.visible = false
                veryDamaged.visible = false
            }
            healthPercentage > 0.50-> {
                fullHealth.visible = false
                slightDamage.visible = true
                damaged.visible = false
                veryDamaged.visible = false
            }
            healthPercentage > 0.25 -> {
                fullHealth.visible = false
                slightDamage.visible = false
                damaged.visible = true
                veryDamaged.visible = false
            }
            else -> {
                fullHealth.visible = false
                slightDamage.visible = false
                damaged.visible = false
                veryDamaged.visible = true
            }
        }
    }

    fun isMoving() : Boolean{

        return moveX != 0.0 || moveY != 0.0;
    }

}
