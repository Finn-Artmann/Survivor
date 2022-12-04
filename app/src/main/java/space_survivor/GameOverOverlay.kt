package space_survivor

import com.soywiz.korau.sound.SoundChannel
import com.soywiz.korau.sound.readSound
import com.soywiz.korge.input.mouse
import com.soywiz.korge.scene.SceneContainer
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korio.file.std.resourcesVfs


class GameOverOverlay(val sceneContainer: SceneContainer, val onRestart: () -> Unit) : Container() {

    private lateinit var gameOverText: Text
    private lateinit var restartText: Text
    private lateinit var backgound: SolidRect
    private lateinit var gameOverSound: SoundChannel



    suspend fun load(){

        gameOverSound = resourcesVfs["gameover_loud.mp3"].readSound().play()

        backgound = solidRect(
            sceneContainer.views.virtualWidth,
            sceneContainer.views.virtualHeight,
            Colors.BLACK
        ).alpha(0.5)


        gameOverText = text("Game Over!")
            .centerXOnStage()
            .apply { textSize = 100.0 }

        // Center the text on the screen
        gameOverText.position(
            gameOverText.x - gameOverText.width / 2,
            sceneContainer.views.virtualHeight / 4 - gameOverText.height / 2
        )

        restartText = text(" > Restart <", color = Colors.GREEN)
            .centerXOnStage().apply { textSize = 50.0 }

        // Center the text on the screen
        restartText.position(
            restartText.x - restartText.width / 2,
            sceneContainer.views.virtualHeight / 3 - restartText.height / 2
        )


        restartText.mouse {
            onClick { restart() }
        }
    }

    private fun restart() {
        this.removeFromParent()
        onRestart()
    }

}