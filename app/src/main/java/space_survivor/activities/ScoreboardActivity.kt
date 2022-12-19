package space_survivor.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import space_survivor.R
import space_survivor.databinding.ActivityScoreboardBinding
import space_survivor.databinding.CardScoreBinding
import com.soywiz.klock.ISO8601
import space_survivor.adapters.ScoreAdapter
import space_survivor.main.MainApp
import space_survivor.models.ScoreModel


class ScoreboardActivity : AppCompatActivity() {

    lateinit var app : MainApp
    private lateinit var binding: ActivityScoreboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScoreboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        // Display the top 100 scores in the recycler view sorted by highest score
        binding.recyclerView.adapter = ScoreAdapter(app.scores.findAll().sortedByDescending { it.score }.take(100))
    }

    // Swipe refresh layout
    override fun onResume() {
        super.onResume()
        binding.recyclerView.adapter?.notifyDataSetChanged()
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.recyclerView.adapter?.notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}

