package com.ravisugara.sociallogin.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ravisugara.sociallogin.R
import com.ravisugara.sociallogin.databinding.ActivityLoginBinding
import com.ravisugara.sociallogin.uital.Key
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    // for facebook login
    private lateinit var callbackManager: CallbackManager
    // for google login
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
       // bind view with data binding
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        //set binding with view model
        binding.lifecycleOwner = this
        binding.loginViewModel = viewModel
        facebookLogin()
        googleLogin()
    }

    private fun facebookLogin() {
        viewModel.startLoginFacebook().observe(this, androidx.lifecycle.Observer {
            if (it){
                callbackManager = CallbackManager.Factory.create()
                LoginManager.getInstance()
                        .logInWithReadPermissions(this, Arrays.asList(Key.EMAIL,Key.PUBLIC_PROFILE))
                LoginManager.getInstance()
                        .registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                            override fun onSuccess(loginResult: LoginResult?) {
                                viewModel.tokenTracker.startTracking()
                            }
                            override fun onCancel() {
                                //  toast("Cancel Facebook Login")
                            }
                            override fun onError(error: FacebookException) {
                                // toast(error.message.toString())
                            }
                        })

            }
        })

    }

    private fun googleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        viewModel.startLoginGoogle().observe(this, androidx.lifecycle.Observer {
            if (it){
                val signInIntent = mGoogleSignInClient!!.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        })
        viewModel.userSinOut().observe(this, androidx.lifecycle.Observer {
            if (it){
                signOut()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
           viewModel.handleSignInResult(task)
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }

    }
    private fun signOut() {
        mGoogleSignInClient!!.signOut()
                .addOnCompleteListener(this) {
                 //   Toast.makeText(this, "user signOut", Toast.LENGTH_SHORT).show()
                }
    }

    companion object {
        private const val RC_SIGN_IN = 1
    }


}