package ru.velesov7493.products.stores;

import java.util.List;

public interface Store<K, V> {

    List<V> findAll();

    V findById(K id);

    boolean save(V value);

    boolean deleteById(K id);
}
