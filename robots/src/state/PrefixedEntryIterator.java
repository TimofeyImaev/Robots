package state;

import java.util.Iterator;
import java.util.Map;

class PrefixedEntryIterator implements Iterator<Map.Entry<String, String>> {
    private final Iterator<Map.Entry<String, String>> backingIterator;
    private final String prefix;
    private Map.Entry<String, String> nextEntry;

    PrefixedEntryIterator(Iterator<Map.Entry<String, String>> backingIterator, String prefix) {
        this.backingIterator = backingIterator;
        this.prefix = prefix;
    }

    private void findNext() {
        while (nextEntry == null && backingIterator.hasNext()) {
            Map.Entry<String, String> candidate = backingIterator.next();
            String key = candidate.getKey();
            if (key != null && key.startsWith(prefix)) {
                nextEntry = candidate;
            }
        }
    }

    @Override
    public boolean hasNext() {
        if (nextEntry == null) {
            findNext();
        }
        return nextEntry != null;
    }

    @Override
    public Map.Entry<String, String> next() {
        if (nextEntry == null) {
            findNext();
        }
        Map.Entry<String, String> current = nextEntry;
        nextEntry = null;
        return new PrefixedEntry(current, prefix);
    }

    @Override
    public void remove() {
        backingIterator.remove();
    }

    private static class PrefixedEntry implements Map.Entry<String, String> {
        private final Map.Entry<String, String> backingEntry;
        private final String prefix;

        PrefixedEntry(Map.Entry<String, String> backingEntry, String prefix) {
            this.backingEntry = backingEntry;
            this.prefix = prefix;
        }

        @Override
        public String getKey() {
            String globalKey = backingEntry.getKey();
            if (globalKey != null && globalKey.startsWith(prefix)) {
                return globalKey.substring(prefix.length());
            }
            return null;
        }

        @Override
        public String getValue() {
            return backingEntry.getValue();
        }

        @Override
        public String setValue(String value) {
            return backingEntry.setValue(value);
        }
    }
}

