package com.github.pockethub

import java.net.HttpURLConnection

import org.eclipse.egit.github.core.client.GitHubClient

/**
  * Created by chentao on 15/12/10.
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
  */
class DefaultClient() extends GitHubClient() {
  private val USER_AGENT = "GitHubAndroid/1.6"

  setSerializeNulls(false)
  setUserAgent(USER_AGENT)

  override def configureRequest(request: HttpURLConnection): HttpURLConnection = {
    super.configureRequest(request)

    request.setRequestProperty(GitHubClient.HEADER_ACCEPT, "application/vnd.github.beta.full+json")
    request
  }
}
