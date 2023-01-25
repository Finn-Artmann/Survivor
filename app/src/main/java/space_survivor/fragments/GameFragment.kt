package space_survivor.fragments

import android.animation.AnimatorInflater
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.soywiz.korge.android.KorgeAndroidView
import space_survivor.R
import space_survivor.databinding.FragmentGameBinding
import space_survivor.main.MainApp
import space_survivor.view_models.GameViewModel
import space_survivor.view_models.GameViewModelFactory

class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var korgeAndroidView: KorgeAndroidView
    private lateinit var binding: FragmentGameBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
                GameViewModelFactory(requireActivity().application,
                DisplayMetrics())
        )[GameViewModel::class.java]
        viewModel.app = activity?.application as MainApp
    }

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

        viewModel.displayMetrics = resources.displayMetrics
        korgeAndroidView = KorgeAndroidView(viewModel.app)
        viewModel.loadToolModule(korgeAndroidView)
        binding.toolContainer.addView(korgeAndroidView)
        binding.exitButton.setOnClickListener {
            // Animate the button using the Animator class
            val animator = AnimatorInflater.loadAnimator(
                viewModel.app,
                R.animator.exit_button_animator
            )
            animator.setTarget(view)
            animator.start()

            viewModel.resetGameState()

            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_gameFragment_to_mainMenuFragment)
            }, animator.duration)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val parent = korgeAndroidView.parent as ViewGroup
        parent.removeView(korgeAndroidView)
        viewModel.unloadToolModule(korgeAndroidView)
        val displayMetrics = DisplayMetrics()
        val activity = activity ?: return
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        viewModel.displayMetrics = displayMetrics
        korgeAndroidView = KorgeAndroidView(viewModel.app)
        viewModel.loadToolModule(korgeAndroidView)
        binding.toolContainer.addView(korgeAndroidView)

    }

    override fun onPause() {
        super.onPause()
        viewModel.unloadToolModule(korgeAndroidView)
    }
}
