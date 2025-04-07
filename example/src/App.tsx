import { ScrollView, StyleSheet } from 'react-native';
import Trasaction from './transaction';

export default function App() {
  return (
    <ScrollView contentContainerStyle={styles.scrollContainer}>
      <Trasaction />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  scrollContainer: {
    flexGrow: 1,
  },
});
