import {
  Text,
  View,
  StyleSheet,
  Button,
  TextInput,
  Alert,
  NativeEventEmitter,
} from 'react-native';
import { useState, useRef } from 'react';
import {
  StonePosSDK,
  type StoneTransactionResponse,
} from 'react-native-stone-pos';
import Config from 'react-native-config';

const Trasaction = () => {
  const [isActivated, setIsActivated] = useState(false);
  const [amount, setAmount] = useState('');
  const [installments, setInstallments] = useState('1');
  const [transactionAtk, setTransactionAtk] = useState('');
  const [transactions, setTransactions] = useState<StoneTransactionResponse[]>(
    []
  );
  const [loading, setLoading] = useState(false);
  const [statusMessage, setStatusMessage] = useState('');

  const savedHandler = useRef<(data: any) => void>(null);

  // Adiciona listener para eventos de transação
  const handleEventListener = async (
    eventName: string,
    handler: (data: any) => void
  ) => {
    savedHandler.current = handler;

    const eventEmitter = new NativeEventEmitter(StonePosSDK);
    const eventSubscription = eventEmitter.addListener(
      eventName,
      (event: any) => {
        savedHandler.current?.(event);
      }
    );

    return () => {
      eventSubscription.remove();
    };
  };

  handleEventListener('MAKE_TRANSACTION_PROGRESS', (event: any) => {
    console.info('MAKE_TRANSACTION_PROGRESS', event);

    if (event.status === 'TRANSACTION_WAITING_CARD') {
      // Quando solicita para aproximar o cartão
      console.info('TRANSACTION_WAITING_CARD');
    }

    if (event.status === 'TRANSACTION_SENDING') {
      // Status da transação
      console.info('TRANSACTION_SENDING');
    }

    if (event.status === 'TRANSACTION_WAITING_QRCODE_SCAN') {
      // Quando gera o QRCode
      console.info('TRANSACTION_WAITING_QRCODE_SCAN', event.qrCode);
    }
  });

  // Função para processar pagamento
  const processPayment = async () => {
    if (!amount) {
      Alert.alert('Erro', 'Informe o valor da transação');
      return;
    }

    try {
      setLoading(true);
      setStatusMessage('Processando pagamento...');

      // Converte o valor para centavos (formato exigido pela SDK)
      const amountInCents = Math.round(parseFloat(amount) * 100);
      const installmentsValue = parseInt(installments, 10);

      const transaction = await StonePosSDK.makeTransaction({
        typeOfTransaction: 'CREDIT',
        installmentCount: installmentsValue,
        installmentHasInterest: false,
        amountInCents: String(amountInCents),
        initiatorTransactionKey: `TRANS_${Date.now()}`,
        capture: true,
      });

      if (transaction) {
        setTransactionAtk(transaction.acquirerTransactionKey || 'Sem ATK');
        setStatusMessage('Pagamento realizado com sucesso!');
        Alert.alert(
          'Sucesso',
          `Pagamento realizado! ATK: ${transaction.acquirerTransactionKey}`
        );

        loadTransactions();
      }
    } catch (error: any) {
      console.error('Erro ao processar pagamento:', error);
      setStatusMessage(`Erro: ${error?.message}`);
      Alert.alert('Erro', `Falha no pagamento: ${error?.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Função para cancelar transação
  const voidTransaction = async () => {
    if (!transactionAtk) {
      Alert.alert('Erro', 'Informe o ATK da transação');
      return;
    }

    try {
      setLoading(true);
      setStatusMessage('Cancelando transação...');

      const result = await StonePosSDK.voidTransaction(transactionAtk);

      if (result) {
        setStatusMessage('Transação cancelada com sucesso!');
        Alert.alert('Sucesso', 'Transação cancelada com sucesso!');

        // Atualiza a lista de transações
        loadTransactions();
      }
    } catch (error: any) {
      console.error('Erro ao cancelar transação:', error);
      setStatusMessage(`Erro: ${error?.message}`);
      Alert.alert('Erro', `Falha ao cancelar: ${error?.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Função para imprimir recibo
  const printReceipt = async () => {
    try {
      setLoading(true);
      setStatusMessage('Imprimindo recibo...');

      const htmlContent = `
        <html>
          <body style="text-align: center; font-family: sans-serif;">
            <h1>COMPROVANTE DE VENDA</h1>
            <p>Valor: R$ ${amount}</p>
            <p>Parcelas: ${installments}</p>
            <p>ATK: ${transactionAtk || 'N/A'}</p>
            <p>Data: ${new Date().toLocaleDateString()}</p>
            <p>Hora: ${new Date().toLocaleTimeString()}</p>
            <hr>
            <p>Obrigado pela preferência!</p>
          </body>
        </html>
      `;

      const result = await StonePosSDK.printByHtml(htmlContent);

      if (result) {
        setStatusMessage('Recibo impresso com sucesso!');
        Alert.alert('Sucesso', 'Recibo impresso com sucesso!');
      }
    } catch (error: any) {
      console.error('Erro ao imprimir recibo:', error);
      setStatusMessage(`Erro: ${error?.message}`);
      Alert.alert('Erro', `Falha ao imprimir: ${error?.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Função para carregar transações
  const loadTransactions = async () => {
    try {
      setLoading(true);
      setStatusMessage('Carregando transações...');

      const result = await StonePosSDK.getAllTransactionsByIdDesc();

      if (result) {
        setTransactions(result);
        setStatusMessage(`${result.length} transações carregadas`);
      }
    } catch (error: any) {
      console.error('Erro ao carregar transações:', error);
      setStatusMessage(`Erro: ${error?.message}`);
      Alert.alert('Erro', `Falha ao carregar transações: ${error?.message}`);
    } finally {
      setLoading(false);
    }
  };

  const activateTerminal = async () => {
    const result = await StonePosSDK.initSDK({
      appName: Config.APP_NAME || 'StonePos Example',
      qrCodeProviderKey: Config.QR_CODE_PROVIDER_KEY || '',
      qrCodeProviderAuthorization: Config.QR_CODE_PROVIDER_AUTHORIZATION || '',
    });
    setIsActivated(result);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Stone POS Demo</Text>

      <View style={styles.statusContainer}>
        <Text style={styles.statusText}>
          Status: {isActivated ? 'Terminal Ativado' : 'Terminal Não Ativado'}
        </Text>
        <Text style={styles.statusMessage}>{statusMessage}</Text>
      </View>

      {!isActivated && (
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Ativação do Terminal</Text>
          <Button
            title="Ativar Terminal"
            onPress={() => activateTerminal()}
            disabled={loading}
          />
        </View>
      )}

      {isActivated && (
        <>
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Pagamento</Text>
            <TextInput
              style={styles.input}
              placeholder="Valor (R$)"
              keyboardType="decimal-pad"
              value={amount}
              onChangeText={setAmount}
            />
            <TextInput
              style={styles.input}
              placeholder="Parcelas"
              keyboardType="number-pad"
              value={installments}
              onChangeText={setInstallments}
            />
            <Button
              title="Processar Pagamento"
              onPress={processPayment}
              disabled={loading}
            />
          </View>

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Gerenciar Transação</Text>
            <TextInput
              style={styles.input}
              placeholder="ATK da Transação"
              value={transactionAtk}
              onChangeText={setTransactionAtk}
            />
            <View style={styles.buttonRow}>
              <View style={styles.button}>
                <Button
                  title="Cancelar"
                  onPress={voidTransaction}
                  disabled={loading}
                  color="#ff3b30"
                />
              </View>
            </View>
            <Button
              title="Imprimir Recibo"
              onPress={printReceipt}
              disabled={loading}
            />
          </View>

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>Histórico de Transações</Text>
            <Button
              title="Carregar Transações"
              onPress={loadTransactions}
              disabled={loading}
            />

            {transactions.length > 0 ? (
              <View style={styles.transactionList}>
                {transactions.map((transaction, index) => (
                  <View key={index} style={styles.transactionItem}>
                    <Text>ATK: {transaction.acquirerTransactionKey}</Text>
                    <Text>Valor: R$ {transaction.amount}</Text>
                    <Text>Status: {transaction.transactionStatus}</Text>
                    <Button
                      title="Usar este ATK"
                      onPress={() =>
                        setTransactionAtk(
                          transaction.acquirerTransactionKey || ''
                        )
                      }
                    />
                  </View>
                ))}
              </View>
            ) : (
              <Text style={styles.noTransactions}>
                Nenhuma transação encontrada
              </Text>
            )}
          </View>
        </>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
    textAlign: 'center',
  },
  statusContainer: {
    backgroundColor: '#e0e0e0',
    padding: 10,
    borderRadius: 5,
    marginBottom: 20,
  },
  statusText: {
    fontWeight: 'bold',
    marginBottom: 5,
  },
  statusMessage: {
    fontStyle: 'italic',
  },
  section: {
    backgroundColor: 'white',
    padding: 15,
    borderRadius: 10,
    marginBottom: 20,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 3,
    elevation: 2,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 15,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 5,
    padding: 10,
    marginBottom: 15,
  },
  buttonRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 15,
  },
  button: {
    flex: 1,
    marginHorizontal: 5,
  },
  transactionList: {
    marginTop: 15,
  },
  transactionItem: {
    padding: 10,
    borderWidth: 1,
    borderColor: '#ddd',
    borderRadius: 5,
    marginBottom: 10,
  },
  noTransactions: {
    textAlign: 'center',
    marginTop: 15,
    fontStyle: 'italic',
  },
});

export default Trasaction;
