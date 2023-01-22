package space_survivor.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import space_survivor.R
import space_survivor.databinding.FragmentMainMenuBinding
import space_survivor.main.MainApp

class MainMenuFragment : Fragment(){

    private lateinit var binding: FragmentMainMenuBinding
    private lateinit var navController: NavController

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private var account: GoogleSignInAccount? = null
    private lateinit var app: MainApp

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        binding = FragmentMainMenuBinding.bind(view)
        app = activity?.application as MainApp


        binding.playButton.setOnClickListener {
            navController.navigate(R.id.action_mainMenuFragment_to_gameFragment)
        }

        binding.statisticsButton.setOnClickListener {
            navController.navigate(R.id.action_mainMenuFragment_to_statisticsFragment)
        }

        binding.scoreboardButton.setOnClickListener {
            navController.navigate(R.id.action_mainMenuFragment_to_scoreboardFragment)
        }

        binding.loginButton.setOnClickListener {
            navController.navigate(R.id.action_mainMenuFragment_to_loginFragment)
        }

        binding.logoutButton.setOnClickListener {
            mGoogleSignInClient.signOut()
            app.account = null
            checkUserLoggedIn()
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(app,gso)
        checkUserLoggedIn()

    }

    override fun onResume() {
        super.onResume()
        checkUserLoggedIn()
    }


    private fun checkUserLoggedIn(){
        // Check if user is signed in (non-null) and update UI accordingly.
        account = GoogleSignIn.getLastSignedInAccount(app)
        if (GoogleSignIn.getLastSignedInAccount(app) != null) {
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