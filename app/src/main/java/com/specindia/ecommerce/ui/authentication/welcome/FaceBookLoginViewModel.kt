package com.specindia.ecommerce.ui.authentication.welcome

import androidx.lifecycle.ViewModel
import com.facebook.CallbackManager

class FaceBookLoginViewModel : ViewModel() {
    var callbackManager: CallbackManager = CallbackManager.Factory.create()
}