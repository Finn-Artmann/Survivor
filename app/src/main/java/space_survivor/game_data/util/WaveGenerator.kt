package space_survivor.game_data.util

import com.soywiz.klock.*
import com.soywiz.korau.sound.readSound
import com.soywiz.korge.scene.*
import com.soywiz.korma.geom.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import space_survivor.game_data.views.Enemy
import kotlin.random.*
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.math.ceil

class WaveGenerator(var scene: Scene, var enemies: MutableList<Enemy>) {

    var waveNumber = 1
    var enemiesPerWave = 0
    var enenmyCountIncrement = 1
    var waveTimer: TimeSpan = 0.seconds
    var nextWaveDelay : TimeSpan = 5.seconds
    var maxWaveLength : Long = 6000 // milliseconds



    fun restart(){
        waveNumber = 1
        enemiesPerWave = 0
        enenmyCountIncrement = 1
        waveTimer = 0.seconds
        nextWaveDelay = 5.seconds
        maxWaveLength = 6000
    }

    // Returns true if the waveNumber was updated
    fun checkNextWave(dt: TimeSpan) : Boolean{
        waveTimer += dt
        if(waveTimer >= nextWaveDelay){

            if(waveNumber >= 30){

                // Spawn random amount of hunters (max 50%)and enemies with increased speed
                var hunterCount = Random.nextInt(0,  ceil(enemiesPerWave * 0.5).toInt())
                spawnEnemies(hunterCount, Enemy.Type.HUNTER)
                spawnEnemies(enemiesPerWave - hunterCount, Enemy.Type.DEFAULT, 9.0)
                enenmyCountIncrement = 3
                enemiesPerWave+=enenmyCountIncrement
            }
            else if(waveNumber >= 20){

                // Spawn random amount of hunters, max 20 % of the wave
                var hunterCount = Random.nextInt(0,  ceil(enemiesPerWave * 0.2).toInt())
                spawnEnemies(hunterCount, Enemy.Type.HUNTER)
                spawnEnemies(enemiesPerWave - hunterCount, Enemy.Type.DEFAULT)
                enenmyCountIncrement = 2
                enemiesPerWave+=enenmyCountIncrement
            }
            else{
                // Default case: Spawn default enemies
                spawnEnemies(enemiesPerWave, Enemy.Type.DEFAULT, 4.0)
                enemiesPerWave+=enenmyCountIncrement
            }
            waveTimer = 0.seconds
            waveNumber++
            return true
        }
        return false
    }

    private fun spawnEnemies(count: Int, type: Enemy.Type = Enemy.Type.DEFAULT, speed: Double = 4.0){
        CoroutineScope(scene.coroutineContext).launch {

            for (i in 0..count) {
                //delay(Random.nextLong(0, maxWaveLength))
                enemies.add(i, Enemy())

                val points = generateEnemyPoints()
                enemies[i].loadEnemy(points.first, type, speed)
                enemies[i].setGoal(points.second)

                scene.sceneView.addChild(enemies[i].apply { scale = 1.0})
            }
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
        val margin = 200.0

        when(Random.nextInt(0, 4)){

            // Right
            0 -> {
                spawnPoint = randPointRight(margin)
                movePoint = randPointLeft(1.0)
            }

            // Bottom
            1 -> {
                spawnPoint = randPointBottom(margin)
                movePoint = randPointTop(1.0)
            }

            // Left
            2 -> {
                spawnPoint = randPointLeft(margin)
                movePoint = randPointRight(1.0)
            }

            // Top
            3 -> {
                spawnPoint = randPointTop(margin)
                movePoint = randPointBottom(1.0)
            }
        }


        return Pair(spawnPoint, movePoint)
    }
}
