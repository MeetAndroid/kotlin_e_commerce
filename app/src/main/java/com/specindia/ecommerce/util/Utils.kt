package com.specindia.ecommerce.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.specindia.ecommerce.R
import com.specindia.ecommerce.ui.activity.AuthActivity
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.Constants.Companion.FIELD_FB_DATA
import com.specindia.ecommerce.util.Constants.Companion.FIELD_FB_EMAIL
import com.specindia.ecommerce.util.Constants.Companion.FIELD_FB_FIRST_NAME
import com.specindia.ecommerce.util.Constants.Companion.FIELD_FB_ID
import com.specindia.ecommerce.util.Constants.Companion.FIELD_FB_LAST_NAME
import com.specindia.ecommerce.util.Constants.Companion.FIELD_FB_PICTURE
import com.specindia.ecommerce.util.Constants.Companion.FIELD_FB_URL
import com.specindia.ecommerce.util.Constants.Companion.KEY_FIELDS
import com.specindia.ecommerce.util.Constants.Companion.VALUE_FIELDS
import kotlinx.coroutines.launch
import org.json.JSONException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.regex.Matcher
import java.util.regex.Pattern


fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.visible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun View.enable(enabled: Boolean) {
    isEnabled = enabled
    alpha = if (enabled) 1f else 0.5f
}

fun View.snackBar(message: String, action: (() -> Unit)? = null) {
    val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackBar.setAction("Retry") {
            it()
        }
    }
    snackBar.show()
}

fun Activity.snack(message: String) {
    Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        .show()
}

fun Fragment.logout() = lifecycleScope.launch {
    if (activity is HomeActivity) {
        (activity as HomeActivity).performLogout()
    }
}

val Context.isConnected: Boolean
    get() {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val nw = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
                when {
                    actNw.hasTransport(TRANSPORT_WIFI) -> true
                    actNw.hasTransport(TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
            else -> {
                // Use depreciated methods only on older devices
                val nwInfo = connectivityManager.activeNetworkInfo ?: return false
                nwInfo.isConnected
            }
        }
    }

fun showFullScreen(activity: Activity) {
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}

fun hideActionBar(activity: Activity) {
    if (activity.actionBar != null) {
        activity.actionBar!!.hide()
    }
}

fun Context.showShortToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.showLongToast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()

fun TextInputEditText.emptyEditText(editText: TextInputEditText) {

    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.startsWith(" ") == true) {
                editText.setText("")
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    })
}

fun showMaterialSnack(context: Context, view: View, message: String) {
    val snack = Snackbar.make(context, view, message, Toast.LENGTH_SHORT)
    val snackView = snack.view
    snackView.background = ContextCompat.getDrawable(context, R.drawable.bg_snackbar)
    snack.show()

}

// Get the FB Key Hash
fun printHashKey(context: Context) {
    try {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                val packageInfo: PackageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )

                for (signature in packageInfo.signingInfo.apkContentsSigners) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                }
            }
            else -> {
                val packageInfo: PackageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )

                for (signature in packageInfo.signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
                }
            }
        }

    } catch (e: PackageManager.NameNotFoundException) {
        Log.d("TAG", "printHashKey: PackageManager.NameNotFoundException -> $e")
    } catch (e: NoSuchAlgorithmException) {
        Log.d("TAG", "printHashKey: NoSuchAlgorithmException -> $e")
    }
}

// do Logout from Facebook
fun logoutFromFacebook() {
    LoginManager.getInstance().logOut()
}

// Get FB details from Graph Request
fun getUserDetailsFromFB(accessToken: AccessToken, activity: Activity) {
    val request =
        GraphRequest.newMeRequest(
            accessToken
        ) { obj, _ ->
            try {
                val id = obj?.optString(FIELD_FB_ID, "")
                val firstName = obj?.optString(FIELD_FB_FIRST_NAME, "")
                val lastName = obj?.optString(FIELD_FB_LAST_NAME, "")
                val email = obj?.optString(FIELD_FB_EMAIL, "")
                val imageUrl =
                    obj?.getJSONObject(FIELD_FB_PICTURE)?.getJSONObject(FIELD_FB_DATA)
                        ?.getString(FIELD_FB_URL)

                saveUserFBDetails(
                    activity,
                    accessToken.token,
                    id!!,
                    email!!,
                    firstName!!,
                    lastName!!,
                    imageUrl!!
                )

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    val bundle = Bundle()
    bundle.putString(KEY_FIELDS, VALUE_FIELDS)
    request.parameters = bundle
    request.executeAsync()
}

// Save FB details in data store
fun saveUserFBDetails(
    activity: Activity,
    token: String,
    id: String,
    email: String,
    firstName: String,
    lastName: String,
    imageUrl: String
) {
    with((activity as AuthActivity).dataStoreViewModel) {
        saveFBAccessToken(token)
        saveUserId(id)
        saveEmail(email)
        saveFirstName(firstName)
        saveLastName(lastName)
        saveProfileUrl(imageUrl)
    }
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String?): Boolean {
    val pattern: Pattern
    val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    pattern = Pattern.compile(passwordPattern)
    val matcher: Matcher = pattern.matcher(password)
    return matcher.matches()
}

// ============== Alert Dialogs

/**
 * Show a dialog from [AlertDialog.Builder] with a title and message
 *
 * Clicking on a button will dismiss the dialog.
 */
suspend inline fun AlertDialog.Builder.alert(
    buttonText: String = "Ok"
) = SuspendAlertDialog.alert(buttonText) {
    this
}




