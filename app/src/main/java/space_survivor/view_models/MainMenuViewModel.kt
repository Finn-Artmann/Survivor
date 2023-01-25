package space_survivor.view_models

import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import space_survivor.main.MainApp

class MainMenuViewModel() : ViewModel() {

    var mGoogleSignInClient: GoogleSignInClient? = null
    var app : MainApp? = null
    var webClientId : String? = null

    fun setGoogleSignInClient() {
        val gso = webClientId?.let {
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(it)
                .requestEmail()
                .build()
        }

        if (gso != null && app != null) {
            mGoogleSignInClient = GoogleSignIn.getClient(app!!, gso)
        }
    }

}
