package loaders;

public interface DataLoader<T> {
    T load(String file) throws Exception;
}
