package space_survivor.game_data.upgrades

import space_survivor.game_data.views.Player

class HealthUpgrade(private var player: Player) : Upgrade {

        override var name = "Health Upgrade"
        override var description = "Increases the player's health by 10"
        override var upgradeAmount = 10.0

        override fun apply() {
            player.maxHealth += upgradeAmount
            player.health = player.maxHealth
            player.healthBar.setHealth(player.health, player.maxHealth)
            player.setDamageImage()
        }
}