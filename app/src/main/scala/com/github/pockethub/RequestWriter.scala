package com.github.pockethub

import java.io._
import java.util.zip.GZIPOutputStream

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
class RequestWriter(private val handle: File, private val version: Int) {
  private val TAG = "RequestWriter"

  def createDirectory(dir: File): Unit = {
    if (dir != null && !dir.exists()) {
      dir.mkdirs()
    }
  }

  def write[V >: Null](request: V): V = {
    createDirectory(handle.getParentFile)
    val dir = new RandomAccessFile(handle, "rw")
    val lock = dir.getChannel.lock()
    val result: Either[V, IOException] = try {
      inSafe(dir) {
        inSafe(lock) {
          val output = new ObjectOutputStream(
            new GZIPOutputStream(new FileOutputStream(dir.getFD), 8192)
          )
          inSafe(output) {
            output.writeInt(version)
            output.writeObject(request)
          }
        }
      }
      Left(request)
    } catch {
      case e: IOException =>
        Log.d(TAG, "Exception writing cache " + handle.getName, e)
        Right(e)
    }
    result match {
      case Left(r) => r
      case Right(e) => null
    }
  }
}
