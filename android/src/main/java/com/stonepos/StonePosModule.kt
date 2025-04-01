package com.stonepos

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.bridge.*

import android.content.Context

import com.stonepos.executors.*
import com.stonepos.helpers.StoneTransactionHelpers
import stone.application.StoneStart
import stone.user.UserModel
import stone.utils.Stone
import stone.utils.keys.StoneKeyType

@ReactModule(name = StonePosModule.NAME)
class StonePosModule(reactContext: ReactApplicationContext) :
  NativeStonePosSpec(reactContext) {

  override fun getName(): String {
    return NAME
  }

  companion object {
    const val NAME = "StonePos"
    private const val IS_RUNNING_IN_POS = "IS_RUNNING_IN_POS"
    private const val STONE_SDK_VERSION = "STONE_SDK_VERSION"
    private var STONE_QRCODE_PROVIDER_ID = ""
    private var STONE_QRCODE_AUTHORIZATION = ""

    var currentUserList: List<UserModel>? = null
    fun hasStoneCodeInList(stoneCode: String): Boolean {
      if (currentUserList?.findLast { it.stoneCode.equals(stoneCode) } != null) {
        return true
      }

      return false
    }

    fun updateUserList(reactContext: Context) {
      synchronized(this) {
        if (Stone.isInitialized()) {
          currentUserList = StoneStart.init(
            reactContext, hashMapOf(
              StoneKeyType.QRCODE_PROVIDERID to STONE_QRCODE_PROVIDER_ID,
              StoneKeyType.QRCODE_AUTHORIZATION to STONE_QRCODE_AUTHORIZATION
            )
          )
        }
      }
    }

    fun userListCount(): Int {
      return if (currentUserList != null) currentUserList!!.size else 0
    }

    fun hasPixKeysProvided(): Boolean {
      return !(STONE_QRCODE_PROVIDER_ID.isNullOrEmpty() || STONE_QRCODE_AUTHORIZATION.isNullOrEmpty())
    }
  }

  override fun getConstants(): Map<String, Any> {
    val constants: MutableMap<String, Any> = HashMap()

    constants[IS_RUNNING_IN_POS] = StoneTransactionHelpers.isRunningInPOS(reactApplicationContext)
    constants[STONE_SDK_VERSION] = Stone.getSdkVersion()

    return constants
  }

  @ReactMethod
  override fun initSDK(
    appName: String,
    qrCodeProviderKey: String,
    qrCodeProviderAuthorization: String,
    promise: Promise
  ) {
    try {
      synchronized(this) {
        STONE_QRCODE_PROVIDER_ID = qrCodeProviderKey
        STONE_QRCODE_AUTHORIZATION = qrCodeProviderAuthorization

        currentUserList = StoneStart.init(
          reactApplicationContext, hashMapOf(
            StoneKeyType.QRCODE_PROVIDERID to qrCodeProviderKey,
            StoneKeyType.QRCODE_AUTHORIZATION to qrCodeProviderAuthorization
          )
        )

        Stone.setAppName(appName)

        promise.resolve(true)
      }
    } catch (e: Exception) {
      promise.reject(e)
    }
  }


  @ReactMethod
  override fun activateCode(
    stoneCode: String,
    promise: Promise
  ) {
    try {
      ActivateDeactivateCode(reactApplicationContext, currentActivity).executeAction(
        isActivationAction = true,
        stoneCode,
        ignoreLastStoneCodeCheck = false,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun getActivatedCodes(promise: Promise) {
    try {
      ActivateDeactivateCode(reactApplicationContext, currentActivity).executeGetActivatedCodes(
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun getAllTransactionsOrderByIdDesc(promise: Promise) {
    try {
      GetTransactions(reactApplicationContext, currentActivity).executeActionOrderByIdDesc(promise)
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun getLastTransaction(promise: Promise) {
    try {
      GetTransactions(reactApplicationContext, currentActivity).executeActionGetLastTransaction(
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun findTransactionWithAuthorizationCode(authorizationCode: String, promise: Promise) {
    try {
      GetTransactions(
        reactApplicationContext,
        currentActivity
      ).executeFindTransactionWithAuthorizationCode(authorizationCode, promise)
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun findTransactionWithInitiatorTransactionKey(
    initiatorTransactionKey: String,
    promise: Promise
  ) {
    try {
      GetTransactions(
        reactApplicationContext,
        currentActivity
      ).executeFindTransactionWithInitiatorTransactionKey(initiatorTransactionKey, promise)
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun findTransactionWithId(transactionId: Double, promise: Promise) {
    try {
      GetTransactions(reactApplicationContext, currentActivity).executeFindTransactionWithId(
        transactionId.toInt(),
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun reversePendingTransactions(
    promise: Promise
  ) {
    try {
      ReversePendingTransactions(reactApplicationContext, currentActivity).executeAction(
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun voidTransaction(
    transactionAtk: String,
    promise: Promise
  ) {
    try {
      VoidTransaction(reactApplicationContext, currentActivity).executeAction(
        transactionAtk,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun captureTransaction(
    transactionAtk: String,
    promise: Promise
  ) {
    try {
      CaptureTransaction(reactApplicationContext, currentActivity).executeAction(
        transactionAtk,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun makeTransaction(
    transactionSetup: ReadableMap,
    promise: Promise
  ) {
    try {
      MakeTransaction(reactApplicationContext, currentActivity).executeAction(
        transactionSetup,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun cancelRunningTaskMakeTransaction(
    promise: Promise
  ) {
    try {
      MakeTransaction(reactApplicationContext, currentActivity).cancelAction(
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  fun sendTransactionReceiptMail(
    transactionAtk: String,
    receiptType: String,
    toContact: ReadableArray,
    fromContact: ReadableMap,
    promise: Promise
  ) {
    try {
      SendTransactionReceiptMail(reactApplicationContext, currentActivity).executeAction(
        transactionAtk,
        receiptType,
        toContact,
        fromContact,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun fetchTransactionsForCard(
    pinpadMacAddress: String?,
    promise: Promise
  ) {
    try {
      FetchTransactionsForCard(reactApplicationContext, currentActivity).executeAction(
        pinpadMacAddress,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun displayMessageInPinPad(
    pinpadMessage: String,
    pinpadMacAddress: String?,
    promise: Promise
  ) {
    try {
      DisplayMessageInPinPad(reactApplicationContext, currentActivity).executeAction(
        pinpadMessage,
        pinpadMacAddress,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun connectToPinPad(
    pinpadName: String,
    pinpadMacAddress: String,
    promise: Promise
  ) {
    try {
      ConnectToPinPad(reactApplicationContext, currentActivity).executeAction(
        pinpadName,
        pinpadMacAddress,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun printReceiptInPOSPrinter(
    receiptType: String,
    transactionAtk: String,
    isReprint: Boolean,
    promise: Promise
  ) {
    try {
      PrintReceiptInPOSPrinter(reactApplicationContext, currentActivity).executeAction(
        receiptType,
        transactionAtk,
        isReprint,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun printHTMLInPOSPrinter(
    htmlContent: String,
    promise: Promise
  ) {
    try {
      PrintHtmlInPOSPrinter(reactApplicationContext, currentActivity).executeAction(
        htmlContent,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun mifareDetectCard(
    promise: Promise
  ) {
    try {
      MifarePOSExecutor(reactApplicationContext, currentActivity).executeDetectCard(
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun mifareAuthenticateSector(
    keyType: Double,
    sector: Double,
    key: String,
    promise: Promise
  ) {
    try {
      MifarePOSExecutor(reactApplicationContext, currentActivity).executeAuthenticateSector(
        keyType.toInt(),
        key,
        sector.toInt(),
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun mifareReadBlock(
    keyType: Double,
    sector: Double,
    block: Double,
    key: String,
    promise: Promise
  ) {
    try {
      MifarePOSExecutor(reactApplicationContext, currentActivity).executeReadBlock(
        keyType.toInt(),
        key,
        sector.toInt(),
        block.toInt(),
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  @ReactMethod
  override fun mifareWriteBlock(
    keyType: Double,
    sector: Double,
    block: Double,
    data: String,
    key: String,
    promise: Promise
  ) {
    try {
      MifarePOSExecutor(reactApplicationContext, currentActivity).executeWriteBlock(
        keyType.toInt(),
        key,
        sector.toInt(),
        block.toInt(),
        data,
        promise
      )
    } catch (e: Exception) {
      promise.reject(e)
    }
  }

  override fun addListener(eventName: String?) = Unit;

  override fun removeListeners(count: Double) = Unit;
}
