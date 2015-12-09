/**
  * Created by chentao on 15/12/9.
  *
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
package com.github.pockethub

import android.content.Context
import android.support.multidex.{MultiDex, MultiDexApplication}
import com.alorma.github.basesdk.client.credentials.{GithubDeveloperCredentials, MetaDeveloperCredentialsProvider}
import com.bugsnag.android.Bugsnag

class PocketHub extends MultiDexApplication {
  override def onCreate() {
    super.onCreate()
    GithubDeveloperCredentials.init(new MetaDeveloperCredentialsProvider(getApplicationContext))
    Bugsnag.init(this)
    Bugsnag.setNotifyReleaseStages("production")
  }

  override protected def attachBaseContext(base: Context) = {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }
}