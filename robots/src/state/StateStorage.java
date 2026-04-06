package state;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import log.Logger;

public class StateStorage {
    private final Path file;

    public StateStorage() {
        this(Path.of(System.getProperty("user.home"), ".myapp", "window-state.properties"));
    }

    public StateStorage(File file) {
        this(file.toPath());
    }

    public StateStorage(Path file) {
        this.file = file;
    }

    public Map<String, String> load() {
        Map<String, String> state = new HashMap<>();
        if (!Files.exists(file)) {
            return state;
        }

        Properties properties = new Properties();
        try (Reader in = Files.newBufferedReader(file)) {
            properties.load(in);
            for (String name : properties.stringPropertyNames()) {
                state.put(name, properties.getProperty(name));
            }
        } catch (Exception ignored) {
            Logger.debug("ошибка при загрузке" + ignored.getMessage());
        }

        return state;
    }

    public void save(Map<String, String> state) {
        Properties properties = new Properties();
        properties.putAll(state);

        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            Logger.debug("ошибка создания папки" + e.getMessage());
            return;
        }

        try (Writer out = Files.newBufferedWriter(file)) {
            properties.store(out, null);
        } catch (IOException e) {
            Logger.debug("ошибка при сохранении состояния " + e.getMessage());
        }
    }
}
