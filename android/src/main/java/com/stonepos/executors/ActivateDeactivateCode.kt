package com.stonepos.executors

import android.app.Activity
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.stonepos.StonePosModule
import com.stonepos.helpers.CodedException
import com.stonepos.helpers.ConversionHelpers
import com.stonepos.helpers.StoneActivationHelpers
import com.stonepos.helpers.writableArrayFrom

class ActivateDeactivateCode(
  reactApplicationContext: ReactApplicationContext,
  currentActivity: Activity?
) : BaseExecutor(reactApplicationContext, currentActivity) {
  fun executeGetActivatedCodes(promise: Promise) {
    checkSDKInitializedAndHandleExceptions(promise) {
      if (StonePosModule.currentUserList != null) {
        promise.resolve(
          writableArrayFrom(
            StonePosModule.currentUserList!!.map {
              ConversionHelpers.convertUserToWritableMap(it)
            }
          )
        )
      } else {
        throw CodedException("101", "No stone code activated")
      }
    }
  }

  fun executeAction(
    isActivationAction: Boolean, stoneCode: String,
    ignoreLastStoneCodeCheck: Boolean,
    promise: Promise
  ) {
    checkSDKInitializedAndHandleExceptions(promise) {
      if (isActivationAction) {
        if (StonePosModule.hasStoneCodeInList(stoneCode)) {
          promise.resolve(true)
          return@checkSDKInitializedAndHandleExceptions
        }
      } else {
        if (!StonePosModule.hasStoneCodeInList(stoneCode)) {
          throw CodedException("201", "Stone Code not currently activated")
        }
      }

      val transactionProvider = StoneActivationHelpers.activationProvider(
        reactApplicationContext,
        promise
      )

      if (isActivationAction) {
        transactionProvider.activate(stoneCode)
      } else {
        if (StonePosModule.userListCount() > 1 || ignoreLastStoneCodeCheck) {
          transactionProvider.deactivate(stoneCode)
        } else {
          throw CodedException("401", "You can't deactivate the only Stone Code in this POS")
        }
      }
    }
  }
}
