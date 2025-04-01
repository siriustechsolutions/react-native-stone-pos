import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface TransactionRequest {
  amountInCents: string;
  stoneCode?: string;
  pinpadMacAddress?: string;
  capture: boolean;
  typeOfTransaction: 'DEBIT' | 'CREDIT' | 'VOUCHER' | 'INSTANT_PAYMENT' | 'PIX';
  entryMode?:
    | 'MAGNETIC_STRIPE'
    | 'CHIP_N_PIN'
    | 'CONTACTLESS'
    | 'CONTACTLESS_MAG'
    | 'QRCODE'
    | 'UNKNOWN';
  installmentCount?: number;
  installmentHasInterest?: boolean;
  initiatorTransactionKey?: string;
  shortName?: string;
  subMerchantCategoryCode?: string;
  subMerchantAddress?: string;
  subMerchantCity?: string;
  subMerchantPostalAddress?: string;
  subMerchantRegisteredIdentifier?: string;
  subMerchantTaxIdentificationNumber?: string;
}

export interface Spec extends TurboModule {
  initSDK(
    appName: string,
    qrCodeProviderKey: string,
    qrCodeProviderAuthorization: string
  ): Promise<boolean>;
  activateCode(stoneCode: string): Promise<any>;
  getActivatedCodes(): Promise<any>;
  getAllTransactionsOrderByIdDesc(): Promise<any>;
  getLastTransaction(): Promise<any>;
  findTransactionWithAuthorizationCode(authorizationCode: string): Promise<any>;
  findTransactionWithInitiatorTransactionKey(
    initiatorTransactionKey: string
  ): Promise<any>;
  findTransactionWithId(transactionId: number): Promise<any>;
  reversePendingTransactions(): Promise<any>;
  voidTransaction(transactionAtk: string): Promise<any>;
  captureTransaction(transactionAtk: string): Promise<any>;
  makeTransaction(transactionSetup: TransactionRequest): Promise<any>;
  cancelRunningTaskMakeTransaction(): Promise<any>;
  fetchTransactionsForCard(pinpadMacAddress?: string): Promise<any>;
  displayMessageInPinPad(
    pinpadMessage: string,
    pinpadMacAddress?: string
  ): Promise<any>;
  connectToPinPad(pinpadName: string, pinpadMacAddress: string): Promise<any>;
  printReceiptInPOSPrinter(
    receiptType: string,
    transactionAtk: string,
    isReprint: boolean
  ): Promise<any>;
  printHTMLInPOSPrinter(htmlContent: string): Promise<any>;
  mifareDetectCard(): Promise<any>;
  mifareAuthenticateSector(
    keyType: number,
    sector: number,
    key: string
  ): Promise<any>;
  mifareReadBlock(
    keyType: number,
    sector: number,
    block: number,
    key: string
  ): Promise<any>;
  mifareWriteBlock(
    keyType: number,
    sector: number,
    block: number,
    data: string,
    key: string
  ): Promise<any>;
  addListener(eventName: string): void;
  removeListeners(count: number): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('StonePos');
