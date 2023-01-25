package space_survivor.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.soywiz.kgl.KmlGlDummy
import space_survivor.R
import space_survivor.main.MainApp
import space_survivor.models.SavedPreference

class LoginViewModel(var app: MainApp) : ViewModel() {

    var mGoogleSignInClient: GoogleSignInClient
    var firebaseAuth: FirebaseAuth
    var webClientId = app.getString(R.string.default_web_client_id)
    val reqCode:Int=123

    init {
        FirebaseApp.initializeApp(app)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(app,gso)
        firebaseAuth= FirebaseAuth.getInstance()
    }

    fun signInAndSave(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful) {
                SavedPreference.setEmail(app,account.email.toString())
                SavedPreference.setUsername(app,account.displayName.toString())
                KmlGlDummy.finish()
            }
        }
    }

}

class LoginModelFactory(private val app : MainApp) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}