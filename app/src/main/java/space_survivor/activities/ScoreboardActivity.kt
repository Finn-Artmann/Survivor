package space_survivor.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.space_survivor.R
import com.example.space_survivor.databinding.ActivityScoreboardBinding
import com.example.space_survivor.databinding.CardScoreBinding
import com.soywiz.klock.ISO8601
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
        binding.recyclerView.adapter = ScoreAdapter(app.scores)
    }
}

class ScoreAdapter constructor(private var scores: List<ScoreModel>) :
    RecyclerView.Adapter<ScoreAdapter.MainHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardScoreBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val score = scores[holder.adapterPosition]
        holder.bind(score)
    }

    override fun getItemCount(): Int = scores.size

    class MainHolder(private val binding : CardScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(score: ScoreModel) {
            binding.scoreNumber.text = ISO8601.TIME_LOCAL_COMPLETE.format(score.score)
            binding.playerName.text = score.playerName
        }
    }
}