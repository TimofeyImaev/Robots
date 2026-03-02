package state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppStateManager {
    private static class RegisteredComponent {
        private final String prefix;
        private final StatefulComponent component;

        private RegisteredComponent(String prefix, StatefulComponent component) {
            this.prefix = prefix;
            this.component = component;
        }
    }

    private final StateStorage storage;
    private final List<RegisteredComponent> components = new ArrayList<>();

    public AppStateManager(StateStorage storage) {
        this.storage = storage;
    }

    public AppStateManager() {
        this(new StateStorage());
    }

    public void register(String prefix, StatefulComponent component) {
        components.add(new RegisteredComponent(prefix, component));
    }

    public void loadAll() {
        Map<String, String> globalState = storage.load();
        for (RegisteredComponent rc : components) {
            PrefixedMap prefixedMap = new PrefixedMap(globalState, rc.prefix);
            rc.component.restoreState(prefixedMap);
        }
    }

    public void saveAll() {
        Map<String, String> globalState = new HashMap<>();
        for (RegisteredComponent rc : components) {
            PrefixedMap prefixedMap = new PrefixedMap(globalState, rc.prefix);
            rc.component.saveState(prefixedMap);
        }
        storage.save(globalState);
    }
}
