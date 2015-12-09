package com.github.pockethub.api

import com.github.pockethub.model.Authorization
import org.eclipse.egit.github.core.client.GitHubClient
import retrofit.RestAdapter
import retrofit.RestAdapter.Builder
import retrofit.android.AndroidLog
import retrofit.client.Response
import retrofit.http._

import java.util

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
object GitHubClientV2 extends GitHubClient {
  private val API_URL = "https://api.github.com"

  private lazy val restAdapter = new Builder()
    .setRequestInterceptor(r => r.addHeader("Accept", "application/vnd.github.v3.full+json"))
    .setEndpoint(API_URL)
    .setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("Retrofit"))
    .build()
  private lazy val sGitHubClientInterface = restAdapter.create(classOf[GitHubClientV2Interface])

  def getServiceClient = sGitHubClientInterface

  trait GitHubClientV2Interface {
    @DELETE("/repos/{owner}/{repo}")
    def deleteRepository(@Header("Authorization") basicCredentials: String,
                         @Path("owner") owner: String,
                         @Path("repo") repo: String): Response

    @GET("/authorizations")
    def getAuthorizations(@Header("Authorization") token: String): util.List[Authorization]

    @POST("/authorizations")
    def createDeleteAuthorization(@Header("Authorization") basicCredentials: String,
                                  @Body authorization: Authorization): Authorization
  }
}