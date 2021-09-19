package com.ravisugara.sociallogin.uital

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase

fun Context.xtnCreateShotDynamicLink(){
    val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
        link = Uri.parse("https://www.ravitech.in/?email=rss@gmail & user_name=RaviSugara")
        domainUriPrefix = "https://ravisugara.page.link"
        // Set parameters
        socialMetaTagParameters {
            title = "Example of a Dynamic Link"
            description = "This link works whether the app is installed or not!"
            imageUrl = Uri.parse("https://www.blueappsoftware.com/logo-1.png")
        }
        androidParameters("com.ravisugara.sociallogin") {
            minimumVersion = 20
        }
        iosParameters("com.example.ios") {
            appStoreId = "123456789"
            minimumVersion = "1.0.1"
        }
        googleAnalyticsParameters {
            source = "orkut"
            medium = "social"
            campaign = "example-promo"
        }
        itunesConnectAnalyticsParameters {
            providerToken = "123456"
            campaignToken = "example-promo"
        }
        // ...
    }.addOnSuccessListener { (shortLink, flowchartLink) ->
        // You'll need to import com.google.firebase.dynamiclinks.ktx.component1 and
        // com.google.firebase.dynamiclinks.ktx.component2

        // Short link created
        // processShortLink(shortLink, flowchartLink)
       this.shareLink(shortLink!!)
    }.addOnFailureListener {
        // Error
        // ...
    }


}

private fun Context.shareLink(shortLink: Uri) {
    Log.e("main ", "short link " + shortLink.toString())
    // share app dialog
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
    intent.type = "text/plain"
    this.startActivity(intent)
}

