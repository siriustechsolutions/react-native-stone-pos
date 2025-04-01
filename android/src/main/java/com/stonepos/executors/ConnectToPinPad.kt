package com.stonepos.executors

import android.app.Activity
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.stonepos.StonePosModule
import com.stonepos.helpers.CodedException
import com.stonepos.helpers.StoneTransactionHelpers
import com.stonepos.helpers.writableMapOf
import stone.application.enums.Action
import stone.application.interfaces.StoneActionCallback
import stone.providers.BluetoothConnectionProvider
import stone.utils.PinpadObject

class ConnectToPinPad(
  reactApplicationContext: ReactApplicationContext,
  currentActivity: Activity?
) : BaseExecutor(reactApplicationContext, currentActivity) {
  fun executeAction(
    pinpadName: String,
    pinpadMacAddress: String,
    promise: Promise
  ) {
    checkSDKInitializedAndHandleExceptions(promise) {
      if (StonePosModule.currentUserList.isNullOrEmpty()) {
        throw CodedException("401", "You need to activate the terminal first")
      }

      if (StoneTransactionHelpers.isRunningInPOS(reactApplicationContext)) {
        throw CodedException("402", "You cannot connect to a pinpad in POS")
      }

      val transactionProvider = BluetoothConnectionProvider(
        reactApplicationContext, PinpadObject(pinpadName, pinpadMacAddress)
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
              "PINPAD_PROGRESS", writableMapOf(
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
