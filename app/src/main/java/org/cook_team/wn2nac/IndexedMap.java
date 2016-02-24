package org.cook_team.wn2nac;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class IndexedMap<K,V> extends LinkedHashMap<K,V> {
    ArrayList<K> keys = new ArrayList<>();
    @Override
    public V put(K key,V val) {
        keys.add(key);
        return super.put(key, val);
    }
    @Override
    public V remove(Object key) {
        keys.remove(key);
        return super.remove(key);
    }
    @Override
    public void clear() {
        keys.clear();
        super.clear();
    }
    public V get(int i){ return super.get(keys.get(i)); }
    public K getKey(int i){ return keys.get(i); }
    public V getLast(){ return keys.size() > 0 ? super.get(keys.get(keys.size()-1)) : null; }
    public K getLastKey(){ return keys.size() > 0 ? keys.get(keys.size() - 1) : null; }
}
