package com.github.pockethub

import android.accounts.Account
import android.content.Context
import android.util.Log
import com.github.pockethub.accounts.{AccountUtils, AuthenticatedUserLoader}

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
  */
abstract class ThrowableLoader[A](context: Context, val data: A)
  extends AuthenticatedUserLoader[A](context) {
  private val TAG = "ThrowableLoader"
  private var exception: Exception = null

  override protected def getAccountFailureData: A = data

  override def load(account: Account): A = {
    val result: Either[A, Exception] = try {
      Left(loadData())
    } catch {
      case e: Exception =>
        if (AccountUtils.isUnauthorized(e)
          && AccountUtils.updateAccount(account, activity)) {
          try {
            Left(loadData())
          } catch {
            case e2: Exception => Right(e2)
          }
        } else {
          Right(e)
        }
    }
    result match {
      case Left(d) => d
      case Right(e) =>
        Log.d(TAG, "Exception loading data", e)
        exception = e; data
    }
  }

  def getException = exception

  /**
    * Clear the stored exception and return it
    *
    * @return exception
    */
  def clearException: Exception = {
    val throwable = exception
    exception = null
    throwable
  }

  /**
    * Load data
    *
    * @return data
    * @throws Exception
    */
  @throws(classOf[Exception])
  def loadData(): A
}
