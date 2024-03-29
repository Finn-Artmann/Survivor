package space_survivor.game_data.views


import com.soywiz.klock.milliseconds
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
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import timber.log.Timber.i
import kotlin.math.atan2



class Player(private val sceneView: Container) : Container(){

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
    private lateinit var damageSound: Sound
    private val fieldMargin = 150
    lateinit var state: State
    lateinit var healthBar: HealthBar

    private var damageCoroutine: Deferred<Unit>? = null
    private var damageSoundCoroutine: Deferred<Unit>? = null


    private var hitRadius = 10.0
    var moveSpeed = 300.0
    var moveX = 0.0
    var moveY = 0.0
    var colliding = false

    // Stats
    var health = 100.0
    var maxHealth = 100.0
    var bulletDamageMul = 1.0
    var bulletSpeedMul = 1.0



    suspend fun loadPlayer(initialXPos: Double, initialYPos: Double, virtWidth: Int, virtHeight: Int) {
        i("Player.kt: loadPlayer() called")
        position(initialXPos, initialYPos)
        state = State.IDLE

        damageSound = resourcesVfs["explosion2.mp3"].readSound()
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
            takeDamage(6.0)
            moveSpeed = 50.0
            colliding = true

            // reset speed and collision after 0.2 seconds
            GlobalScope.launch {
                delay(200.milliseconds)
                moveSpeed = 300.0
                colliding = false
            }
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

        addFixedUpdater(1.seconds){
            GlobalScope.launch {
                shootBullet()
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

    private fun takeDamage(damage: Double) {

        if (state != State.IDLE) return
        state = State.DAMAGED

        healthBar.setHealth(health, maxHealth)

        if (health > 0) {
            health -= damage
        }

        setDamageImage()

        // Check if player is dead
        if (health <= 0 && state != State.DEAD) {
            die()
        }
        damageSoundCoroutine = GlobalScope.async {
            damageSound.play()
        }


        damageCoroutine = GlobalScope.async {

            i("Damage coroutine started")
            fullHealth.colorMul = Colors.RED
            slightDamage.colorMul = Colors.RED
            damaged.colorMul = Colors.RED
            veryDamaged.colorMul = Colors.RED
            delay(0.1.seconds)
            fullHealth.colorMul = Colors.WHITE
            slightDamage.colorMul = Colors.WHITE
            damaged.colorMul = Colors.WHITE
            veryDamaged.colorMul = Colors.WHITE

            state = State.IDLE
            i("Damage coroutine ended")
        }

        // Register a callback to cancel the coroutine when the function returns
        damageCoroutine?.invokeOnCompletion {
            damageCoroutine?.cancel()
        }

    }

    private suspend fun shootBullet() {
        i("Player.kt: shootBullet() called")

        val bullet = Bullet(bulletDamageMul, bulletSpeedMul)
        sceneView.addChild(bullet)
        bullet.loadBullet(x, y, rotation)
    }

    private fun die() {
        state = State.DEAD
        damageSoundCoroutine?.cancel()
        launch(GlobalScope.coroutineContext) {
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

        return moveX != 0.0 || moveY != 0.0
    }


}
