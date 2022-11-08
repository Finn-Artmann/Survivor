import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

class HealthBar(val hwidth: Double) : Container() {

    val healthBarBg = solidRect(hwidth, 3.0, Colors.RED)
    val healthBar = solidRect(hwidth, 3.0, Colors.GREEN)

    fun setHealth(health: Double, maxHealth: Double){
        if(health <= 0)
            healthBar.scaledWidth = 0.0
        else
            healthBar.scaledWidth = (health /  maxHealth) * hwidth
    }
}
