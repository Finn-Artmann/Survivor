package space_survivor.game_data.views

import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.*
import kotlin.math.cos
import kotlin.math.sin
import timber.log.Timber.i

class Bullet : Container() {

    var damage = 100.0
    var bulletspeed = 30.0
    suspend fun loadBullet(x: Double, y: Double, rotation: Angle){
        i("Bullet created at $x, $y")

        position(x,y)
        // rotate by 90 degrees to make it point in the right direction
        rotation(rotation - 90.degrees)
        solidRect(10, 5, Colors.MAGENTA)

        addUpdater {

            // Move bullet forward
            val moveX = cos(this.rotation.radians) * bulletspeed
            val moveY = sin(this.rotation.radians) * bulletspeed
            this.x += moveX
            this.y += moveY

        }

        onCollision { other ->
            i("Bullet collided with $other")

            if (other is Enemy) {
                other.health -= damage
                i("Enemy health: ${other.health}")
                this.removeFromParent()
            }
        }
    }
}
