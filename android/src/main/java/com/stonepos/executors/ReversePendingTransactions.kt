package com.stonepos.executors

import android.app.Activity
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.stonepos.StonePosModule
import com.stonepos.helpers.CodedException
import com.stonepos.helpers.writableMapOf
import stone.application.enums.Action
import stone.application.interfaces.StoneActionCallback
import stone.providers.ReversalProvider

class ReversePendingTransactions(
  reactApplicationContext: ReactApplicationContext,
  currentActivity: Activity?
) : BaseExecutor(reactApplicationContext, currentActivity) {
  fun executeAction(
    promise: Promise
  ) {
    checkSDKInitializedAndHandleExceptions(promise) {
      if (StonePosModule.currentUserList.isNullOrEmpty()) {
        throw CodedException("401", "You need to activate the terminal first")
      }

      val transactionProvider = ReversalProvider(
        reactApplicationContext
      )

      transactionProvider.useDefaultUI(false)

      transactionProvider.connectionCallback = object : StoneActionCallback {
        override fun onSuccess() {
          promise.resolve(true)
        }

        override fun onError() {
          promise.reject(
            "405",
            "Generic Error - Transaction Failed [onError from Provider] - Check adb log output"
          )
        }

        override fun onStatusChanged(action: Action?) {
          reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(
              "MAKE_TRANSACTION_PROGRESS", writableMapOf(
                "initiatorTransactionKey" to null,
                "status" to action?.name
              )
            )
        }
      }
      transactionProvider.execute()
    }
  }
}
