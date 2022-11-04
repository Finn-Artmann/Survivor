package models

import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korio.file.*
import views.*

enum class State{
    IDLE,
    MOVING
}

data class EnemyModel(

    var id: Long = 0,
    var state: State = State.IDLE,
    var image: VfsFile = resourcesVfs["Galactica Ranger/Galactica_Ranger_A.png"],
    var goalPoint: Point? = null,
    var moveSpeed: Double = 100.0,
    var health: Double = 100.0,
    var initialDistToGoal: Point = Point(0.0, 0.0),
    var view: EnemyView? = null
)
