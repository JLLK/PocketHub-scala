package com.github.pockethub.accounts

import java.util.concurrent.ConcurrentHashMap

import android.accounts.{Account, AccountManager}
import com.google.inject.{AbstractModule, Key, Module, OutOfScopeException}

import scala.collection.mutable
/**
  * Created by chentao on 15/12/11.
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
object AccountScope {
  private val GITHUB_ACCOUNT_KEY = Key.get(classOf[GitHubAccount])

  /**
    * Create new module
    *
    * @return module
    */
  def module(): Module = {
    new AbstractModule() {
      override def configure() {
        val scope: AccountScope = new AccountScope
        bind(classOf[AccountScope]).toInstance(scope)
        bind(GITHUB_ACCOUNT_KEY).toProvider(ScopeBase.seededKeyProvider()).in(scope)
      }
    }
  }
}

class AccountScope extends ScopeBase {
  import AccountScope._

  private val currentAccount = new ThreadLocal[GitHubAccount]
  private val repoScopeMaps  = new ConcurrentHashMap[GitHubAccount, mutable.Map[Key[_], AnyRef]]()

  /**
    * Enters scope using a GitHubAccount derived from the supplied account
    *
    * @param account
    * @param accountManager
    */
  def enterWith(account: Account, accountManager: AccountManager) {
    enterWith(new GitHubAccount(account, accountManager))
  }

  /**
    * Enter scope with account
    *
    * @param account
    */
  def enterWith(account: GitHubAccount) {
    if (currentAccount.get != null)
      throw new IllegalStateException("A scoping block is already in progress")
    currentAccount.set(account)
  }

  /**
    * Exit scope
    */
  def exit(): Unit = {
    if (currentAccount.get == null)
      throw new IllegalStateException("No scoping block in progress")
    currentAccount.remove()
  }

  /**
    * Get scoped object map
    *
    * @param key
    * @return map
    */
  override def getScopedObjectMap[A](key: Key[A]): mutable.Map[Key[_], AnyRef] = {
    import scala.collection.JavaConversions._
    val account = currentAccount.get()
    if (account == null) {
      throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block")
    }
    val scopeMap = repoScopeMaps.get(account)
    if (scopeMap == null) {
      val newScopeMap = new ConcurrentHashMap[Key[_], AnyRef]()
      newScopeMap += (GITHUB_ACCOUNT_KEY -> account)
      newScopeMap
    } else {
      scopeMap
    }
  }
}
