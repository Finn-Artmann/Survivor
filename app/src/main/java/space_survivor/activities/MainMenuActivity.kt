package space_survivor.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import space_survivor.databinding.ActivityMainMenuBinding
import androidx.recyclerview.widget.RecyclerView
import space_survivor.activities.GameActivity

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.playButton.setOnClickListener {
            binding.playButton.isEnabled = true
            binding.playButton.isEnabled = true

            val launcherIntent = Intent(this, GameActivity::class.java);
            startActivity(launcherIntent )
        }

        binding.scoreboardButton.setOnClickListener {
            binding.scoreboardButton.isEnabled = true
            binding.scoreboardButton.isEnabled = true

            val launcherIntent = Intent(this, ScoreboardActivity::class.java);
            startActivity(launcherIntent )
        }

        binding.loginButton.setOnClickListener {
            binding.loginButton.isEnabled = true
            binding.loginButton.isEnabled = true

            val launcherIntent = Intent(this, LoginActivity::class.java);
            startActivity(launcherIntent )
        }

    }

    override fun onResume() {
        super.onResume()
        binding.playButton.isEnabled = true
        binding.scoreboardButton.isEnabled = true

    }

    override fun onPause() {
        super.onPause()

    }

}







