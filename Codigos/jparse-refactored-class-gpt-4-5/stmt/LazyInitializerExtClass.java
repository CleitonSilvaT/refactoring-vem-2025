//class extracted from StatementAST

package jparse.stmt;

// TRECHO REFATORADO: Classe extraída para realizar inicialização preguiçosa.
public class LazyInitializer<T> {
    private T instance;
    private final Initializer<T> initializer;

    public LazyInitializer(Initializer<T> initializer) {
        this.initializer = initializer;
    }

    public T get() {
        if (instance == null) {
            instance = initializer.initialize();
        }
        return instance;
    }

    @FunctionalInterface
    public interface Initializer<T> {
        T initialize();
    }
}