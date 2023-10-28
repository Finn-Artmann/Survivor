package space_survivor.game_data.upgrades

import space_survivor.game_data.views.Player

class SpeedUpgrade(private var player: Player) : Upgrade {

    override var name = "Speed Upgrade"
    override var description = "Increases the player's speed by 10"
    override var upgradeAmount = 10.0

    override fun apply() {
        player.moveSpeed += upgradeAmount
    }
}