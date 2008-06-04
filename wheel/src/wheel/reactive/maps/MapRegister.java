package wheel.reactive.maps;


public interface MapRegister<K, V> {

	MapSignal<K, V> output();

	void put(K key, V value);
	void remove(K key);

}