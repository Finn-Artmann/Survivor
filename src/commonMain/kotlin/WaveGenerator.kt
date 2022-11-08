import com.soywiz.klock.*
import com.soywiz.korge.scene.*
import com.soywiz.korma.geom.*
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*

class WaveGenerator(var scene: Scene, var enemies: MutableList<Enemy>) {

    var waveNumber = 0
    var enemiesPerWave = 5
    var waveTimer: TimeSpan = 0.seconds
    var nextWaveDelay : TimeSpan = 5.seconds


    fun checkNextWave(dt: TimeSpan){
        waveTimer += dt
        if(waveTimer >= nextWaveDelay){
            waveNumber++
            spawnEnemies(enemiesPerWave)
            enemiesPerWave++
            waveTimer = 0.seconds
        }

    }

    fun spawnEnemies(count: Int){

        for(i in 0..count-1){
            enemies.add(i ,Enemy())

            CoroutineScope(scene.coroutineContext).launch{
                val points = generateEnemyPoints()
                enemies[i].loadEnemy(points.first)
                enemies[i].setGoal(points.second)
            }
            scene.sceneView.addChild(enemies[i])
        }
    }

    private fun randPointLeft(margin: Double) : Point {
        return Point(
            Random.nextDouble(-margin,  -0.0),
            Random.nextDouble(-margin, scene.views.virtualHeight.toDouble() + margin)
        )
    }

    private fun randPointRight(margin: Double) : Point {
        return Point(
            Random.nextDouble(scene.views.virtualWidth.toDouble() , scene.views.virtualWidth.toDouble() + margin),
            Random.nextDouble(-margin, scene.views.virtualHeight.toDouble() + margin)
        )
    }

    private fun randPointTop(margin: Double) : Point {
        return Point(
            Random.nextDouble(-margin,  scene.views.virtualWidth.toDouble() + margin),
            Random.nextDouble(-margin, 0.0)
        )
    }

    private fun randPointBottom(margin: Double) : Point {
        return Point(
            Random.nextDouble(-margin, scene.views.virtualWidth.toDouble() + margin),
            Random.nextDouble(scene.views.virtualHeight.toDouble(), scene.views.virtualHeight.toDouble() + margin)
        )
    }


    private fun generateEnemyPoints() : Pair<Point, Point> {

        lateinit var spawnPoint: Point
        lateinit var movePoint: Point
        val margin = 40.0
        val randSide = Random.nextInt(0, 3)


        when(randSide){

            // Right
            0 -> {
                spawnPoint = randPointRight(margin)
                movePoint = randPointLeft(margin)
            }

            // Bottom
            1 -> {
                spawnPoint = randPointBottom(margin)
                movePoint = randPointTop(margin)
            }

            // Left
            2 -> {
                spawnPoint = randPointLeft(margin)
                movePoint = randPointRight(margin)
            }

            // Top
            3 -> {
                spawnPoint = randPointTop(margin)
                movePoint = randPointBottom(margin)
            }
        }


        return Pair(spawnPoint, movePoint)
    }
}
