package lol.koblizek.bytelens.core.utils;

/**
 * Utility class for checking preconditions.
 */
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

    /**
     * Checks whether any of the objects is null.
     * @param objects the objects to check
     * @return whether any of the objects is null
     */
    public static boolean isAnyNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether all objects are non-null.
     * @param objects the objects to check
     * @throws NullPointerException if any of the objects is null
     */
    public static void nonNull(Object... objects) {
        if (isAnyNull(objects)) {
            throw new NullPointerException("One or more objects are null");
        }
    }
}
