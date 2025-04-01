import StonePos, { type TransactionRequest } from './NativeStonePos';

export type { TransactionRequest };

export type StoneAddressModelType = {
  distric: string;
  city: string;
  street: string;
  doorNumber: string;
  neighborhood: string;
};

export type StoneUserModelType = {
  stoneCode: string;
  merchantName: string;
  merchantAddress: StoneAddressModelType;
  merchantDocumentNumber: string;
  saleAffiliationKey: string;
};

export type StoneTransactionResponseInfoType = {
  id: number;
  appLabel: string;
  transTypeEnum: string;
};

export type StoneTransAppSelectedInfo = {
  brandName: string;
  aid: string;
  transactionTypeInfo: StoneTransactionResponseInfoType;
  cardAppLabel: string;
  paymentBusinessModel: string;
  brandId: number;
};

export type StoneInstalmentType = {
  count?: number;
  interest?: boolean;
  name?: string;
};

export type StoneTransactionResponse = {
  amount: string;
  emailSent?: string;
  timeToPassTransaction?: string;
  initiatorTransactionKey?: string;
  acquirerTransactionKey?: string;
  cardHolderNumber?: string;
  cardHolderName?: string;
  date?: string;
  time?: string;
  aid?: string;
  arcq?: string;
  authorizationCode?: string;
  iccRelatedData?: string;
  transactionReference?: string;
  actionCode?: string;
  commandActionCode?: string;
  pinpadUsed?: string;
  saleAffiliationKey?: string;
  cne?: string;
  cvm?: string;
  balance?: string;
  serviceCode?: string;
  subMerchantCategoryCode?: string;
  entryMode?: string;
  cardBrandName?: string;
  instalmentTransaction?: StoneInstalmentType;
  transactionStatus?:
    | 'UNKNOWN'
    | 'APPROVED'
    | 'DECLINED'
    | 'DECLINED_BY_CARD'
    | 'CANCELLED'
    | 'PARTIAL_APPROVED'
    | 'TECHNICAL_ERROR'
    | 'REJECTED'
    | 'WITH_ERROR'
    | 'PENDING_REVERSAL'
    | 'PENDING'
    | 'REVERSED';
  instalmentType?: string;
  typeOfTransactionEnum?: string;
  cancellationDate?: string;
  shortName?: string;
  subMerchantAddress?: string;
  userModel?: StoneUserModelType;
  cvv?: string;
  isFallbackTransaction?: boolean;
  subMerchantCity?: string;
  subMerchantTaxIdentificationNumber?: string;
  subMerchantRegisteredIdentifier?: string;
  subMerchantPostalAddress?: string;
  appLabel?: string;
  transAppSelectedInfo?: StoneTransAppSelectedInfo;
  cardExpireDate?: string;
  cardSequenceNumber?: string;
  externalId?: string;
  messageFromAuthorizer?: string;
};

export type StoneReceiptType = 'CLIENT' | 'MERCHANT';

export enum StoneMifareKeyType {
  TypeA = 0,
  TypeB = 1,
}

export interface StoneMakeTransaction {
  amountInCents: string;
  installmentCount?: number;
  typeOfTransaction: any;
  installmentHasInterest?: boolean;
}

export interface StoneInitSDKRequest {
  appName: string;
  qrCodeProviderKey: string;
  qrCodeProviderAuthorization: string;
}

