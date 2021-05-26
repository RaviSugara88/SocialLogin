package com.ravisugara.sociallogin.ui.login

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.ravisugara.sociallogin.uital.Key
import org.json.JSONException
import org.json.JSONObject

class LoginViewModel(val app:Application) :AndroidViewModel(app)  {
    private val startLoginStatusFacebook = MutableLiveData<Boolean>()
    private val startLoginStatusGoogle = MutableLiveData<Boolean>()
    private val startGoogleLogout = MutableLiveData<Boolean>()
    private val _name = MutableLiveData("")
    private val _email = MutableLiveData("")
    private val _viewLayDetail = MutableLiveData(false)
    val name: LiveData<String> = _name
    val email: LiveData<String> = _email
    val viewLayDetail:MutableLiveData<Boolean> = MutableLiveData()

    fun startLoginFacebook():MutableLiveData<Boolean>{
        return startLoginStatusFacebook
    }
    fun facebookLogin(){
        startLoginStatusFacebook.value = true        //loginActivity.faceBookLogin()
    }
    fun googleLogin(){
        startLoginStatusGoogle.value = true
    }
    fun logOut(){
        startGoogleLogout.value = true
        LoginManager.getInstance().logOut()
        viewLayDetail.value =false
    }
    fun userSinOut():MutableLiveData<Boolean>{
        return startGoogleLogout
    }
    fun startLoginGoogle():MutableLiveData<Boolean>{
        return startLoginStatusGoogle
    }
    var tokenTracker: AccessTokenTracker = object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(
            oldAccessToke: AccessToken?,
            currentAccessToken: AccessToken?
        ) {
            if (currentAccessToken == null) {
                // Toast.makeText(this@LoginActivity, "log out", Toast.LENGTH_SHORT).show()
            } else {
                loadUser(currentAccessToken)
            }
        }
    }
    private fun loadUser(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            accessToken
        ) { `object`: JSONObject, response: GraphResponse? ->
            try {
                viewLayDetail.value =true
                _name.value = "${`object`.getString(Key.FIRST_NAME)} ${`object`.getString(Key.LAST_NAME)}"
                _email.value = `object`.getString(Key.EMAIL)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parame = Bundle()
        parame.putString(Key.FIELDS, "${Key.FIRST_NAME},${Key.LAST_NAME},${Key.EMAIL},${Key.ID}")
        request.parameters = parame
        request.executeAsync()
    }
     fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.statusCode)
            //  updateUI(null);
        }
    }
    private fun updateUI(account: GoogleSignInAccount?) {
        try {
            viewLayDetail.value =true
            _name.value = account?.displayName
            _email.value = account?.email
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}