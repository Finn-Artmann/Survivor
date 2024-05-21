package space_survivor.fragments

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import space_survivor.R
import space_survivor.databinding.FragmentMainMenuBinding
import space_survivor.main.MainApp
import space_survivor.view_models.MainMenuViewModel

class MainMenuFragment : Fragment(){

    private lateinit var binding: FragmentMainMenuBinding
    private lateinit var viewModel: MainMenuViewModel
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainMenuViewModel::class.java]
        viewModel.app = activity?.application as MainApp
        if (viewModel.app != null) {
            viewModel.webClientId = viewModel.app!!.getString(
                R.string.default_web_client_id
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        navController = Navigation.findNavController(view)
        binding = FragmentMainMenuBinding.bind(view)

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
            viewModel.mGoogleSignInClient?.signOut()
            viewModel.app?.account = null
            updateLoggedInUI()
        }

        viewModel.setGoogleSignInClient()
        updateLoggedInUI()
    }


    private fun updateLoggedInUI(){
        // Check if user is signed in (non-null) and update UI accordingly.
        if(viewModel.app == null) return

        val app = viewModel.app!!
        val account = GoogleSignIn.getLastSignedInAccount(app)
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

    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

}
