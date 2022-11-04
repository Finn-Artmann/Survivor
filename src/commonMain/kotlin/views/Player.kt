package views
import com.soywiz.korau.sound.*
import com.soywiz.korge.view.*
import com.soywiz.korio.file.std.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korma.geom.shape.*


class Player : Container(){

    enum class State{
        IDLE,
        MOVING
    }

    private lateinit var idle: Image
    lateinit var moveSound: Sound
    lateinit var damageSound: Sound
    lateinit var state: State
    lateinit var soundChannel: SoundChannel
    lateinit var healthBar: HealthBar


    var moveSpeed = 600.0
    var moveX = 0.0
    var moveY = 0.0
    var health = 100.0
    var maxHealth = 100.0

    suspend fun loadPlayer(initialXPos: Double, initialYPos: Double) {
        position(initialXPos, initialYPos)
        state = State.IDLE
        damageSound = resourcesVfs["!SFX + MUSIC!/Audio/SFX/Space, Robotic, Futuristic/destroyed_stones.mp3"].readSound()
        moveSound = resourcesVfs["!SFX + MUSIC!/Audio/SFX/Space, Robotic, Futuristic/mystic.mp3"].readSound()
        idle = Image(
            resourcesVfs["Foozle_2DS0011_Void_MainShip/Main Ship/Main Ship - Bases/PNGs/Main Ship - Base - Full health.png"].readBitmap(),
            smoothing = false,
            anchorX = .5
        )

        healthBar = HealthBar(30.0).centerXOn(this).positionY(height + 20.0)


        //hitShape { circle { radius = 30.0 } }
        hitShape2d = Shape2d.Circle(48.0 /2 , 48.0 /2, 48.0)
        //var c = circle(48.0 /2).center()


        addChild(idle)
        addChild(healthBar)
    }


    fun isMoving() : Boolean{

        return moveX != 0.0 || moveY != 0.0;
    }
}
