package com.ravisugara.sociallogin.ui.login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.ravisugara.sociallogin.R
import com.ravisugara.sociallogin.databinding.ActivityLoginBinding
import com.ravisugara.sociallogin.uital.Key
import com.ravisugara.sociallogin.uital.xtnCreateShotDynamicLink
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
        receiveDynamicLink()

        binding.shareLink.setOnClickListener {
            this.xtnCreateShotDynamicLink()
        }
    }

    private fun receiveDynamicLink() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.e("TAG", "handleViews: deepLink $deepLink", )
                    //https://www.ravitech.in/?email=rss@gmail+&+user_name=Ravi+Sugara
                    val email = deepLink.toString().split("=")[1].split("+")[0]
                    val userNamene = deepLink.toString().split("user_name=")[1]
                    if (!email.isNullOrEmpty()){
                        viewModel.viewLayDetail.postValue(true)
                        viewModel.name.postValue(userNamene)
                        viewModel.email.postValue(email)
                    }

                    Log.e("TAG", "handleViews: deepLink $email $userNamene", )
                  //  viewModel._email.postValue(email)
                 //   viewModel._name.postValue(userNamene)
//                    if (!id.isEmpty()){
//                      //  vm.getRunningDetail(id)
//                    }
                }
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }

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