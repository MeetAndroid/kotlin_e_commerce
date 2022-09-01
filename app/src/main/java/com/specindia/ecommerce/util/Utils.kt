package com.specindia.ecommerce.util

import android.R.color.transparent
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.util.Predicate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.facebook.login.LoginManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils.attachBadgeDrawable
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.specindia.ecommerce.R
import com.specindia.ecommerce.models.response.cart.getcart.GetCartResponse
import com.specindia.ecommerce.ui.activity.AuthActivity
import com.specindia.ecommerce.ui.activity.HomeActivity
import com.specindia.ecommerce.util.Constants.Companion.APPLICATION_JSON
import com.specindia.ecommerce.util.Constants.Companion.AUTHORIZATION
import com.specindia.ecommerce.util.Constants.Companion.BEARER
import com.specindia.ecommerce.util.Constants.Companion.CONTENT_TYPE
import com.specindia.ecommerce.util.dialogs.CustomProgressDialog
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
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


// Save FB details in data store
fun saveLoggedInUserData(
    activity: Activity,
    token: String,
    id: String,
    email: String,
    firstName: String,
    lastName: String,
    imageUrl: String,
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

// Add Request Headers here
fun getHeaderMap(token: String, hasBearerToken: Boolean): Map<String, String> {
    val headerMap = mutableMapOf<String, String>()
    headerMap[CONTENT_TYPE] = APPLICATION_JSON
    if (hasBearerToken) headerMap[AUTHORIZATION] = "$BEARER $token"
    Log.d("headerMap", headerMap.toString())
    return headerMap
}

fun View.setRandomBackgroundColor() {
    val rnd = Random()
    val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    this.setBackgroundColor(color)
}

fun <T> removeElementByMatchingCriteria(list: MutableList<T>, predicate: Predicate<T>) {
    list.filter { predicate.test(it) }.forEach { list.remove(it) }
}

// ============== Alert Dialogs

/**
 * Show a dialog from [AlertDialog.Builder] with a title and message
 *
 * Clicking on a button will dismiss the dialog.
 */
suspend inline fun AlertDialog.Builder.alert(
    buttonText: String = "Ok",
) = SuspendAlertDialog.alert(buttonText) {
    this
}

inline fun Activity.showProgressDialog(func: CustomProgressDialog.() -> Unit): AlertDialog =
    CustomProgressDialog(this).apply {
        func()
    }.create()

inline fun Fragment.showProgressDialog(func: CustomProgressDialog.() -> Unit): AlertDialog =
    CustomProgressDialog(requireContext()).apply {
        func()
    }.create()

fun String.toIds() = trim().splitToSequence(',').filter { it.isNotEmpty() }.toList()

fun String.toWords(): List<String> {
    val words = ArrayList<String>()
    for (w in this.trim(' ').split(",")) {
        if (w.isNotEmpty()) {
            words.add(w)
        }
    }
    return words
}

fun dateFormat(date1: String, inputString: String, outPutString: String): String {

    val srcDf: DateFormat = SimpleDateFormat(inputString, Locale.US)
    val destDf: DateFormat = SimpleDateFormat(outPutString, Locale.US)
    var outPutDate = ""
    try {
        val date = srcDf.parse(date1)
        outPutDate = date?.let { destDf.format(it) }.toString()
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return outPutDate

}

@ExperimentalBadgeUtils
fun handleCartBadgeCount(
    response: GetCartResponse,
    activity: HomeActivity,
    frameLayout: FrameLayout,
) {
    if (response.data.isNotEmpty()) {
        // Save Latest Cart Counter Value to DataStore
        val restaurantIdInCart =
            response.data.first().product.restaurantId
        activity.dataStoreViewModel.saveExistingRestaurantIdOfCart(
            restaurantIdInCart
        )

        activity.dataStoreViewModel.saveCartItemCount(
            response.data.size
        )

    } else {
        activity.dataStoreViewModel.saveExistingRestaurantIdOfCart(
            0
        )
        activity.dataStoreViewModel.saveCartItemCount(
            0
        )

    }
    updateCartCountOnUI(activity, frameLayout)
}

// Updating Cart Count by getting latest Cart Counter from DataStore
@ExperimentalBadgeUtils
fun updateCartCountOnUI(activity: HomeActivity, frameLayout: FrameLayout) {
    Handler(Looper.getMainLooper()).postDelayed({
        val cartItemCount =
            activity.dataStoreViewModel.getCartItemCount()
        cartItemCount?.let {
            setCartBadgeCount(
                activity,
                it,
                frameLayout
            )
        }
    }, 500)
}

@ExperimentalBadgeUtils
fun setCartBadgeCount(activity: Activity, counter: Int, frameLayout: FrameLayout) {
    val badgeDrawable = BadgeDrawable.create(activity).apply {
        number = counter
        verticalOffset = 4
        horizontalOffset = 4
        maxCharacterCount = 999
        badgeGravity = BadgeDrawable.TOP_END

        if (counter != 0) {
            badgeTextColor = ContextCompat.getColor(activity, R.color.white)
            backgroundColor = ContextCompat.getColor(activity, R.color.color_red)
        } else {
            number = 0
            badgeTextColor = ContextCompat.getColor(activity, transparent)
            backgroundColor = ContextCompat.getColor(activity, transparent)
        }

    }
    frameLayout.foreground = badgeDrawable
    frameLayout.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
        attachBadgeDrawable(
            badgeDrawable,
            view,
            frameLayout
        )
    }
}

// This will return the instance of the current fragment of particular activity
fun getCurrentFragmentInstance(activity: HomeActivity): Fragment? {
    val navHostFragment =
        activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment?
    val fragmentManager: FragmentManager = navHostFragment!!.childFragmentManager
    return fragmentManager.primaryNavigationFragment
}

// This will make Edit Text read only or editable
fun AppCompatEditText.setReadOnly(value: Boolean, inputType: Int = InputType.TYPE_NULL) {
    isFocusable = !value
    isFocusableInTouchMode = !value
    requestFocusFromTouch()
    this.inputType = inputType
}

fun View.showKeyboard() {
    this.requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


