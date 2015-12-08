package com.github.pockethub.accounts

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebViewClient
import com.github.pockethub.R
import com.github.pockethub.ui.{LightProgressDialog, WebView}

/**
  * @author chentaov5@gmail.com
  *
  *                            ___====-_  _-====___
  *                      _--^^^#####//      \\#####^^^--_
  *                   _-^##########// (    ) \\##########^-_
  *                  -############//  |\^^/|  \\############-
  *                _/############//   (@::@)   \\############\_
  *               /#############((     \\//     ))#############\
  *              -###############\\    (oo)    //###############-
  *             -#################\\  / VV \  //#################-
  *            -###################\\/      \//###################-
  *           _#/|##########/\######(   /\   )######/\##########|\#_
  *           |/ |#/\#/\#/\/  \#/\##\  |  |  /##/\#/  \/\#/\#/\#| \|
  *           `  |/  V  V  `   V  \#\| |  | |/#/  V   '  V  V  \|  '
  *              `   `  `      `   / | |  | | \   '      '  '   '
  *                               (  | |  | |  )
  *                              __\ | |  | | /__
  *                             (vvv(VVV)(VVV)vvv)
  *
  *                              HERE BE DRAGONS
  *
  */
class LoginWebViewActivity extends AppCompatActivity { outer =>
  protected override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val webView = new WebView(this)
    webView.loadUrl(getIntent.getStringExtra(LoginActivity.INTENT_EXTRA_URL))
    webView.setWebViewClient(new WebViewClient() {
      val dialog = LightProgressDialog.create(outer, R.string.loading)

      override def onPageStarted(view: android.webkit.WebView, url: String, favicon: Bitmap): Unit = {
        dialog.show()
      }

      override def onPageFinished(view: android.webkit.WebView, url: String): Unit = {
        dialog.dismiss()
      }

      override def shouldOverrideUrlLoading(view: android.webkit.WebView, url: String): Boolean = {
        val uri = Uri.parse(url)
        if (uri.getScheme.equals(getString(R.string.github_oauth_scheme))) {
          val data = new Intent()
          data.setData(uri)
          setResult(Activity.RESULT_OK, data)
          finish()
          true
        } else {
          super.shouldOverrideUrlLoading(view, url)
        }
      }
    })

    setContentView(webView)
  }
}