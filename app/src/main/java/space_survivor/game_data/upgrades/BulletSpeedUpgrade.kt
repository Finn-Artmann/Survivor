package space_survivor.game_data.upgrades

import space_survivor.game_data.views.Player

class BulletSpeedUpgrade(private var player: Player) : Upgrade {

    override var name = "Bullet Speed Multiplier"
    override var description = "Increases bullet speed by 1%"
    override var upgradeAmount = 0.01
    override fun apply() {
        player.bulletSpeedMul += upgradeAmount
    }
}