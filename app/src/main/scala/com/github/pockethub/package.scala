package com.github

import java.io.Closeable
import java.nio.channels.FileLock

import android.util.Log

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

  def inSafe(in: Closeable)(fun: => Unit): Unit = {
    try
      fun
    finally {
      if (in != null) {
        try
          in.close()
        catch {
          case e: Throwable =>
            Log.d(TAG, "Exception closing stream", e)
        }
      }
    }
  }

  def inSafe(in: FileLock)(fun: => Unit): Unit = {
    try
      fun
    finally {
      if (in != null) {
        try
          in.release()
        catch {
          case e: Throwable =>
            Log.d(TAG, "Exception unlocking file", e)
        }
      }
    }
  }
}