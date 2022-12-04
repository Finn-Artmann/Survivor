package space_survivor.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import space_survivor.databinding.GameActivityBinding

import com.soywiz.korge.android.KorgeAndroidView
import space_survivor.CustomModule

class GameActivity : AppCompatActivity() {

    private lateinit var korgeAndroidView: KorgeAndroidView
    private lateinit var binding: GameActivityBinding
    private lateinit var app : space_survivor.main.MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = GameActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        app = application as space_survivor.main.MainApp

        korgeAndroidView = KorgeAndroidView(this)
        binding.toolContainer.addView(korgeAndroidView)

        loadToolModule()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()
        //unloadToolModule()
    }

    private fun loadToolModule() {
        korgeAndroidView.loadModule(CustomModule(app, width = 800, height = 1440, callback = {
            println("Callback from android app")
        }))
    }

    private fun unloadToolModule() {
        korgeAndroidView.unloadModule()
    }
}