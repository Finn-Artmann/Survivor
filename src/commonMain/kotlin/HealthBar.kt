import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

class HealthBar(val hwidth: Double) : Container() {

    val healthBarBg = solidRect(hwidth, 3.0, Colors.RED)
    val healthBar = solidRect(hwidth, 3.0, Colors.GREEN)

    fun setHealth(health: Double, maxHealth: Double){
        healthBar.scaledWidth = (health /  maxHealth) * hwidth
    }
}
