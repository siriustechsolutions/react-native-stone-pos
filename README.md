




# react-native-stone-smart-pos

## Modulo React Native e Expo com implementação do SDK Stone para terminais smart POS. Para criação de aplicativos para maquininha de cartão de crédito da Stone.

Esta lib pode ser usado em React Native e Expo, está com a versão mais atual do SDK da Stone.

Para mais informações sobre o SDK: https://sdkandroid.stone.com.br/docs/o-que-e-a-sdk-android

OBS> Os fluxos básicos estão funcionando porém ainda existem algumas implementações a serem feitas, você pode implementar e abrir um PR para colaborar com este projeto ou nos solicitar via issues do github.

Nossa ideia é constuir um modelo básico multiadquirente para facilitar a implementação em terminais smart POS.
Além de vários adquirentes e telas prontas o projeto conta com intefaces para impressoras térmicas, NFC, QrCode e muito mais.
Este modulo estará disponível no repo: https://github.com/siriustechsolutions/react-native-pos-multi-acquirer-template

## Agradecimento

Este módulo se baseia no projeto de EightSystems, disponível em:
https://github.com/EightSystems/react-native-stone-pos

## Instalação

```sh
yarn add react-native-stone-smart-pos
```

OBS:
Talvez tenha que reduzir sua targetSdkVersion para a 34.
Talves precise adicionar android.enableJetifier=true no seu gradle.properties.
Por fim pode precisar adicionar no arquivo AndroidManisfet.xml o parametro android:allowBackup="true". 
Na pasta exemplo tem essas e outras configurações que vc pode precisar.


## Exemplo de uso

Exemplo de uso mais extensivo você pode encontrar na pasta [example] presente na raiz deste projeto

OS dados de credenciais seram fornecidos pela Stone durante seu processo de se tornar Stone Partners

No seu projeto será nescessário utilizar a lib [react-native-config](https://www.npmjs.com/package/react-native-config) 

Adicione o token de acesso ao SDK da Stone na variável: STONE_TOKEN=

Você pode passar as demais credenciais por parametro ou simplismente adicionar ao seu arquivo .env e adicionar as seguintes variáveis de ambiente.
STONE_TOKEN=
STONE_APP_NAME=
STONE_QR_CODE_PROVIDER_ID=
STONE_QR_CODE_AUTHORIZATION=


##EXPO: 
Em projeto EXPO, não é nescessário  adicionar a dependencia react-native-config, poise será nescessário adicionar manualmente a referência de repositorio, juntamente com seu Token
Você poderá passar as demais variáveis de ambiente, para inicialização do SDK via process.env ou passar direto na chamada da função

```js
import { StonePosSDK } from 'react-native-stone-smart-pos';

const result = await StonePosSDK.initSDK(
  'APP_NAME',
  'QR_CODE_PROVIDER_ID',
  'QR_CODE_PROVIDER_AUTHORIZATION'
);
```

```js
const transaction = await StonePosSDK.makeTransaction({
  typeOfTransaction: 'CREDIT',
  installmentCount: 1,
  installmentHasInterest: false,
  amountInCents: 100,
  initiatorTransactionKey: `TRANSACTION_${Date.now()}`,
  capture: true,
});
```

## Eventos durantes uma transação

Acompanhe os status de atualização de uma tranção com uso de Listener, a cada atualização a adquirente notifica o status em tempo real
No código de exemplo vc pode obsevar melhor essa implementação.

```js
  /** Eventos de status da transação */
  useEventEmitter((event: any) => {
    console.info('MAKE_TRANSACTION_PROGRESS', event);

    if (event.status === 'TRANSACTION_WAITING_CARD') {
      // Quando solicita para aproximar o cartão
    }

    if (event.status === 'TRANSACTION_SENDING') {
      // Status da transação
    }

    if (event.status === 'TRANSACTION_WAITING_QRCODE_SCAN') {
      // Quando gera o QRCode
    }
  });
```

## Nesta versão a impressão será feita apenas através de uma string HTML, em uma futura terá impressão por uma imagem

# Configuração no Expo - (APENAS EXPO)

Trabalhar com terminais smart POS demandam na maioria das vezes o manuseio de configuração de baixo nível no Android, isso inviabiliza o uso de desenvolvimento com Expo GO.
Portanto, você precisa usar expo-dev-client para expor a pasta android do seu projeto Expo.

TODO - Podemos melhorar isto no futuro:
Adicione o repo ao arquivo /android/build.gradle:

```xml
allprojects {
    repositories {
        maven { url "https://packagecloud.io/priv/SEU_STONE_CODE/stone/pos-android/maven2" }
        maven { url "https://packagecloud.io/priv/SEU_STONE_CODE/stone/pos-android-internal/maven2" }
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
   }
}
```

## Dúvidas

Estamos a disposição para construir juntos ferramentas para soluções financeiras e facilitar a vida dos devs Brasileiros

Saiba mais sobre nós em [Sirius Tech](https://siriustechsolucoes.com)

## License

MIT
