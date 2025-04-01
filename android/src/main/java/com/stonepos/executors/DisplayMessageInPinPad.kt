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
import stone.providers.DisplayMessageProvider
import stone.utils.PinpadObject
import stone.utils.Stone

class DisplayMessageInPinPad(
  reactApplicationContext: ReactApplicationContext,
  currentActivity: Activity?
) : BaseExecutor(reactApplicationContext, currentActivity) {
  fun executeAction(
    pinpadMessage: String,
    pinpadMacAddress: String?,
    promise: Promise
  ) {
    try {
      if (StoneTransactionHelpers.isSDKInitialized()) {
        if (StonePosModule.currentUserList.isNullOrEmpty()) {
          throw CodedException("401", "You need to activate the terminal first")
        }

        if (!StoneTransactionHelpers.isRunningInPOS(reactApplicationContext)) {
          if (!Stone.isConnectedToPinpad()) {
            throw CodedException("402", "You need to connect to a pinpad first")
          }
        } else {
          throw CodedException("402", "You are running in POS mode.")
        }

        val selectedPinPad: PinpadObject? = if (!pinpadMacAddress.isNullOrEmpty()) {
          Stone.getPinpadObjectList().findLast {
            it.macAddress == pinpadMacAddress
          } ?: throw CodedException("402", "Pinpad not found")
        } else {
          if (Stone.getPinpadListSize() > 0) {
            Stone.getPinpadFromListAt(0)
          } else {
            throw CodedException("402", "No pinpad connected")
          }
        }

        val transactionProvider = DisplayMessageProvider(
          reactApplicationContext, pinpadMessage, selectedPinPad
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
    } catch (e: CodedException) {
      promise.reject(e.code, e.internalMessage)
    } catch (e: Exception) {
      promise.reject(e)
    }
  }
}
