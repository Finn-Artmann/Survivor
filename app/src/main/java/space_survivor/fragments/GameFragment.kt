package space_survivor.fragments

import android.animation.AnimatorInflater
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.soywiz.korge.android.KorgeAndroidView
import space_survivor.R
import space_survivor.databinding.FragmentGameBinding
import space_survivor.game_data.util.CustomModule
import space_survivor.main.MainApp

class GameFragment : Fragment() {

    private lateinit var korgeAndroidView: KorgeAndroidView
    private lateinit var binding: FragmentGameBinding
    private lateinit var app : MainApp

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app = activity?.application as MainApp

        korgeAndroidView = KorgeAndroidView(app)
        binding.toolContainer.addView(korgeAndroidView)

        binding.exitButton.setOnClickListener {
            // Animate the button using the Animator class
            val animator = AnimatorInflater.loadAnimator(app, R.animator.exit_button_animator)
            animator.setTarget(view)
            animator.start()

            // Finish the activity after the animation has completed
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_gameFragment_to_mainMenuFragment)
            }, animator.duration)
        }

        loadToolModule()
    }

    private fun loadToolModule() {

        // get screen height and width according to api level 32
        val displayMetrics = DisplayMetrics()
        val activity = activity ?: return
        activity.windowManager.currentWindowMetrics.bounds.let {
            displayMetrics.widthPixels = it.width()
            displayMetrics.heightPixels = it.height()
        }

        korgeAndroidView.loadModule(CustomModule(app, width =   displayMetrics.widthPixels+100, height = displayMetrics.heightPixels, callback = {
            println("Callback from android app")
        }))
    }

    private fun unloadToolModule() {
        korgeAndroidView.unloadModule()
    }
}
