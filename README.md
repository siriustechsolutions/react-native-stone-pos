# react-native-stone-pos TODO - EM DESENVOLVIMENTO

Lib React Native e Expo para expor o SDK das maquininhas de cartão da Stone

## Installation

```sh
npm install react-native-stone-pos
```

Instale a lib react native config
apply from: project(':react-native-config').projectDir.getPath() + "/dotenv.gradle"
crie um arquivo .env na raiz do seu projeto e add STONE_TOKEN=

talvez tenha que reduzir sua targetSdkVersion para a 34
talves adicione android.enableJetifier=true no seu gradle.properties
no seu AndroidManisfet.xml adicione, android:allowBackup="true". Na pasta exemplo tem outras configuraç~eos que vc pode precisar

## Usage


```js
import { multiply } from 'react-native-stone-pos';

// ...

const result = multiply(3, 7);
```


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
