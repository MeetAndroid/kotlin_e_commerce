package com.specindia.ecommerce.ui.activity

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.specindia.ecommerce.databinding.ActivityLauncherBinding
import com.specindia.ecommerce.util.startNewActivity

class LauncherActivity : AppCompatActivity() {
    private val TAG = "Main Activity"

    //    private val networkMonitor = NetworkMonitorUtil(this)
    var contentHasLoaded = false
    private lateinit var binding: ActivityLauncherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
//        checkNetworkConnectivity()
        setContentView(binding.root)
        startLoadingContent()
//        setupSplashScreen(splashScreen)
    }

    private fun startLoadingContent() {
        // For this example, the Timer delay represents awaiting a response from a network call
        val activity = OnBoardingActivity::class.java
        startNewActivity(activity)
    }

    private fun setupSplashScreen(splashScreen: SplashScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val content: View = findViewById(android.R.id.content)
            content.viewTreeObserver.addOnPreDrawListener(
                object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        return if (contentHasLoaded) {
                            Log.d(TAG, "onPreDraw called...")
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
                        Log.d(TAG, "Exit Animation End called...")
                        splashScreenView.remove()

                    }
                }
                slideBack.start()
            }
        }
    }

    // -------------- Checking Network State
//    private fun checkNetworkConnectivity() {
//        networkMonitor.result = { isAvailable, type ->
//            CoroutineScope(Dispatchers.Main).launch {
//                when (isAvailable) {
//                    true -> {
//                        when (type) {
//                            ConnectionType.Wifi -> {
//                                //Toast.makeText(applicationContext,getString(R.string.message_wifi_connected),Toast.LENGTH_SHORT).show()
//                            }
//                            ConnectionType.Cellular -> {
//                                //Toast.makeText(applicationContext,getString(R.string.message_cellular_connected),Toast.LENGTH_SHORT).show()
//                            }
//                            else -> {}
//                        }
//                    }
//                    false -> {
//                        //Toast.makeText(applicationContext,getString(R.string.message_no_internet_connection),Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
//    }

    // Register Network Monitor
//    override fun onResume() {
//        super.onResume()
//        networkMonitor.register()
//    }

    // Unregister Network Monitor
//    override fun onStop() {
//        super.onStop()
//        networkMonitor.unregister()
//    }
}