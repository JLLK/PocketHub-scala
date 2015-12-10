package com.github

import java.io.{IOException, Closeable}
import java.nio.channels.FileLock

import android.accounts.{AccountManager, Account}
import android.content.Context
import android.util.Log
import com.github.pockethub.accounts.AccountScope
import roboguice.inject.ContextScope

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
package object pockethub {
  private val TAG = "pockethub"

  def inSafe[A](in: Closeable)(fun: => A): A = {
    try {
      fun
    } finally {
      if (in != null) {
        try
          in.close()
        catch {
          case e: IOException =>
            Log.d(TAG, "Exception closing stream", e)
        }
      }
    }
  }

  def inSafe[A](in: FileLock)(fun: => A): A = {
    try {
      fun
    } finally {
      if (in != null) {
        try
          in.release()
        catch {
          case e: IOException =>
            Log.d(TAG, "Exception unlocking file", e)
        }
      }
    }
  }

  def inAccountScope[A](account: Account, manager: AccountManager)(fun: => A)(implicit accountScope: AccountScope): A = {
    require(account != null)
    require(manager != null)
    require(accountScope != null)
    accountScope.enterWith(account, manager)
    try{
      fun
    } finally {
      accountScope.exit()
    }
  }

  def inContextScope[A](context: Context)(fun: => A)(implicit contextScope: ContextScope): A = {
    require(context != null)
    require(contextScope != null)
    contextScope.enter(context)
    try{
      fun
    } finally {
      contextScope.exit(context)
    }
  }
}