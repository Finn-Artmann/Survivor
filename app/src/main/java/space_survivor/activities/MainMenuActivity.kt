package space_survivor.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import space_survivor.databinding.ActivityMainMenuBinding
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import space_survivor.activities.GameActivity
import timber.log.Timber.i
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import space_survivor.R
import space_survivor.main.MainApp

class MainMenuActivity : AppCompatActivity() {

    public lateinit var binding: ActivityMainMenuBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var account: GoogleSignInAccount? = null
    private lateinit var app: MainApp

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        app = application as MainApp

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

        binding.playButton.setOnClickListener {

            val launcherIntent = Intent(this, GameActivity::class.java);
            startActivity(launcherIntent )
        }

        binding.scoreboardButton.setOnClickListener {

            val launcherIntent = Intent(this, ScoreboardActivity::class.java);
            startActivity(launcherIntent )
        }

        binding.loginButton.setOnClickListener {

            val launcherIntent = Intent(this, LoginActivity::class.java);
            startActivity(launcherIntent )
        }

        binding.logoutButton.setOnClickListener {


            mGoogleSignInClient.signOut()
            checkUserLoggedIn()
        }

        checkUserLoggedIn()
    }

    override fun onResume() {
        super.onResume()
        checkUserLoggedIn()
    }

    override fun onPause() {
        super.onPause()

    }

    private fun checkUserLoggedIn(){
        // Check if user is signed in (non-null) and update UI accordingly.
        account = GoogleSignIn.getLastSignedInAccount(this)
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            app.account = account
            binding.loginButton.isEnabled = false
            binding.loginButton.visibility = View.INVISIBLE
            binding.logoutButton.isEnabled = true
            binding.logoutButton.visibility = View.VISIBLE

            binding.usernameText.text = getString(R.string.signed_in_text, account?.displayName)
        }
        else{
            binding.loginButton.isEnabled = true
            binding.loginButton.visibility = View.VISIBLE
            binding.logoutButton.isEnabled = false
            binding.logoutButton.visibility = View.INVISIBLE

            binding.usernameText.text = getString(R.string.signed_out_text)
        }
    }

}







