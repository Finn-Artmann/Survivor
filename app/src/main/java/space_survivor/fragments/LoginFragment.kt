package space_survivor.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.soywiz.kgl.KmlGlDummy.finish
import space_survivor.activities.MainMenuActivity
import space_survivor.databinding.FragmentLoginBinding
import space_survivor.main.MainApp
import space_survivor.view_models.LoginModelFactory
import space_survivor.view_models.LoginViewModel

class LoginFragment :  Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel : LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,
            LoginModelFactory(requireActivity().application as MainApp)
        )[LoginViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = viewModel.app

        binding.Signin.setOnClickListener{
            Toast.makeText(app,"Logging In",Toast.LENGTH_SHORT).show()
            signInGoogle()
        }
    }

    private fun signInGoogle(){

        val signInIntent: Intent = viewModel.mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,viewModel.reqCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==viewModel.reqCode){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
//            firebaseAuthWithGoogle(account!!)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount = completedTask.
                getResult(ApiException::class.java)
                ?: return

            viewModel.signInAndSave(account)
            val intent = Intent(activity, MainMenuActivity::class.java)
            startActivity(intent)

        } catch (e: ApiException){
            Toast.makeText(viewModel.app ,e.toString(), Toast.LENGTH_SHORT).show()
        }
    }


    override fun onStart() {
        super.onStart()
        if(GoogleSignIn.getLastSignedInAccount(viewModel.app)!=null){

            val intent = Intent(viewModel.app, MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}