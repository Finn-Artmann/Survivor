package space_survivor.activities

import android.os.Bundle
import android.util.DisplayMetrics
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

        // get screen height and width according to api level 32, do not use defaultDisplay since it is deprecated

        val displayMetrics = DisplayMetrics()
        windowManager.currentWindowMetrics.bounds.let {
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