package state;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PrefixedMap extends AbstractMap<String, String> {
    private final Map<String, String> backingMap;
    private final String prefix;

    public PrefixedMap(Map<String, String> backingMap, String prefix) {
        this.backingMap = Objects.requireNonNull(backingMap, "backingMap");
        this.prefix = Objects.requireNonNull(prefix, "prefix");
    }

    String getPrefix() {
        return prefix;
    }

    Map<String, String> getBackingMap() {
        return backingMap;
    }

    private String toGlobalKey(Object key) {
        if (key == null) {
            return null;
        }
        return prefix + key;
    }

    @Override
    public String put(String key, String value) {
        return backingMap.put(toGlobalKey(key), value);
    }

    @Override
    public String get(Object key) {
        return backingMap.get(toGlobalKey(key));
    }

    @Override
    public boolean containsKey(Object key) {
        return backingMap.containsKey(toGlobalKey(key));
    }

    @Override
    public String remove(Object key) {
        return backingMap.remove(toGlobalKey(key));
    }

    @Override
    public void clear() {
        new PrefixedEntrySet(backingMap, prefix).clear();
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return new PrefixedEntrySet(backingMap, prefix);
    }
}
