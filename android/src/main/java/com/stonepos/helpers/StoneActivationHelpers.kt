package com.stonepos.helpers

import android.content.Context
import com.facebook.react.bridge.Promise
import com.stonepos.StonePosModule
import stone.application.interfaces.StoneCallbackInterface
import stone.providers.ActiveApplicationProvider

class StoneActivationHelpers {
  companion object {
    fun activationProvider(
      reactContext: Context,
      promise: Promise
    ): ActiveApplicationProvider {
      val activeApplicationProvider = ActiveApplicationProvider(reactContext)

      activeApplicationProvider.useDefaultUI(false)
      activeApplicationProvider.connectionCallback = object : StoneCallbackInterface {
        override fun onSuccess() {
          StonePosModule.updateUserList(reactContext)
          promise.resolve(true)
        }

        override fun onError() {
          promise.reject("201", activeApplicationProvider.listOfErrors.toString())
        }
      }
      return activeApplicationProvider
    }
  }
}
