package space_survivor.view_models

import android.app.Application
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.util.DisplayMetrics
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.soywiz.korge.Korge
import com.soywiz.korge.android.KorgeAndroidView
import space_survivor.game_data.util.CustomModule
import space_survivor.main.MainApp
import timber.log.Timber.i


class GameViewModel(var app: Application, displayMetrics: DisplayMetrics) : ViewModel() {

    lateinit var customModule: CustomModule
    lateinit var displayMetrics: DisplayMetrics

    init {
        customModule = CustomModule(
            app as MainApp,
            width = displayMetrics.widthPixels,
            height = displayMetrics.heightPixels,
            callback = {
                println("Callback from android app")
            })
    }

    fun loadToolModule(korgeAndroidView: KorgeAndroidView) {
        try {
            var width = displayMetrics.widthPixels
            var height = displayMetrics.heightPixels

            customModule = CustomModule(
                app as MainApp,
                width = width,
                height = height,
                callback = {
                    i("Callback from android app")
                }
            )
            korgeAndroidView.loadModule(customModule)
        }
        catch (e: Exception) {
            i("Error: ${e.message}")
        }

    }

    fun unloadToolModule(korgeAndroidView: KorgeAndroidView) {
        korgeAndroidView.unloadModule()
    }
}

class GameViewModelFactory(private val app: Application, private val displayMetrics: DisplayMetrics) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(app, displayMetrics) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}