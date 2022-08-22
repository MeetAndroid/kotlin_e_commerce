package com.specindia.ecommerce.ui.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.specindia.ecommerce.R
import com.specindia.ecommerce.api.network.NetworkResult
import com.specindia.ecommerce.databinding.FragmentWelcomeBinding
import com.specindia.ecommerce.models.request.Parameters
import com.specindia.ecommerce.ui.activity.AuthActivity
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.ui.viewmodel.FaceBookLoginViewModel
import com.specindia.ecommerce.util.*
import com.specindia.ecommerce.util.Constants.Companion.SOCIAL_TYPE_FB
import com.specindia.ecommerce.util.Constants.Companion.SOCIAL_TYPE_GOOGLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONException
import kotlin.coroutines.resumeWithException


@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var fbLoginViewModel: FaceBookLoginViewModel
    private lateinit var customProgressDialog: AlertDialog

    var googleSignInClient: GoogleSignInClient? = null

    private var fbToken: String = ""
    private var fbUserId: String = ""
    private var fbUserFirstName: String = ""
    private var fbUserLastName: String = ""
    private var fbUserEmail: String = ""
    private var fbUserProfileUrl: String = ""
    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWelcomeBinding.inflate(layoutInflater)
        setSpannableText()
        fbLoginViewModel = ViewModelProvider(this)[FaceBookLoginViewModel::class.java]
        return binding.root
    }

    private fun setSpannableText() {
        val spanText = SpannableStringBuilder(getString(R.string.already_have_an_account_login))
        val clickableString = object : ClickableSpan() {
            override fun onClick(view: View) {
                view.findNavController()
                    .navigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
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
        setUpProgressDialog()
        observeFBResponse()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    handleSignInResult(task)
                }
            }
    }

    private fun setUpProgressDialog() {
        customProgressDialog = showProgressDialog {
            cancelable = false
            isBackGroundTransparent = true
        }
    }

    private fun setUpButtonClick(view: View) {
        binding.apply {
            btnStartWithEmailOrPhone.setOnClickListener {
                view.findNavController()
                    .navigate(WelcomeFragmentDirections.actionWelcomeFragmentToRegistrationFragment())
            }

            btnFacebook.setOnClickListener {
                doFBLogin()
            }

            btnGooglePlus.setOnClickListener {
                signIn()
            }
        }
    }

    private fun observeFBResponse() {
        (activity as AuthActivity).authViewModel.socialResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Success -> {
                    customProgressDialog.hide()

                    //Save User response as a Json In Data Store
                    val data = Gson().toJson(response.data?.data)
                    (activity as AuthActivity).dataStoreViewModel.saveLoggedInUserData(data)

                    saveLoggedInUserData(
                        (activity as AuthActivity),
                        fbToken,
                        fbUserId,
                        fbUserEmail,
                        fbUserFirstName,
                        fbUserLastName,
                        fbUserProfileUrl
                    )
                    requireActivity().showShortToast("FB Login Successfully ...")
                    goToHomeActivity()

                }
                is NetworkResult.Error -> {
                    customProgressDialog.hide()
                    showDialog(response.message.toString())
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }


    private fun showDialog(message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.app_name))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
            }
            .show()
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
                fbToken = loginResult.accessToken.token
                requireContext().showLongToast(fbToken)
            }
        }
    }

    private fun callSocialLoginApi(fbToken: String) {
        customProgressDialog.show()
        val parameter = Parameters(
            firstName = fbUserFirstName,
            lastName = fbUserLastName,
            socialId = fbToken,
            socialType = SOCIAL_TYPE_FB

        )
        (activity as AuthActivity).authViewModel.doSocialLogin(
            getHeaderMap("", false),
            Gson().toJson(parameter)
        )

    }

    private fun goToHomeActivity() {
        (activity as AuthActivity).dataStoreViewModel.saveUserLoggedIn(true)
        requireActivity().startNewActivity(HomeActivity::class.java)
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
                        getUserDetailsFromFB(result.accessToken)
                        requireContext().showLongToast("FB Token got successfully....")
                        continuation.resume(result) {
                        }
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
        onCredential: suspend (LoginResult) -> Unit,
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

    // Get FB details from Graph Request
    fun getUserDetailsFromFB(accessToken: AccessToken) {
        val request =
            GraphRequest.newMeRequest(
                accessToken
            ) { obj, _ ->
                try {
                    fbUserId = obj?.optString(Constants.FIELD_FB_ID, "").toString()
                    fbUserFirstName = obj?.optString(Constants.FIELD_FB_FIRST_NAME, "").toString()
                    fbUserLastName = obj?.optString(Constants.FIELD_FB_LAST_NAME, "").toString()

                    fbUserEmail = obj?.optString(Constants.FIELD_FB_EMAIL, "").toString()
                    fbUserProfileUrl =
                        obj?.getJSONObject(Constants.FIELD_FB_PICTURE)
                            ?.getJSONObject(Constants.FIELD_FB_DATA)
                            ?.getString(Constants.FIELD_FB_URL).toString()

                    Log.d(
                        "GRAPH", """UserID = $fbUserId
                        |First Name = $fbUserFirstName
                        |Last Name = $fbUserLastName
                        |Email Id = $fbUserEmail
                        |Profile URL = $fbUserProfileUrl
                    """.trimMargin()
                    )
                    callSocialLoginApi(fbToken)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

        val bundle = Bundle()
        bundle.putString(Constants.KEY_FIELDS, Constants.VALUE_FIELDS)
        request.parameters = bundle
        request.executeAsync()
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
//            customProgressDialog.show()
            val account = completedTask.getResult(ApiException::class.java)

            fbUserFirstName = account.givenName.toString()
            fbUserLastName = account.familyName.toString()
            fbToken = account.id.toString()
            fbUserProfileUrl = account.photoUrl.toString()
            fbUserEmail = account.email.toString()
            fbUserId = account.id.toString()
            callSocialLoginGoogleApi()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient?.signInIntent!!
        resultLauncher?.launch(signInIntent)
    }

    private fun callSocialLoginGoogleApi() {
        customProgressDialog.show()
        val parameter = Parameters(
            firstName = fbUserFirstName,
            lastName = fbUserFirstName,
            socialId = fbToken,
            socialType = SOCIAL_TYPE_GOOGLE

        )
        (activity as AuthActivity).authViewModel.doSocialLogin(
            getHeaderMap("", false),
            Gson().toJson(parameter)
        )

    }
}