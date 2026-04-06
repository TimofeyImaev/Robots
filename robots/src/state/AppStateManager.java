package state;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppStateManager {
    private final StateStorage storage;
    private final List<RegisteredComponent> components = new ArrayList<>();

    public AppStateManager(StateStorage storage) {
        this.storage = storage;
    }

    public AppStateManager() {
        this(new StateStorage());
    }

    public void register(String prefix, Component component) {
        components.add(new RegisteredComponent(prefix, component));
    }

    public void loadAll() {
        Map<String, String> globalState = storage.load();
        for (RegisteredComponent rc : components) {
            WindowStateMapper.restoreWindowState(globalState, rc.prefix(), rc.component());
        }
    }

    public void saveAll() {
        Map<String, String> globalState = new HashMap<>();
        for (RegisteredComponent rc : components) {
            WindowStateMapper.saveWindowState(globalState, rc.prefix(), rc.component());
        }
        storage.save(globalState);
    }
}
