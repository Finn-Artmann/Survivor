package space_survivor.game_data.util

import com.soywiz.klock.TimeSpan
import space_survivor.game_data.views.Player

class GameState(val player : Player,
                val waveGen : WaveGenerator,
                val timer : TimeSpan,
                val status : Status,
) {
    enum class Status {
        RUNNING,
        PAUSED,
        GAME_OVER
    }
}