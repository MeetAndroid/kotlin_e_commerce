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
import com.specindia.ecommerce.databinding.ActivityMainBinding
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
                    doOnEnd { splashScreenView.remove() }
                }

                slideBack.start()
            }
        }
    }
}