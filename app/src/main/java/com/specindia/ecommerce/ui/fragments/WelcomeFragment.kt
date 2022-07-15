package com.specindia.ecommerce.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.specindia.ecommerce.R
import com.specindia.ecommerce.databinding.FragmentWelcomeBinding
import com.specindia.ecommerce.ui.activity.AuthActivity
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.viewmodel.FaceBookLoginViewModel
import com.specindia.ecommerce.util.getUserDetailsFromFB
import com.specindia.ecommerce.util.showLongToast
import com.specindia.ecommerce.util.showShortToast
import com.specindia.ecommerce.util.startNewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException


@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var fbLoginViewModel: FaceBookLoginViewModel
    private var accessTokenTracker: AccessTokenTracker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(layoutInflater)
        setSpannableText()
        fbLoginViewModel = ViewModelProvider(this)[FaceBookLoginViewModel::class.java]
        accessTrackerToken()
        return binding.root
    }


    private fun setSpannableText() {
        val spanText = SpannableStringBuilder(getString(R.string.already_have_an_account_login))
        val clickableString = object : ClickableSpan() {
            override fun onClick(view: View) {
                view.findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
            }
        }
        spanText.setSpan(
            clickableString,
            25,
            spanText.length,
            30
        )
        spanText.setSpan(
            ForegroundColorSpan(Color.RED),
            25, // start
            30, // end
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.apply {
            tvAlreadyHaveAnAccount.setText(spanText, TextView.BufferType.SPANNABLE)
            tvAlreadyHaveAnAccount.movementMethod = LinkMovementMethod.getInstance();
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpButtonClick(view)
    }

    private fun setUpButtonClick(view: View) {
        binding.apply {
            btnStartWithEmailOrPhone.setOnClickListener {
                view.findNavController()
                    .navigate(R.id.action_welcomeFragment_to_registrationFragment)
            }

            btnFacebook.setOnClickListener {
                requireActivity().showShortToast("Coming soon...")
                doFBLogin()
            }

            btnGooglePlus.setOnClickListener {
                requireActivity().showShortToast("Coming soon...")
            }
        }
    }


    private fun accessTrackerToken() {
        accessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken?,
                currentAccessToken: AccessToken?
            ) {
                if (currentAccessToken == null) {
                    // User is loggedOut
                    // Clear data
                    // User Logged Out
                } else {
                    getUserDetailsFromFB(currentAccessToken, (activity as AuthActivity))
                }
            }
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun doFBLogin() {
        activity?.let { fragmentActivity ->
            LoginManager.getInstance().logInWithReadPermissions(
                fragmentActivity,
                fbLoginViewModel.callbackManager,
                listOf("email", "public_profile")
            )
            fragmentActivity.finishFacebookLoginToThirdParty { loginResult ->
                val fbAccessToken = loginResult.accessToken
                val fbToken = fbAccessToken.token
                Log.d("FB", "Token $fbToken")
                Log.d("FB", "User Id ${fbAccessToken.userId}")
                delay(2000)
                goToHomeActivity()
                requireContext().showLongToast(fbToken)
            }
        }
    }

    private fun goToHomeActivity() {
        requireActivity().startNewActivity(HomeActivity::class.java)
        (activity as AuthActivity).dataStoreViewModel.saveUserLoggedIn(true)
        requireContext().showLongToast("FB Login successfully")
    }

    @ExperimentalCoroutinesApi
    suspend fun getFacebookToken(callbackManager: CallbackManager): LoginResult =
        suspendCancellableCoroutine { continuation ->
            LoginManager.getInstance()
                .registerCallback(callbackManager, object :
                    FacebookCallback<LoginResult> {

                    override fun onSuccess(result: LoginResult) {
                        Log.d("FB", "Success $result")
                        requireContext().showLongToast("Success")
                        continuation.resume(result) {}
                    }

                    override fun onCancel() {
                        Log.d("FB", "Cancel")
                        requireContext().showLongToast("Cancel")
                        // handling cancelled flow (probably don't need anything here)
                        continuation.cancel()
                    }

                    override fun onError(error: FacebookException) {
                        // Facebook authorization error
                        Log.e("FB", "Authorization Error $error")
                        requireContext().showLongToast("Authorization Error $error")
                        continuation.resumeWithException(error)
                    }
                })
        }

    @ExperimentalCoroutinesApi
    fun FragmentActivity.finishFacebookLoginToThirdParty(
        onCredential: suspend (LoginResult) -> Unit
    ) {
        this.lifecycleScope.launchWhenStarted {
            try {
                val loginResult: LoginResult = getFacebookToken(fbLoginViewModel.callbackManager)
                onCredential(loginResult)
            } catch (e: FacebookException) {
                Log.e("FB", e.toString())
                requireContext().showLongToast(e.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        accessTokenTracker?.stopTracking()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        loginViewModel.callbackManager.onActivityResult(requestCode, resultCode, data)
//        // onActivityResult() is deprecated, but Facebook hasn't added support
//        // for the new Result Contracts API yet.
//        // https://github.com/facebook/facebook-android-sdk/issues/875
//        super.onActivityResult(requestCode, resultCode, data)
//    }

}