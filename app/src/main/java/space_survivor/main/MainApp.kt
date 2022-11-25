package space_survivor.main

import android.app.Application
import com.soywiz.klock.TimeSpan
import space_survivor.models.ScoreModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    var scores = ArrayList<ScoreModel>()

    override fun onCreate(){
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("SpaceSurvivor started")

        scores.add(ScoreModel("Player 1", TimeSpan(5000.0)))
        scores.add(ScoreModel("Player 2", TimeSpan(6640.0)))
        scores.add(ScoreModel("Player 3", TimeSpan(16640.0)))
    }
}