
import com.soywiz.korge.view.*
import com.soywiz.korio.file.std.*
import com.soywiz.korim.format.readBitmap

class Player : Container(){

    private lateinit var idle: Image
    var moveSpeed = 600.0

    suspend fun loadPlayer(initialXPos: Double, initialYPos: Double) {

        this.position(initialXPos, initialYPos)

        idle = Image(
            resourcesVfs["Foozle_2DS0011_Void_MainShip/Main Ship/Main Ship - Bases/PNGs/Main Ship - Base - Full health.png"].readBitmap(),
            smoothing = false
        )

        hitShape {
            circle(width / 2)
        }

        addChild(idle)

    }
}
