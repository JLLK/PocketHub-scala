package com.github.pockethub.core.commit

import java.util

import android.text.TextUtils
import com.alorma.github.sdk.bean.dto.response.{Commit, CommitComment}

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

/**
  * Created by chentao on 15/12/12.
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
class FullCommit(private val commit: Commit, comments: util.Collection[CommitComment])
  extends util.ArrayList[CommitComment] with Serializable {

  private val serialVersionUID = 2470370479577730822L
  private val files            = getFullCommitFiles(commit, comments)

  def this(commit: Commit) = this(commit, null)

  private def getFullCommitFiles(commit: Commit, comments: util.Collection[CommitComment]): ListBuffer[FullCommitFile] = {
    comments match {
      case null =>
        val rawFiles = commit.files
        if (rawFiles != null && !rawFiles.isEmpty) {
          val files = ListBuffer[FullCommitFile]()
          rawFiles.foreach(f => files += new FullCommitFile(f))
          files
        }
        else ListBuffer.empty
      case _ =>
        val rawFiles = commit.files
        val hasFiles = rawFiles != null && !rawFiles.isEmpty
        var hasComments = comments != null && !comments.isEmpty
        if (hasFiles) {
          val files = ListBuffer[FullCommitFile]()
          if (hasComments) {
            rawFiles.foreach(f => {
              val full = new FullCommitFile(f)
              comments
              .withFilter(c => f.getFileName.equals(c.path))
              .foreach(c => full.add(c))
              files.add(full)

              comments
              .filterNot(c => f.getFileName.equals(c.path))
              .foreach(comments.remove(_))
            })
            hasComments = !comments.isEmpty
            if (hasComments) addAll(comments)
            files
          }
          else {
            rawFiles.foreach(f => files.add(new FullCommitFile(f)))
            if (hasComments) addAll(comments)
            files
          }
        }
        else ListBuffer.empty
    }
  }

  override def add(comment: CommitComment): Boolean = {
    val path = comment.path
    if (TextUtils.isEmpty(path))
      super.add(comment)
    else {
      val file: FullCommitFile = files
      .dropWhile(f => !path.equals(f.getFile.filename))
      .map(e => e)
      .head
      if (file != null) {
        file.add(comment)
        true
      }
      else super.add(comment)
    }
  }

  /**
    * @return files
    */
  def getFiles: util.List[FullCommitFile] = files

  /**
    * @return commit
    */
  def getCommit: Commit = commit
}
