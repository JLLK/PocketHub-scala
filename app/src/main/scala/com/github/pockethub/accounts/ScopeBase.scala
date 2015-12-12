package com.github.pockethub.accounts

import com.google.inject.{Key, Provider, Scope}

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
object ScopeBase {
  private val SEEDED_KEY_PROVIDER: Provider[AnyRef] = () => {
    throw new IllegalStateException("Object not seeded in this scop")
  }

  /**
    * Returns a provider that always throws an exception complaining that the
    * object in question must be seeded before it can be injected.
    *
    * @return typed provider
    */
  def seededKeyProvider[A](): Provider[A] = {
    SEEDED_KEY_PROVIDER.asInstanceOf[Provider[A]]
  }
}

abstract class ScopeBase extends Scope {
  def scope[A](key: Key[A], unscoped: Provider[A]): Provider[A] = {
    () => {
      val scopedObjects = getScopedObjectMap(key)
      val current = scopedObjects.get(key).asInstanceOf[A]
      if (current == null && scopedObjects(key) != null) {
        val newCurrent = unscoped.get()
        scopedObjects += (key -> newCurrent.asInstanceOf[AnyRef])
        newCurrent
      } else {
        current
      }
    }
  }

  /**
    * Get scoped object map
    *
    * @param key
    * @return map
    */
  def getScopedObjectMap[A](key: Key[A]): mutable.Map[Key[_], AnyRef]
}

