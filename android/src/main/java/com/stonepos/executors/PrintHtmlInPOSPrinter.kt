package com.stonepos.executors

import android.app.Activity
import android.graphics.Bitmap
import br.com.stone.posandroid.providers.PosPrintProvider
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.izettle.html2bitmap.Html2Bitmap
import com.izettle.html2bitmap.content.WebViewContent
import com.stonepos.StonePosModule
import com.stonepos.helpers.CodedException
import com.stonepos.helpers.StoneTransactionHelpers
import com.stonepos.helpers.writableMapOf
import stone.application.enums.Action
import stone.application.interfaces.StoneActionCallback
import kotlin.math.ceil

class PrintHtmlInPOSPrinter(
  reactApplicationContext: ReactApplicationContext,
  currentActivity: Activity?
) : BaseExecutor(reactApplicationContext, currentActivity) {
  fun executeAction(
    htmlContent: String,
    promise: Promise
  ) {
    reactApplicationContext.runOnNativeModulesQueueThread {
      checkSDKInitializedAndHandleExceptions(promise) {
        if (!StoneTransactionHelpers.isRunningInPOS(reactApplicationContext)) {
          throw CodedException("101", "You can only run this in a POS")
        }

        if (StonePosModule.currentUserList.isNullOrEmpty()) {
          throw CodedException("401", "You need to activate the terminal first")
        }

        val transactionProvider = PosPrintProvider(
          reactApplicationContext
        )

        val computedBitmap: Bitmap? =
          Html2Bitmap.Builder()
            .setContext(reactApplicationContext)
            .setContent(
              WebViewContent.html(htmlContent)
            )
            .setBitmapWidth(380)
            .build().bitmap

        if (computedBitmap != null) {
          var currentY = 0
          var currentBlock = 1
          val blockCount = ceil(computedBitmap.height / 595.00)

          while (currentBlock <= blockCount) {
            val targetHeight = if (currentY + 595 > computedBitmap.height) {
              computedBitmap.height - currentY
            } else {
              595
            }

            transactionProvider.addBitmap(
              Bitmap.createBitmap(computedBitmap, 0, currentY, computedBitmap.width, targetHeight)
            )

            currentY = if (currentY + 595 > computedBitmap.height) {
              computedBitmap.height - currentY
            } else {
              currentY + 595
            }

            currentBlock++
          }

          transactionProvider.useDefaultUI(false)

          transactionProvider.connectionCallback = object : StoneActionCallback {
            override fun onSuccess() {
              promise.resolve(true)
            }

            override fun onError() {
              promise.reject("405", "Generic Error - Transaction Failed [onError from Provider] - Check adb log output")
            }

            override fun onStatusChanged(action: Action?) {
              reactApplicationContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(
                  "PRINT_PROGRESS", writableMapOf(
                    "initiatorTransactionKey" to null,
                    "status" to action?.name
                  )
                )
            }
          }

          transactionProvider.execute()
        } else {
          throw CodedException("101", "Couldn't get bitmap from HTML")
        }
      }
    }
  }
}
