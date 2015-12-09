package com.github.pockethub.accounts

import java.util

import android.accounts.{Account, AccountManager}
import android.app.{Activity, AlertDialog}
import android.content.{ContentResolver, Context, Intent}
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.{Menu, MenuItem}
import android.widget.Toast
import com.alorma.github.basesdk.client.credentials.GithubDeveloperCredentials
import com.alorma.github.basesdk.client.{BaseClient, GithubDeveloperCredentialsProvider}
import com.alorma.github.sdk.bean.dto.response.{Organization, Token, User}
import com.alorma.github.sdk.login.AccountsHelper
import com.alorma.github.sdk.services.login.RequestTokenClient
import com.alorma.github.sdk.services.user.GetAuthUserClient
import com.github.pockethub.R
import com.github.pockethub.accounts.AccountConstants._
import com.github.pockethub.persistence.AccountDataManager
import com.github.pockethub.ui.roboactivities.RoboAccountAuthenticatorAppCompatActivity
import com.github.pockethub.ui.{LightProgressDialog, MainActivity}
import com.google.inject.Inject
import com.squareup.okhttp.HttpUrl
import retrofit.RetrofitError
import retrofit.client.Response

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
object LoginActivity {
  private val TAG               = "LoginActivity"
  /**
    * Sync period in seconds, currently every 8 hours
    */
  private val SYNC_PERIOD: Long = 8L * 60L * 60L
  /**
    * Auth token type parameter
    */
  val PARAM_AUTHTOKEN_TYPE  = "authtokenType"
  /**
    * Initial user name
    */
  val PARAM_USERNAME        = "username"
  val OAUTH_HOST            = "www.github.com"
  val INTENT_EXTRA_URL      = "url"

  def configureSyncFor(account: Account) {
    Log.d(TAG, "Configuring account sync")
    ContentResolver.setIsSyncable(account, PROVIDER_AUTHORITY, 1)
    ContentResolver.setSyncAutomatically(account, PROVIDER_AUTHORITY, true)
    ContentResolver.addPeriodicSync(account, PROVIDER_AUTHORITY, new Bundle, SYNC_PERIOD)
  }
}

class LoginActivity extends RoboAccountAuthenticatorAppCompatActivity with BaseClient.OnResultCallback[User] {
  outer =>
  import LoginActivity._

  val WEBVIEW_REQUEST_CODE = 0

  private var scope: String                           = null
  private var accessToken: String                     = null
  private var accountManager: AccountManager          = null
  private var accounts: Array[Account]                = null
  private var progressDialog: AlertDialog             = null
  private var requestTokenClient: RequestTokenClient  = null

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.login)

    val toolbar = findViewById(R.id.toolbar).asInstanceOf[Toolbar]
    setSupportActionBar(toolbar)

    accountManager = AccountManager.get(this)
    accounts = accountManager.getAccountsByType(getString(R.string.account_type))

    if (accounts != null && accounts.length > 0) {
      openMain()
    }
    checkOauthConfig()
  }

  protected override def onNewIntent(intent: Intent): Unit = {
    super.onNewIntent(intent)
    val uri = intent.getData
    onUserLoggedIn(uri)
  }

  private def onUserLoggedIn(uri: Uri) {
    if (uri != null && (uri.getScheme == getString(R.string.github_oauth_scheme))) {
      openLoadingDialog()
      val code: String = uri.getQueryParameter("code")
      if (requestTokenClient == null) {
        requestTokenClient = new RequestTokenClient(outer, code)
        requestTokenClient.setOnResultCallback(new BaseClient.OnResultCallback[Token]() {
          def onResponseOk(token: Token, r: Response) {
            if (token.access_token != null) {
              endAuth(token.access_token, token.scope)
            }
            else if (token.error != null) {
              Toast.makeText(outer, token.error, Toast.LENGTH_LONG).show()
              progressDialog.dismiss()
            }
          }

          def onFail(error: RetrofitError) {
            error.printStackTrace()
          }
        })
        requestTokenClient.execute()
      }
    }
  }

  private def openLoadingDialog() {
    progressDialog = LightProgressDialog.create(this, R.string.login_activity_authenticating)
    progressDialog.show()
  }

  private def endAuth(accessToken: String, scope: String) {
    this.accessToken = accessToken
    this.scope = scope
    progressDialog.setMessage(getString(R.string.loading_user))
    val userClient: GetAuthUserClient = new GetAuthUserClient(this, accessToken)
    userClient.setOnResultCallback(this)
    userClient.execute()
  }

  override def onFail(retrofitError: RetrofitError): Unit = retrofitError.printStackTrace()

  override def onResponseOk(user: User, response: Response): Unit = {
    val account = new Account(user.login, getString(R.string.account_type))
    val userData = AccountsHelper.buildBundle(user.name, user.email, user.avatar_url, scope)
    userData.putString(AccountManager.KEY_AUTHTOKEN, accessToken)

    accountManager.addAccountExplicitly(account, null, userData)
    accountManager.setAuthToken(account, getString(R.string.account_type), accessToken)

    val result = new Bundle()
    result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
    result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.`type`)
    result.putString(AccountManager.KEY_AUTHTOKEN, accessToken)

    setAccountAuthenticatorResult(result)

    openMain()
 }

  protected override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == WEBVIEW_REQUEST_CODE && resultCode == Activity.RESULT_OK)
      onUserLoggedIn(data.getData)
  }

  private def openMain() {
    if (progressDialog != null) {
      progressDialog.dismiss()
    }
    val intent: Intent = new Intent(this, classOf[MainActivity])
    startActivity(intent)
    finish()
  }

  private def checkOauthConfig() {
    if ((getString(R.string.github_client) == "dummy_client") || (getString(R.string.github_secret) == "dummy_secret"))
      Toast.makeText(this, R.string.error_oauth_not_configured, Toast.LENGTH_LONG).show()
  }

  override def onCreateOptionsMenu(optionMenu: Menu): Boolean = {
    getMenuInflater.inflate(R.menu.login, optionMenu)
    true
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.m_login =>
        handleLogin()
        true
      case _ =>
        super.onOptionsItemSelected(item)
    }
  }

  def handleLogin() {
    openLoginInBrowser(GithubDeveloperCredentials.getInstance.getProvider)
  }

  private def openLoginInBrowser(client: GithubDeveloperCredentialsProvider) {
    val initialScope = "user,public_repo,repo,delete_repo,notifications,gist"
    val url = new HttpUrl.Builder().scheme("https").host(OAUTH_HOST).addPathSegment("login").addPathSegment("oauth").addPathSegment("authorize").addQueryParameter("client_id", client.getApiClient).addQueryParameter("scope", initialScope)
    val intent = new Intent(this, classOf[LoginWebViewActivity])
    intent.putExtra(INTENT_EXTRA_URL, url.toString)
    startActivityForResult(intent, WEBVIEW_REQUEST_CODE)
  }

  class AccountLoader(context: Context) extends AuthenticatedUserTask[util.List[Organization]](context) {
    @Inject private var cache: AccountDataManager = null

    protected override def run(account: Account): util.List[Organization] = {
      cache.getOrgs(true)
    }
  }
}