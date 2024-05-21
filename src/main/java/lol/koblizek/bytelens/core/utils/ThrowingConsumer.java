package lol.koblizek.bytelens.core.utils;

@FunctionalInterface
public interface ThrowingConsumer<T> {

    void accept(T t) throws Exception;

    default ThrowingConsumer<T> andThen(ThrowingConsumer<? super T> after) {
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }

    default void run(T t) {
        try {
            accept(t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
