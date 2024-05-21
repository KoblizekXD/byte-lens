package lol.koblizek.bytelens.api.util;

/**
 * Exception thrown when an error occurs during project any type of project action.
 */
public class ProjectException extends RuntimeException {

    public ProjectException(String message) {
        super(message);
    }

    public ProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectException() {
        super();
    }
}
