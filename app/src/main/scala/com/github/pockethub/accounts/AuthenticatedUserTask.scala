package com.github.pockethub.accounts

import java.io.IOException
import java.util.concurrent.Executor

import android.accounts.{Account, AccountManager}
import android.app.Activity
import android.content.Context
import com.github
import com.google.inject.Inject
import roboguice.inject.ContextScope
import roboguice.util.RoboAsyncTask

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
abstract class AuthenticatedUserTask[A](context: Context, executor: Executor)
  extends RoboAsyncTask[A](context, executor) {

  @Inject
  private implicit var contextScope: ContextScope = null

  @Inject
  private implicit var accountScope: AccountScope = null

  @Inject
  private var activity: Activity = null

  def this(context: Context) {
    this(context, null)
  }

  @throws[IOException]("IOException happened!")
  override def call(): A = {
    import github.pockethub._
    val manager = AccountManager.get(activity)
    val account = AccountUtils.getAccount(manager, activity)

    inAccountScope(account, manager) {
      inContextScope(context) {
        try {
          run(account)
        }
        catch {
          case e: IOException =>
            // Retry task if authentication failure occurs and account is
            // successfully updated
            if (AccountUtils.isUnauthorized(e)
              && AccountUtils.updateAccount(account, activity))
              run(account)
            else throw e
        }
      }
    }

  }

  /**
    * Execute task with an authenticated account
    *
    * @param account
    * @return result
    * @throws Exception
    */
  @throws(classOf[Exception])
  def run(account: Account): A
}