package space_survivor.game_data.views

import com.soywiz.korau.sound.SoundChannel
import com.soywiz.korau.sound.readSound
import com.soywiz.korge.input.mouse
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.SceneContainer
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.file.std.resourcesVfs
import space_survivor.game_data.scenes.GameScene
import space_survivor.game_data.upgrades.Upgrade
import space_survivor.game_data.util.GameState


class UpgradeMenu(private val gameScene: GameScene) : Container(){


    private var upgrades = mutableListOf<Upgrade>()
    private val buttonWidth = 400.0
    private val buttonHeight = 150.0

    init {
        gameScene.sceneContainer.addChild(this)
        positionX(0)
        positionY(0)
    }

    fun addUpgrade(upgrade: Upgrade){
        upgrades.add(upgrade)
    }

    fun createUpgradeView() {
        val verticalStack = uiVerticalStack(padding = 5.0) {
            // Set vertical stack properties
            positionX((gameScene.views.virtualWidth / 2) - buttonWidth )
            positionY(gameScene.views.virtualHeight / 4)
            scale = 2.0
            scaledWidth = buttonWidth * 2

            for (upgrade in upgrades) {
                val uiButtonString = upgrade.name + "\n" + upgrade.description
                val button = uiButton(uiButtonString) {
                    onClick {
                        upgrade.apply()
                        gameScene.sceneContainer.removeChild(this@UpgradeMenu)
                        gameScene.status = GameState.Status.RUNNING
                    }
                    uiSkin = UISkin{
                        textSize = 25.0
                        textColor = Colors.WHITE

                    }
                }
                button.scaledHeight = buttonHeight
                button.scaledWidth = buttonWidth


                addChild(button)
            }
        }


        addChild(verticalStack)
    }

}