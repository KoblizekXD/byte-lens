package lol.koblizek.bytelens.api.util;

import lol.koblizek.bytelens.api.resource.Resource;
import lol.koblizek.bytelens.core.ByteLens;
import org.slf4j.Logger;

/**
 * Provides non-static access to various application instances.
 * This may come useful, as it allows to access the application methods
 * without using {@link ByteLens#getInstance()}.
 */
public interface InstanceAccessor {

    default ByteLens instance() {
        return ByteLens.getInstance();
    }

    default Resource resource(String path) {
        return ByteLens.getInstance().getResourceManager().get(path);
    }

    default Resource jbIcon(String path, boolean isDark) {
        return instance().getResourceManager().getJBIcon(path, isDark);
    }

    default Resource jbIcon(String path) {
        return instance().getResourceManager().getJBIcon(path, true);
    }

    default Logger logger() {
        return instance().getLogger();
    }
}
