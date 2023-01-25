package space_survivor.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import space_survivor.R
import space_survivor.adapters.ScoreAdapter
import space_survivor.databinding.FragmentScoreboardBinding
import space_survivor.main.MainApp
import space_survivor.view_models.ScoreboardViewModel
import timber.log.Timber.i

class ScoreboardFragment : Fragment() {

    private lateinit var binding: FragmentScoreboardBinding
    private lateinit var viewModel: ScoreboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ScoreboardViewModel::class.java]
        viewModel.app = activity?.application as MainApp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScoreboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        updateScores()

        binding.swipeRefreshLayout.setOnRefreshListener {
            updateScores()
            binding.swipeRefreshLayout.isRefreshing = false
        }

    }

    override fun onResume() {
        super.onResume()
        if(viewModel.app == null) return
        binding.recyclerView.adapter = ScoreAdapter(
            viewModel.app!!.scores.findAll().sortedByDescending { it.score }.take(100)
        )
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {

        // print debug message
        i("onConfigurationChanged called")

        super.onConfigurationChanged(newConfig)
        val imageView = view?.findViewById<ImageView>(R.id.imageView2) ?: return

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            imageView.scaleType = ImageView.ScaleType.FIT_XY
        }
    }


    private fun updateScores(){

        if (viewModel.app == null) return
        // Display the top 100 scores in the recycler view sorted by highest score
        binding.recyclerView.adapter = ScoreAdapter(
            viewModel.app!!.scores.findAll().sortedByDescending { it.score }.take(100))
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}

