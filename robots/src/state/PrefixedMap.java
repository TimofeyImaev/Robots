package state;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PrefixedMap extends AbstractMap<String, String> {
    private final Map<String, String> backingMap;
    private final String prefix;

    public PrefixedMap(Map<String, String> backingMap, String prefix) {
        this.backingMap = Objects.requireNonNull(backingMap);
        this.prefix = Objects.requireNonNull(prefix);
    }

    private String fullKey(Object key) {
        return key == null ? null : prefix + key;
    }

    @Override public String put(String key, String value) { return backingMap.put(fullKey(key), value); }
    @Override public String get(Object key) { return backingMap.get(fullKey(key)); }
    @Override public boolean containsKey(Object key) { return backingMap.containsKey(fullKey(key)); }
    @Override public String remove(Object key) { return backingMap.remove(fullKey(key)); }

    @Override
    public void clear() {
        for (String k : backingMap.keySet().toArray(new String[0])) {
            if (k != null && k.startsWith(prefix)) {
                backingMap.remove(k);
            }
        }
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        Set<Entry<String, String>> result = new HashSet<>();
        for (Entry<String, String> e : backingMap.entrySet()) {
            String globalKey = e.getKey();
            if (globalKey != null && globalKey.startsWith(prefix)) {
                String localKey = globalKey.substring(prefix.length());
                result.add(new AbstractMap.SimpleEntry<>(localKey, e.getValue()));
            }
        }
        return result;
    }
}
