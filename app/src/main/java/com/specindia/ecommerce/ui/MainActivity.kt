package com.specindia.ecommerce.ui

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    var contentHasLoaded = false
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startLoadingContent()
        setupSplashScreen(splashScreen)
        loadNewsFragment()
    }

    private fun loadNewsFragment() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.news_container_view) as NavHostFragment
        val navController = navHostFragment.navController

    }

    private fun startLoadingContent() {
        // For this example, the Timer delay represents awaiting a response from a network call
        Timer().schedule(3000) {
            contentHasLoaded = true
        }
    }

    private fun setupSplashScreen(splashScreen: SplashScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val content: View = findViewById(android.R.id.content)
            content.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        return if (contentHasLoaded) {
                            content.viewTreeObserver.removeOnPreDrawListener(this)
                            true
                        } else false
                    }
                }
            )

            splashScreen.setOnExitAnimationListener { splashScreenView ->
                val slideBack = ObjectAnimator.ofFloat(
                    splashScreenView.view,
                    View.TRANSLATION_X,
                    0f,
                    -splashScreenView.view.width.toFloat()
                ).apply {
                    interpolator = DecelerateInterpolator()
                    duration = 800L
                    doOnEnd {
                        splashScreenView.remove()
                    }
                }
                slideBack.start()
            }
        }


    }
}