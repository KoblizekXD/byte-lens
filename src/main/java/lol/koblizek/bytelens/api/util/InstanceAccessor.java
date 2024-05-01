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
}
