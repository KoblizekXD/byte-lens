package lol.koblizek.bytelens.core.utils;

public final class Preconditions {

    private Preconditions() {
        throw new UnsupportedOperationException("Preconditions is a utility class and cannot be instantiated");
    }

    /**
     * Checks whether all objects are non-null.
     * @param objects the objects to check
     * @return whether all objects are non-null
     */
    public static boolean isNonNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return false;
            }
        }
        return true;
    }
}
