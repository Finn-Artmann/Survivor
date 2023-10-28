package space_survivor.game_data.upgrades

import space_survivor.game_data.views.Player
import space_survivor.game_data.views.UpgradeMenu

class UpgradeManager(private val player: Player, private val upgradeMenu: UpgradeMenu) {

    private val availableUpgrades = listOf(
        HealthUpgrade(player),
        SpeedUpgrade(player)

    )
    fun selectRandomUpgrades(numUpgrades: Int = 3){
        val randomUpgrades = availableUpgrades.shuffled().take(numUpgrades)
        randomUpgrades.forEach { upgradeMenu.addUpgrade(it) }
    }


}