package state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import log.Logger;

public class StateStorage {
    private final File file;

    public StateStorage() {
        this(new File(System.getProperty("user.home"), ".myapp/window-state.properties"));
    }

    public StateStorage(File file) {
        this.file = file;
    }

    public Map<String, String> load() {
        Map<String, String> state = new HashMap<>();
        if (!file.exists()) {
            return state;
        }

        Properties properties = new Properties();
        try (InputStream in = new FileInputStream(file)) {
            properties.load(in);
            for (String name : properties.stringPropertyNames()) {
                state.put(name, properties.getProperty(name));
            }
        } catch (Exception ignored) {
            Logger.debug("Ошибка при загрузке состояния " + ignored.getMessage());
        }

        return state;
    }

    public void save(Map<String, String> state) {
        Properties properties = new Properties();
        properties.putAll(state);

        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (OutputStream out = new FileOutputStream(file)) {
            properties.store(out, "windows");
        } catch (IOException e) {
            Logger.debug("Ошибка при сохранении состояния " + e.getMessage());
        }
    }
}
