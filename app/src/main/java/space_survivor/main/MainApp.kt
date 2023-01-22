package space_survivor.main

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.FirebaseApp
import space_survivor.models.ScoreFireBaseStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var scores : ScoreFireBaseStore
    var account: GoogleSignInAccount? = null

    override fun onCreate(){
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("SpaceSurvivor started")

        FirebaseApp.initializeApp(this)

        scores = ScoreFireBaseStore()

    }

}