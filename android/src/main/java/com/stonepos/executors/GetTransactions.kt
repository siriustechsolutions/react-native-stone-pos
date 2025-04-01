package com.stonepos.executors

import android.app.Activity
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.stonepos.helpers.ConversionHelpers
import com.stonepos.helpers.writableArrayFrom
import stone.database.transaction.TransactionDAO

class GetTransactions(
  reactApplicationContext: ReactApplicationContext,
  currentActivity: Activity?
) : BaseExecutor(reactApplicationContext, currentActivity) {
  private fun getDaoObject(): TransactionDAO {
    return TransactionDAO(reactApplicationContext)
  }

  fun executeActionOrderByIdDesc(promise: Promise) {
    checkSDKInitializedAndHandleExceptions(promise) {
      promise.resolve(
        writableArrayFrom(
          getDaoObject().getAllTransactionsOrderByIdDesc().map {
            ConversionHelpers.convertTransactionToWritableMap(it)
          }
        )
      )
    }
  }

  fun executeActionGetLastTransaction(promise: Promise) {
    checkSDKInitializedAndHandleExceptions(promise) {
      val dao = getDaoObject()
      val trx = dao.findTransactionWithId(dao.getLastTransactionId())
      if (trx != null) {
        promise.resolve(
          ConversionHelpers.convertTransactionToWritableMap(trx)
        )
      } else {
        promise.resolve(null)
      }
    }
  }

  fun executeFindTransactionWithAuthorizationCode(authorizationCode: String, promise: Promise) {
    checkSDKInitializedAndHandleExceptions(promise) {
      val dao = getDaoObject()
      val trx = dao.findTransactionWithAuthorizationCode(authorizationCode)
      if (trx != null) {
        promise.resolve(
          ConversionHelpers.convertTransactionToWritableMap(trx)
        )
      } else {
        promise.resolve(null)
      }
    }
  }

  fun executeFindTransactionWithInitiatorTransactionKey(
    initiatorTransactionKey: String,
    promise: Promise
  ) {
    checkSDKInitializedAndHandleExceptions(promise) {
      val dao = getDaoObject()
      val trx = dao.findTransactionWithAtk(initiatorTransactionKey)
      if (trx != null) {
        promise.resolve(
          ConversionHelpers.convertTransactionToWritableMap(trx)
        )
      } else {
        promise.resolve(null)
      }
    }
  }

  fun executeFindTransactionWithId(
    transactionId: Int,
    promise: Promise
  ) {
    checkSDKInitializedAndHandleExceptions(promise) {
      val dao = getDaoObject()
      val trx = dao.findTransactionWithId(transactionId)
      if (trx != null) {
        promise.resolve(
          ConversionHelpers.convertTransactionToWritableMap(trx)
        )
      } else {
        promise.resolve(null)
      }
    }
  }
}
