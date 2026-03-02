package state;

import java.util.Map;

public interface StatefulComponent {
    void saveState(Map<String, String> state);

    void restoreState(Map<String, String> state);
}
