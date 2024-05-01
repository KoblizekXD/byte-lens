package lol.koblizek.bytelens.api.util;

import lol.koblizek.bytelens.api.resource.Resource;
import lol.koblizek.bytelens.core.ByteLens;

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
}
