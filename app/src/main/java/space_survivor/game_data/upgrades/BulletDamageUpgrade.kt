package space_survivor.game_data.upgrades

import space_survivor.game_data.views.Player

class BulletDamageUpgrade(private var player: Player) : Upgrade {

    override var name = "Bullet Damage Multiplier"
    override var description = "Increases bullet damage by 30%"
    override var upgradeAmount = 0.30
    override fun apply() {
        player.bulletDamageMul += upgradeAmount
    }
}