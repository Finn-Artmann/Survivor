package space_survivor.game_data.upgrades

interface Upgrade {
    var name: String
    var description: String
    var upgradeAmount: Double

    fun apply()
}