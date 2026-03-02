package state;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

class PrefixedEntrySet extends AbstractSet<Map.Entry<String, String>> {
    private final Map<String, String> backingMap;
    private final String prefix;

    PrefixedEntrySet(Map<String, String> backingMap, String prefix) {
        this.backingMap = backingMap;
        this.prefix = prefix;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return new PrefixedEntryIterator(backingMap.entrySet().iterator(), prefix);
    }

    @Override
    public int size() {
        int count = 0;
        for (Map.Entry<String, String> entry : backingMap.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.startsWith(prefix)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void clear() {
        Iterator<Map.Entry<String, String>> it = iterator();
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
}

