package com.github.pockethub

import java.io.{FileInputStream, ObjectInputStream, RandomAccessFile, File}
import java.util.zip.GZIPInputStream

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
class RequestReader(private val handle: File, private val version: Int) {
  private val TAG = "RequestReader"

  /**
    * Read request data
    *
    * @return read data
    */
  def read[V >: AnyRef](): V = {
    !handle.exists() || handle.length() == 0 match {
      case true => null
      case _ =>
        val dir = new RandomAccessFile(handle, "rw")
        inSafe(dir) {
          val lock = dir.getChannel.lock()
          inSafe(lock) {
            val input = new ObjectInputStream(
              new GZIPInputStream(new FileInputStream(dir.getFD), 8192 * 8)
            )
            inSafe(input) {
              input.readInt() != version match {
                case true =>
                  dir.setLength(0)
                  null
                case _ =>
                  input.readObject().asInstanceOf[V]
              }
            }
          }
        }
    }
  }
}