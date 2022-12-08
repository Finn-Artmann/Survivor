package space_survivor.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soywiz.klock.ISO8601
import com.soywiz.klock.TimeSpan
import space_survivor.databinding.CardScoreBinding
import space_survivor.models.ScoreModel

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
            if(score.score != null) {
                binding.scoreNumber.text = ISO8601.TIME_LOCAL_COMPLETE.format(TimeSpan(score.score!!.toDouble()))
                binding.playerName.text = score.playerName
            }
        }
    }
}