export const StonePosSDK = new (class {
  initSDK(stoneInitSDKRequest: StoneInitSDKRequest): Promise<boolean> {
    return StonePos.initSDK(
      stoneInitSDKRequest.appName,
      stoneInitSDKRequest.qrCodeProviderKey,
      stoneInitSDKRequest.qrCodeProviderAuthorization
    );
  }

  cancelRunningTransaction(): Promise<boolean> {
    return StonePos.reversePendingTransactions();
  }

  voidTransaction(transactionAtk: string): Promise<StoneTransactionResponse> {
    return StonePos.voidTransaction(transactionAtk);
  }

  makeTransaction(
    transactionRequest: TransactionRequest
  ): Promise<StoneTransactionResponse> {
    return StonePos.makeTransaction(transactionRequest);
  }

  cancelRunningTaskMakeTransaction(): Promise<boolean> {
    return StonePos.cancelRunningTaskMakeTransaction();
  }

  reprintCustomerReceipt(
    receiptType: StoneReceiptType,
    transactionAtk: string,
    isReprint: boolean
  ): Promise<boolean> {
    return StonePos.printReceiptInPOSPrinter(
      receiptType,
      transactionAtk,
      isReprint
    );
  }

  printByHtml(htmlContent: string): Promise<boolean> {
    return StonePos.printHTMLInPOSPrinter(htmlContent);
  }

  captureTransaction(
    transactionAtk: string
  ): Promise<StoneTransactionResponse> {
    return StonePos.captureTransaction(transactionAtk);
  }

  activateCode(stoneCode: string): Promise<boolean> {
    return StonePos.activateCode(stoneCode);
  }

  getActivatedCodes(): Promise<StoneUserModelType> {
    return StonePos.getActivatedCodes();
  }

  getAllTransactionsByIdDesc(): Promise<StoneTransactionResponse[]> {
    return StonePos.getAllTransactionsOrderByIdDesc();
  }

  getLastTransaction(): Promise<StoneTransactionResponse | null> {
    return StonePos.getLastTransaction();
  }

  findTransactionWithAuthorizationCode(
    authorizationCode: string
  ): Promise<StoneTransactionResponse | null> {
    return StonePos.findTransactionWithAuthorizationCode(authorizationCode);
  }

  findTransactionWithInitiatorTransactionKey(
    initiatorTransactionKey: string
  ): Promise<StoneTransactionResponse | null> {
    return StonePos.findTransactionWithInitiatorTransactionKey(
      initiatorTransactionKey
    );
  }

  findTransactionWithId(
    transactionId: number
  ): Promise<StoneTransactionResponse | null> {
    return StonePos.findTransactionWithId(transactionId);
  }

  fetchTransactionsForCard(
    pinpadMacAddress?: string
  ): Promise<StoneTransactionResponse[]> {
    return StonePos.fetchTransactionsForCard(pinpadMacAddress);
  }

  displayMessageInPinPad(
    pinpadMessage: string,
    pinpadMacAddress?: string
  ): Promise<boolean> {
    return StonePos.displayMessageInPinPad(pinpadMessage, pinpadMacAddress);
  }

  connectToPinPad(
    pinpadName: string,
    pinpadMacAddress: string
  ): Promise<boolean> {
    return StonePos.connectToPinPad(pinpadName, pinpadMacAddress);
  }

  mifareDetectCard(): Promise<string[]> {
    return StonePos.mifareDetectCard();
  }

  mifareAuthenticateSector(
    keyType: StoneMifareKeyType,
    sector: number,
    key: string = 'FFFFFFFFFFFF'
  ): Promise<boolean> {
    return StonePos.mifareAuthenticateSector(keyType, sector, key);
  }

  mifareReadBlock(
    keyType: StoneMifareKeyType,
    sector: number,
    block: number,
    key: string = 'FFFFFFFFFFFF'
  ): Promise<string[]> {
    if (block < 0 || block > 3) {
      throw Error('Your block must be between 0-3 inclusive');
    }

    return StonePos.mifareReadBlock(keyType, sector, block, key);
  }

  mifareWriteBlock(
    keyType: StoneMifareKeyType,
    sector: number,
    block: number,
    data: string,
    key: string = 'FFFFFFFFFFFF'
  ): Promise<string[]> {
    if (block < 0 || block > 3) {
      throw Error('Your block must be between 0-3 inclusive');
    }

    return StonePos.mifareWriteBlock(keyType, sector, block, data, key);
  }
})();
