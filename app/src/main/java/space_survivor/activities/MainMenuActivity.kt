package space_survivor.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import space_survivor.databinding.ActivityMainMenuBinding
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.squareup.picasso.Picasso
import space_survivor.R
import space_survivor.main.MainApp
import timber.log.Timber.i


class MainMenuActivity : AppCompatActivity() {

    lateinit var toolbar : androidx.appcompat.widget.Toolbar
    lateinit var navController: NavController
    private lateinit var binding: ActivityMainMenuBinding
    lateinit var app : MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = application as MainApp
        app.orientation = resources.configuration.orientation
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navController = navHostFragment!!.findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.gameFragment,
                R.id.statisticsFragment,
                R.id.scoreboardFragment,
                R.id.loginFragment
            ),
            binding.drawerLayout
        )

        toolbar = binding.toolbar
        toolbar.setupWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            val previousDestination = navController.previousBackStackEntry?.destination

            if(previousDestination?.id != null){
                when (destination.id) {
                    R.id.mainMenuFragment, R.id.gameFragment -> {
                        if ((previousDestination.id != R.id.mainMenuFragment) &&
                            (previousDestination.id != R.id.gameFragment)
                        ) {
                            animateToolbarUp()
                        }
                    }
                    else -> {
                        if ((previousDestination.id == R.id.mainMenuFragment) ||
                            (previousDestination.id == R.id.gameFragment)
                        ) {
                            animateToolbarDown()
                        }
                    }
                }
                updateNavDrawer()
            }
            else{
                toolbar.visibility = View.GONE  // Hide toolbar on first load
            }

        }
    }

    private fun updateNavDrawer(){
        val menu = binding.navView.menu

        val app = application as MainApp
        val loggedIn = app.account != null

        // Update header
        if(loggedIn){
            // get textView from nav_header_main.xml
            val headerView = binding.navView.getHeaderView(0)
            val accName = headerView.findViewById<TextView>(R.id.textView)
            val accImage = headerView.findViewById<ImageView>(R.id.imageView)
            accName.text = app.account?.displayName
            Picasso.with(this).load(app.account?.photoUrl).into(accImage)

        }

        // Dynamically update menu items
        menu.clear()
        for (destination in navController.graph) {
            if(destination.id != navController.currentDestination?.id) {

                if(destination.id == R.id.loginFragment && loggedIn){
                    continue
                }

                val item = menu.add(destination.label)
                item.setOnMenuItemClickListener {
                    navController.navigate(destination.id)
                    binding.drawerLayout.close()
                    true
                }
            }
        }

    }

    private fun animateToolbarUp(){
        val toolbar = toolbar

        toolbar.visibility = View.VISIBLE
        toolbar.animate().setDuration(500).translationY(-toolbar.height.toFloat()).setListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    toolbar.visibility = View.GONE
                }
            })
    }

    private fun animateToolbarDown() {
        val toolbar = toolbar

        toolbar.visibility = View.GONE
        toolbar.animate().setDuration(500).translationY(0f).setListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    toolbar.visibility = View.VISIBLE
                }
            })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            i("Landscape")
            app.orientation = Configuration.ORIENTATION_LANDSCAPE
        }
        else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            i("Portrait")
            app.orientation = Configuration.ORIENTATION_PORTRAIT
        }
    }


}







