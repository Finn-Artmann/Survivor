package com.example.space_survivor

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

    var waveNumber = 1
    var enemiesPerWave = 0
    var enenmyCountIncrement = 1
    var waveTimer: TimeSpan = 0.seconds
    var nextWaveDelay : TimeSpan = 5.seconds
    var maxWaveLength : Long = 6000 // milliseconds

    var enableHunters = 20


    fun checkNextWave(dt: TimeSpan){
        waveTimer += dt
        if(waveTimer >= nextWaveDelay){

            if(waveNumber >= 40){

                // Spawn random amount of hunters and enemies with increased speed
                var hunterCount = Random.nextInt(0, enemiesPerWave)
                spawnEnemies(hunterCount, Enemy.Type.HUNTER, 300.0)
                spawnEnemies(enemiesPerWave - hunterCount, Enemy.Type.DEFAULT, 400.0)
                enemiesPerWave+=enenmyCountIncrement
            }
            else if(waveNumber >= 20){

                // Spawn random amount of hunters
                var hunterCount = Random.nextInt(0, enemiesPerWave)
                spawnEnemies(hunterCount, Enemy.Type.HUNTER)
                spawnEnemies(enemiesPerWave - hunterCount, Enemy.Type.DEFAULT)
                enemiesPerWave+=enenmyCountIncrement
            }
            else{
                // Default case: Spawn default enemies
                spawnEnemies(enemiesPerWave)
                enemiesPerWave+=enenmyCountIncrement
            }
            waveTimer = 0.seconds
            waveNumber++
        }
    }

    fun spawnEnemies(count: Int, type: Enemy.Type = Enemy.Type.DEFAULT, speed: Double = 200.0){
        CoroutineScope(scene.coroutineContext).launch {

            for (i in 0..count) {
                //delay(Random.nextLong(0, maxWaveLength))
                enemies.add(i, Enemy())

                val points = generateEnemyPoints()
                enemies[i].loadEnemy(points.first, type)
                enemies[i].setGoal(points.second)

                scene.sceneView.addChild(enemies[i])
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
        val margin = 60.0
        val randSide = Random.nextInt(0, 3)


        when(randSide){

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
