package com.github.pockethub.core.ref

import android.text.TextUtils
import com.alorma.github.sdk.bean.dto.response.GitReference

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
  *
  */
object RefUtils {
  private val PREFIX_REFS   = "refs/"
  private val PREFIX_PULL   = PREFIX_REFS + "pull/"
  private val PREFIX_TAG    = PREFIX_REFS + "tags/"
  private val PREFIX_HEADS  = PREFIX_REFS + "heads/"

  /**
    * Is reference a branch?
    *
    * @param ref
    * @return true if branch, false otherwise
    */
  def isBranch(ref: GitReference) = ref match {
    case null => false
    case _    =>
      val name = ref.ref
      !TextUtils.isEmpty(name) && name.startsWith(PREFIX_HEADS)
  }

  /**
    * Is reference a tag?
    *
    * @param ref
    * @return true if tag, false otherwise
    */
  def isTag(ref: GitReference): Boolean = ref != null && isTag(ref.ref)

  /**
    * Is reference a tag?
    *
    * @param name
    * @return true if tag, false otherwise
    */
  def isTag(name: String): Boolean = !TextUtils.isEmpty(name) && name.startsWith(PREFIX_TAG)

  /**
    * Get path of ref with leading 'refs/' segment removed if present
    *
    * @param ref
    * @return full path
    */
  def getPath(ref: GitReference): String = ref match {
    case null => null
    case _    =>
      val name = ref.ref
      if (!TextUtils.isEmpty(name) && name.startsWith(PREFIX_REFS))
        name.substring(PREFIX_REFS.length)
      else name
  }

  /**
    * Get short name for ref
    *
    * @param ref
    * @return short name
    */
  def getName(ref: GitReference): String = ref match {
    case null => null
    case _    => getName(ref.ref)
  }

  /**
    * Get short name for ref
    *
    * @param name
    * @return short name
    */
  def getName(name: String): String = {
    if (TextUtils.isEmpty(name)) name
    if (name.startsWith(PREFIX_HEADS)) name.substring(PREFIX_HEADS.length)
    else if (name.startsWith(PREFIX_TAG)) name.substring(PREFIX_TAG.length)
    else if (name.startsWith(PREFIX_REFS)) name.substring(PREFIX_REFS.length)
    else name
  }

  /**
    * Should the given reference be included as valid?
    * <p>
    * This filters out pull request refs
    *
    * @param ref
    * @return true if valid, false otherwise
    */
  def isValid(ref: GitReference): Boolean = ref match {
    case null => false
    case _ =>
      val name: String = ref.ref
      !TextUtils.isEmpty(name) && !name.startsWith(PREFIX_PULL)
  }
}
