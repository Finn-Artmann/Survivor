package space_survivor.main

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.soywiz.klock.TimeSpan
import space_survivor.models.ScoreMemStore
import space_survivor.models.ScoreModel
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    var scores = ScoreMemStore()
    var account: GoogleSignInAccount? = null

    override fun onCreate(){
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("SpaceSurvivor started")

        scores.create(ScoreModel(0,"Player 1", TimeSpan(5000.0)))
        scores.create(ScoreModel(0,"Player 2", TimeSpan(6640.0)))
        scores.create(ScoreModel(0,"Player 3", TimeSpan(16640.0)))
    }
}