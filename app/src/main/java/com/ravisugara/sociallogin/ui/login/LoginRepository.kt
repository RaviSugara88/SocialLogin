package com.ravisugara.sociallogin.ui.login

import android.app.Application
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import java.util.*

class LoginRepository(val app:Application) {

    fun faceBookLogin(){
        val callbackManager = CallbackManager.Factory.create()
//        LoginManager.getInstance()
//                .logInWithReadPermissions(app,
//                Arrays.asList("email","public_profile"))
     //Repository
    }

}